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
import java.nio.ByteBuffer

/**
 * A serializer implementation of [IValSerializer] for [IValue] instances that handles serialization
 * and deserialization to and from [ByteBuffer].
 * This implementation is optimized for working with compact binary formats and supports a wide range
 * of value types including primitives, collections, and compound objects.
 *
 * The serialization process encodes [IValue] instances into a [ByteBuffer], enabling efficient storage
 * and transmission, while the deserialization process reconstructs the original [IValue] objects.
 *
 * This serializer ensures compatibility with all supported [IValue] subtypes and provides robust
 * handling of both basic and complex data structures.
 */
public object DftByteBufferSerializerImpl : IValSerializer<ByteBuffer> {
    /**
     * Serializes an [IValue] instance into a [ByteBuffer].
     *
     * This method encodes the given [IValue] into a binary format suitable for storage or transmission.
     * Supported value types include null, strings, booleans, numeric values, ranges, lists, sets, and maps.
     *
     * @param value The [IValue] instance to serialize.
     * @return A [ByteBuffer] containing the serialized representation of the value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): ByteBuffer =
        when (value) {
            is NullVal -> byteBufferOf(Type.NULL.byte)
            is StrVal -> {
                val strCore = value.core.toByteArray()
                val bufferLen = TYPE_TAG_BYTES + SIZE_PREFIX_BYTES + strCore.size
                ByteBuffer
                    .allocate(bufferLen)
                    .put(Type.STR.byte)
                    .putInt(strCore.size)
                    .put(strCore)
                    .typedFlip()
            }
            is BoolVal -> byteBufferOf(if (value.core) Type.BOOL_TRUE.byte else Type.BOOL_FALSE.byte)
            is Unsure ->
                byteBufferOf(
                    when (value) {
                        Unsure.NUM -> Type.UNSURE_NUM.byte
                        Unsure.STR -> Type.UNSURE_STR.byte
                        Unsure.BOOL -> Type.UNSURE_BOOL.byte
                        else -> Type.UNSURE_ANY.byte
                    },
                )

            is IntVal ->
                ByteBuffer
                    .allocate(TAGGED_LONG_BYTES)
                    .put(Type.INT.byte)
                    .putLong(value.core)
                    .typedFlip()
            is FloatVal ->
                ByteBuffer
                    .allocate(TAGGED_LONG_BYTES)
                    .put(Type.FLOAT.byte)
                    .putDouble(value.core)
                    .typedFlip()

            is RangeVal -> {
                ByteBuffer
                    .allocate(TYPE_TAG_BYTES + 2 * TAGGED_LONG_BYTES)
                    .put(Type.RANGE.byte)
                    .put(Type.INT.byte)
                    .putLong(value.start.core)
                    .put(Type.INT.byte)
                    .putLong(value.endInclusive.core)
                    .typedFlip()
            }

            is ListVal -> containerToBuffer(Type.LIST, value.map { element -> serialize(element) })

            is SetVal -> containerToBuffer(Type.SET, value.map { element -> serialize(element) })

            is MapVal -> { // 1 byte type | count | size_keyN | keyN | size_valueN | valueN
                val elements = value.map { (k, v) -> k.toByteArray() to serialize(v) }
                val bufferLength = TYPE_TAG_BYTES + SIZE_PREFIX_BYTES + elements.sumOf { (k, v) -> SIZE_PREFIX_BYTES + k.size + v.limit() }
                val buffer = ByteBuffer.allocate(bufferLength).put(Type.MAP).putInt(value.size)
                elements.forEach { (k, v) -> buffer.putInt(k.size).put(k).put(v) }
                buffer.typedFlip()
            }
        }

    // LIST and SET share one container layout: 1 byte type | count | element1 | element2 | ...
    private fun containerToBuffer(
        type: Type,
        elements: List<ByteBuffer>,
    ): ByteBuffer {
        val bufferSize = TYPE_TAG_BYTES + SIZE_PREFIX_BYTES + elements.sumOf { element -> element.limit() }
        val buffer = ByteBuffer.allocate(bufferSize).put(type).putInt(elements.size)
        elements.forEach { buffer.put(it) }
        return buffer.typedFlip()
    }

    /**
     * Deserializes a [ByteBuffer] into an [IValue] instance.
     *
     * This method reconstructs an [IValue] from a binary format previously produced by [serialize].
     * Supported types include null, strings, booleans, numeric values, ranges, lists, sets, and maps.
     *
     * @param material The [ByteBuffer] containing the serialized representation of a value.
     * @return The deserialized [IValue] instance.
     * @throws IllegalArgumentException If the material contains an unknown or unsupported type identifier.
     */
    override fun deserialize(material: ByteBuffer): IValue {
        require(material.limit() > 0) { "Empty byte buffer" }
        return when (val type = material.get()) {
            Type.NULL.byte -> NullVal
            Type.STR.byte -> StrVal(material.getString())
            Type.BOOL_TRUE.byte -> BoolVal.T
            Type.BOOL_FALSE.byte -> BoolVal.F
            Type.INT.byte -> IntVal(material.getLong())
            Type.FLOAT.byte -> FloatVal(material.getDouble())
            Type.UNSURE_ANY.byte -> Unsure.ANY
            Type.UNSURE_NUM.byte -> Unsure.NUM
            Type.UNSURE_STR.byte -> Unsure.STR
            Type.UNSURE_BOOL.byte -> Unsure.BOOL
            Type.RANGE.byte ->
                RangeVal(
                    start = requireDecodedType<IntVal>(deserialize(material), "range start"),
                    endInclusive = requireDecodedType<IntVal>(deserialize(material), "range end"),
                )
            Type.LIST.byte -> { // count | element1 | element2 | ...
                val count = readContainerCount(material)
                ListVal(size = count).also { list -> repeat(count) { list.plusAssign(deserialize(material)) } }
            }

            Type.SET.byte -> { // count | element1 | element2 | ...
                val count = readContainerCount(material)
                SetVal(size = count).also { set -> repeat(count) { set.plusAssign(deserialize(material)) } }
            }

            Type.MAP.byte -> { // cnt | keyN | valueN
                val mapElementsCount = checkSizePrefix(material.getInt(), material.remaining(), "entry count")
                val container = MapVal(mapElementsCount)
                repeat(mapElementsCount) {
                    val keyString = material.getString()
                    container[keyString] = deserialize(material)
                }
                container // Return the container with all elements
            }

            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

    // LIST and SET decode share one validated element-count prefix.
    private fun readContainerCount(material: ByteBuffer): Int = checkSizePrefix(material.getInt(), material.remaining(), "element count")
}
