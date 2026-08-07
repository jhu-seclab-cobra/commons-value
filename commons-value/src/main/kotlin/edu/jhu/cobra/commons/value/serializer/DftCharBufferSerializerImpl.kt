package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import java.nio.CharBuffer

/**
 * A serializer implementation for [IValue] instances that handles serialization and deserialization
 * to and from [CharBuffer]. This implementation uses a string-based format for debugging purposes
 * or simple persistence scenarios, where a human-readable representation is beneficial.
 *
 * The serialization process converts [IValue] instances into a [CharBuffer], encoding their type
 * and content in a structured textual format. The deserialization process reconstructs [IValue]
 * instances from their serialized representation.
 */
public object DftCharBufferSerializerImpl : IValSerializer<CharBuffer> {
    /**
     * Serializes an [IValue] instance into a [CharBuffer].
     *
     * The serialization format is type-specific; length and count prefixes are hexadecimal:
     * - Null values: `Null:`
     * - Strings: `Str:<hex length>:<value>`
     * - Booleans: `True:` or `False:`
     * - Integers: `IntV:<value>:`
     * - Floats: `FloatV:<value>:`
     * - Ranges: `Range:<start>,<end>:`
     * - Lists: `List:<hex count>:<element1>,<element2>,...:`
     * - Sets: `Set:<hex count>:<element1>,<element2>,...:`
     * - Maps: `Map:<hex count>:<key1>=<value1>,<key2>=<value2>,...:`
     * - Unsure types: `UnANY:`, `UnSTR:`, `UnNUM:`, or `UnBOOL:`
     *
     * @param value The [IValue] instance to serialize.
     * @return A [CharBuffer] containing the serialized representation of the value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): CharBuffer =
        when (value) {
            is NullVal -> "${Type.NULL.str}:".asCharBuffer()
            is Unsure ->
                when (value) {
                    Unsure.NUM -> "${Type.UNSURE_NUM.str}:".asCharBuffer()
                    Unsure.STR -> "${Type.UNSURE_STR.str}:".asCharBuffer()
                    Unsure.BOOL -> "${Type.UNSURE_BOOL.str}:".asCharBuffer()
                    else -> "${Type.UNSURE_ANY.str}:".asCharBuffer()
                }
            is StrVal -> {
                val hexCnt = value.length.asHexString()
                val string = "${Type.STR.str}:$hexCnt:${value.core}"
                string.asCharBuffer()
            }
            is BoolVal ->
                if (value.isTrue()) {
                    "${Type.BOOL_TRUE.str}:".asCharBuffer()
                } else {
                    "${Type.BOOL_FALSE.str}:".asCharBuffer()
                }
            is IntVal -> "${Type.INT.str}:${value.core}:".asCharBuffer()
            is FloatVal -> "${Type.FLOAT.str}:${value.core}:".asCharBuffer()

            is RangeVal -> "${Type.RANGE.str}:${value.first},${value.last}:".asCharBuffer()
            is ListVal -> containerToBuffer(Type.LIST, value.map { element -> serialize(element) })

            is SetVal -> containerToBuffer(Type.SET, value.map { element -> serialize(element) })

            is MapVal -> { // mapType:cnt_hex{key=element, key=element, key=element}
                val elements = value.map { (k, v) -> serialize(StrVal(k)) to serialize(v) }
                val eleCount = value.size.asHexString() // the counter for ele
                val eleLength = elements.sumOf { (k, v) -> k.length + v.length + 2 }
                val charBuffer = CharBuffer.allocate(Type.MAP.str.length + 2 + eleCount.length + eleLength)
                charBuffer
                    .put(Type.MAP.str)
                    .put(':')
                    .put(eleCount)
                    .put(':') // mapType:cnt:
                elements.forEach { (k, v) ->
                    charBuffer
                        .put(k)
                        .put('=')
                        .put(v)
                        .put(',')
                } // key=value,...
                charBuffer.typedPosition(charBuffer.position() - 1).put(':').typedFlip() // }
            }
        }

    // LIST and SET share one container layout: type:cnt_hex:element,element,...:
    private fun containerToBuffer(
        type: Type,
        elements: List<CharBuffer>,
    ): CharBuffer {
        val eleCount = elements.size.asHexString()
        val eleLength = elements.sumOf { ele -> ele.length + 1 }
        val charBuffer = CharBuffer.allocate(type.str.length + 2 + eleCount.length + eleLength)
        charBuffer
            .put(type.str)
            .put(':')
            .put(eleCount)
            .put(':') // type:cnt:
        elements.forEach { element -> charBuffer.put(element).put(',') } // element,element,...
        return charBuffer.typedPosition(charBuffer.position() - 1).put(':').typedFlip()
    }

    /**
     * Deserializes a [CharBuffer] into an [IValue] instance.
     *
     * This method parses the serialized format produced by [serialize] and reconstructs the original [IValue] instance.
     * The deserialization process is type-specific and supports all formats documented in [serialize].
     *
     * @param material The [CharBuffer] containing the serialized representation of a value.
     * @return The deserialized [IValue] instance.
     * @throws IllegalArgumentException If the material contains an unknown or unsupported type identifier.
     */
    override fun deserialize(material: CharBuffer): IValue =
        when (val type = material.getString(':')) {
            // nullType:
            Type.NULL.str -> NullVal
            // strType:cnt{}
            Type.STR.str -> {
                val strLength = checkSizePrefix(material.getString(until = ':').asHexInt(), material.remaining(), "string size")
                val stringCore = material.getString(size = strLength)
                StrVal(core = stringCore)
            }

            Type.BOOL_TRUE.str -> BoolVal.T
            Type.BOOL_FALSE.str -> BoolVal.F
            Type.INT.str -> IntVal(material.getString(':').toLong())
            Type.FLOAT.str -> FloatVal(material.getString(':').toDouble())
            Type.UNSURE_NUM.str -> Unsure.NUM
            Type.UNSURE_STR.str -> Unsure.STR
            Type.UNSURE_BOOL.str -> Unsure.BOOL
            Type.UNSURE_ANY.str -> Unsure.ANY
            Type.RANGE.str -> { // range_type:num,num
                val start = material.getString(',').toLong()
                val endInclude = material.getString(':').toLong()
                RangeVal(start, endInclude) // handle the start and endInclude
            }

            Type.LIST.str -> { // list_type:hex_cnt{element, element,...}
                val eleCount = readContainerCount(material)
                ListVal(size = eleCount).also { list -> readContainerElements(material, eleCount) { list.plusAssign(it) } }
            }

            Type.SET.str -> { // set_type:hex_cnt{element, element,...}
                val eleCount = readContainerCount(material)
                SetVal(size = eleCount).also { set -> readContainerElements(material, eleCount) { set.plusAssign(it) } }
            }

            Type.MAP.str -> { // mapType:hex_cnt{key=value,key=value,...}
                val eleCount = checkSizePrefix(material.getString(':').asHexInt(), material.remaining(), "entry count")
                val container = MapVal(size = eleCount)
                repeat(eleCount) {
                    val key = deserialize(material) as StrVal
                    material.get() // remove the delimiter '='
                    val value = deserialize(material)
                    material.get() // remove the delimiter ',' or ':'
                    container[key.core] = value
                }
                container // return the final container of the map out
            }

            else -> throw IllegalArgumentException("Unknown type: $type")
        }

    // LIST and SET decode share one validated hex element-count prefix.
    private fun readContainerCount(material: CharBuffer): Int =
        checkSizePrefix(material.getString(':').asHexInt(), material.remaining(), "element count")

    // LIST and SET decode share one delimiter-separated element loop.
    private inline fun readContainerElements(
        material: CharBuffer,
        count: Int,
        action: (IValue) -> Unit,
    ) {
        repeat(count) {
            action(deserialize(material))
            material.get() // remove the end delimiter
        }
    }
}
