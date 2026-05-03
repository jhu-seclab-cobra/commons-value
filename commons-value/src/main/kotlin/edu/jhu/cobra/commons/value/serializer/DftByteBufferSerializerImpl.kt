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
object DftByteBufferSerializerImpl : IValSerializer<ByteBuffer> {

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
    override fun serialize(value: IValue): ByteBuffer = when (value) {
        is NullVal -> byteBufferOf(Type.NULL.byte)
        is StrVal -> {
            val strCore = value.core.toByteArray()
            val bufferLen = 1 + 4 + strCore.size
            ByteBuffer.allocate(bufferLen).put(Type.STR.byte).putInt(strCore.size).put(strCore).typedFlip()
        }
        is BoolVal -> byteBufferOf(if (value.core) Type.BOOL_TRUE.byte else Type.BOOL_FALSE.byte)
        is Unsure -> byteBufferOf(
            when (value) {
                Unsure.NUM -> Type.UNSURE_NUM.byte
                Unsure.STR -> Type.UNSURE_STR.byte
                Unsure.BOOL -> Type.UNSURE_BOOL.byte
                else -> Type.UNSURE_ANY.byte
            }
        )

        is IntVal -> ByteBuffer.allocate(9).put(Type.INT.byte).putLong(value.core).typedFlip()
        is FloatVal -> ByteBuffer.allocate(9).put(Type.FLOAT.byte).putDouble(value.core).typedFlip()

        is RangeVal -> {
            val startBuf = serialize(value.start)
            val endBuf = serialize(value.endInclusive)
            val buf = ByteBuffer.allocate(1 + startBuf.limit() + endBuf.limit()).put(Type.RANGE.byte)
            buf.put(startBuf).put(endBuf).typedFlip()
        }

        is ListVal -> { // 1 byte type | count | element1 | element2 | ...
            val allElements = value.map { element -> serialize(element) }
            val bufferSize = 1 + 4 + allElements.sumOf { array -> array.limit() }
            val buffer = ByteBuffer.allocate(bufferSize).put(Type.LIST).putInt(allElements.size)
            allElements.forEach { buffer.put(it) }
            buffer.typedFlip()
        }

        is SetVal -> { // 1 byte type | count | element1 | element2 | ...
            val allElements = value.map { element -> serialize(element) }
            val bufferSize = 1 + 4 + allElements.sumOf { array -> array.limit() }
            val buffer = ByteBuffer.allocate(bufferSize).put(Type.SET).putInt(allElements.size)
            allElements.forEach { buffer.put(it) }
            buffer.typedFlip()
        }

        is MapVal -> { // 1 byte type | count | size_keyN | keyN | size_valueN | valueN
            val elements = value.map { (k, v) -> k.toByteArray() to serialize(v) }
            val bufferLength = 1 + 4 + elements.sumOf { (k, v) -> 4 + k.size + v.limit() }
            val buffer = ByteBuffer.allocate(bufferLength).put(Type.MAP).putInt(value.size)
            elements.forEach { (k, v) -> buffer.putInt(k.size).put(k).put(v) }
            buffer.typedFlip()
        }

        else -> throw IllegalArgumentException("Unknown value type: $value")
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
        if (material.limit() == 0) throw IllegalArgumentException("Empty byte buffer")
        return when (val type = material.get()) {
            Type.NULL.byte -> NullVal
            Type.STR.byte -> StrVal(material.getString())
            Type.BOOL_TRUE.byte -> BoolVal.T
            Type.BOOL_FALSE.byte -> BoolVal.F
            Type.INT.byte -> IntVal(material.getLong())
            Type.FLOAT.byte -> FloatVal(material.getDouble())
            Type.NUM_BYTE.byte -> IntVal(material.get().toLong())
            Type.NUM_SHORT.byte -> IntVal(material.getShort().toLong())
            Type.NUM_INT.byte -> IntVal(material.getInt().toLong())
            Type.NUM_LONG.byte -> IntVal(material.getLong())
            Type.NUM_FLOAT.byte -> FloatVal(material.getFloat().toDouble())
            Type.NUM_DOUBLE.byte -> FloatVal(material.getDouble())
            Type.NUM_OTHERS.byte -> material.getString().asNumber().toIntOrFloatVal()
            Type.UNSURE_ANY.byte -> Unsure.ANY
            Type.UNSURE_NUM.byte -> Unsure.NUM
            Type.UNSURE_STR.byte -> Unsure.STR
            Type.UNSURE_BOOL.byte -> Unsure.BOOL
            Type.RANGE.byte -> RangeVal(start = deserialize(material) as IntVal, endInclusive = deserialize(material) as IntVal)
            Type.LIST.byte -> { // count | element1 | element2 | ...
                val listDataCount = material.getInt()
                val container = ListVal(size = listDataCount)
                repeat(listDataCount) {
                    val element = deserialize(material)
                    container.plusAssign(value = element)
                }
                container
            }

            Type.SET.byte -> { // count | element1 | element2 | ...
                val setDataCount = material.getInt()
                val container = SetVal(size = setDataCount)
                repeat(setDataCount) {
                    val element = deserialize(material)
                    container.plusAssign(value = element)
                }
                container // Return the container with all elements
            }

            Type.MAP.byte -> { // cnt | keyN | valueN
                val mapElementsCount = material.getInt()
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
}