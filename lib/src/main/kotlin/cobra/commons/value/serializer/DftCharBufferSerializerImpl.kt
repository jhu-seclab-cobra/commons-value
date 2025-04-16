package cobra.commons.value.serializer

import cobra.commons.value.*
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
object DftCharBufferSerializerImpl : IValSerializer<CharBuffer> {

    /**
     * Serializes an [IValue] instance into a [CharBuffer].
     *
     * The serialization format is type-specific:
     * - Null values: `NULL:`
     * - Strings: `STR:<length>:<value>`
     * - Booleans: `BOOL_TRUE:` or `BOOL_FALSE:`
     * - Numbers: `NUM_<type>:<value>:`
     * - Ranges: `RANGE:<start>,<end>:`
     * - Lists: `LIST:<count>:<element1>,<element2>,...:`
     * - Sets: `SET:<count>:<element1>,<element2>,...:`
     * - Maps: `MAP:<count>:<key1>=<value1>,<key2>=<value2>,...:`
     * - Unsure types: `UNSURE_<type>:`
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * val serialized = DftCharBufferSerializerImpl.serialize(numVal)
     * println(serialized) // Outputs: NUM_INT:42:
     * ```
     *
     * @param value The [IValue] instance to serialize.
     * @return A [CharBuffer] containing the serialized representation of the value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): CharBuffer = when (value) {
        // nullType:
        is NullVal -> "${Type.NULL.str}:".asCharBuffer()
        // unsureType:
        is Unsure -> when (value) {
            Unsure.NUM -> "${Type.UNSURE_NUM.str}:".asCharBuffer()
            Unsure.STR -> "${Type.UNSURE_STR.str}:".asCharBuffer()
            Unsure.BOOL -> "${Type.UNSURE_BOOL.str}:".asCharBuffer()
            else -> "${Type.UNSURE_ANY.str}:".asCharBuffer()
        }
        // strType:hex_cnt{str}
        is StrVal -> { // add the length counter for the char issues
            val hexCnt = value.length.asHexString()
            val string = "${Type.STR.str}:$hexCnt:${value.core}"
            string.asCharBuffer()
        }
        // boolType:
        is BoolVal ->
            if (value.isTrue()) "${Type.BOOL_TRUE.str}:".asCharBuffer()
            else "${Type.BOOL_FALSE.str}:".asCharBuffer()
        // numType:num}
        is NumVal -> when (val num = value.core) {
            is Byte -> "${Type.NUM_BYTE.str}:${num}:"
            is Short -> "${Type.NUM_SHORT.str}:${num}:"
            is Int -> "${Type.NUM_INT.str}:${num}:"
            is Long -> "${Type.NUM_LONG.str}:${num}:"
            is Float -> "${Type.NUM_FLOAT.str}:${num}:"
            is Double -> "${Type.NUM_DOUBLE.str}:${num}:"
            else -> "${Type.NUM_OTHERS.str}:$num:"
        }.asCharBuffer()

        is RangeVal -> "${Type.RANGE.str}:${value.first.toInt()},${value.last.toInt()}:".asCharBuffer()
        is ListVal -> { // listType:cnt{element,element,...}
            val elements = value.map { element -> serialize(element) }
            val eleCount = value.size.asHexString() // the counter for ele
            val eleLength = elements.sumOf { ele -> ele.length + 1 }
            val charBuffer = CharBuffer.allocate(Type.LIST.str.length + 2 + eleCount.length + eleLength)
            charBuffer.put(Type.LIST.str).put(':').put(eleCount).put(':') // listType:cnt{
            elements.forEach { element -> charBuffer.put(element).put(',') } // element,element,...
            charBuffer.typedPosition(charBuffer.position() - 1).put(':').typedFlip() // }
        }

        is SetVal -> { // setType:cnt_hex:element,element,...:
            val elements = value.map { element -> serialize(element) }
            val eleCount = value.size.asHexString() // the counter for ele
            val eleLength = elements.sumOf { ele -> ele.length + 1 }
            val charBuffer = CharBuffer.allocate(Type.SET.str.length + 2 + eleCount.length + eleLength)
            charBuffer.put(Type.SET.str).put(':').put(eleCount).put(':') // setType:cnt:
            elements.forEach { element -> charBuffer.put(element).put(',') } // element,element,...
            charBuffer.typedPosition(charBuffer.position() - 1).put(':').typedFlip() // }
        }

        is MapVal -> { // mapType:cnt_hex{key=element, key=element, key=element}
            val elements = value.map { (k, v) -> serialize(StrVal(k)) to serialize(v) }
            val eleCount = value.size.asHexString() // the counter for ele
            val eleLength = elements.sumOf { (k, v) -> k.length + v.length + 2 }
            val charBuffer = CharBuffer.allocate(Type.MAP.str.length + 2 + eleCount.length + eleLength)
            charBuffer.put(Type.MAP.str).put(':').put(eleCount).put(':') // mapType:cnt:
            elements.forEach { (k, v) -> charBuffer.put(k).put('=').put(v).put(',') } //key=value,...
            charBuffer.typedPosition(charBuffer.position() - 1).put(':').typedFlip() // }
        }

        else -> throw IllegalArgumentException("Unknown type: $value")
    }

    /**
     * Deserializes a [CharBuffer] into an [IValue] instance.
     *
     * This method parses the serialized format produced by [serialize] and reconstructs the original [IValue] instance.
     * The deserialization process is type-specific and supports all formats documented in [serialize].
     *
     * Example usage:
     * ```kotlin
     * val buffer = "NUM_INT:42:".asCharBuffer()
     * val value = DftCharBufferSerializerImpl.deserialize(buffer)
     * println(value) // Outputs: NumVal{42}
     * ```
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
                val strLength = material.getString(until = ':').asHexInt()
                val stringCore = material.getString(size = strLength)
                StrVal(core = stringCore)
            }

            Type.BOOL_TRUE.str -> BoolVal.T
            Type.BOOL_FALSE.str -> BoolVal.F
            Type.NUM_BYTE.str -> NumVal(material.getString(':').toByte())
            Type.NUM_SHORT.str -> NumVal(material.getString(':').toShort())
            Type.NUM_INT.str -> NumVal(material.getString(':').toInt())
            Type.NUM_LONG.str -> NumVal(material.getString(':').toLong())
            Type.NUM_FLOAT.str -> NumVal(material.getString(':').toFloat())
            Type.NUM_DOUBLE.str -> NumVal(material.getString(':').toDouble())
            Type.NUM_OTHERS.str -> NumVal(material.getString(':').asNumber())
            Type.UNSURE_NUM.str -> Unsure.NUM
            Type.UNSURE_STR.str -> Unsure.STR
            Type.UNSURE_BOOL.str -> Unsure.BOOL
            Type.UNSURE_ANY.str -> Unsure.ANY
            Type.RANGE.str -> { // range_type:num,num
                val start = material.getString(',').toInt()
                val endInclude = material.getString(':').toInt()
                RangeVal(start, endInclude) // handle the start and endInclude
            }

            Type.LIST.str -> { // list_type:hex_cnt{element, element,...}
                val eleCount = material.getString(':').asHexInt()
                val container = ListVal(size = eleCount) // the final container
                repeat(eleCount) {
                    container.plusAssign(deserialize(material))
                    material.get() // remove the end delimiter
                }
                container // return the final container of the list out
            }

            Type.SET.str -> { // set_type:hex_cnt{element, element,...}
                val eleCount = material.getString(':').asHexInt()
                val container = SetVal(size = eleCount) // the final container
                repeat(eleCount) {
                    container.plusAssign(deserialize(material))
                    material.get() // remove the end delimiter
                }
                container // return the final container of the set out
            }

            Type.MAP.str -> { // mapType:hex_cnt{key=value,key=value,...}
                val eleCount = material.getString(':').asHexInt()
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
}