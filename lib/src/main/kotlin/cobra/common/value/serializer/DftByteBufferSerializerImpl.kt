package cobra.common.value.serializer

import cobra.common.value.*
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
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * val buffer = DftByteBufferSerializerImpl.serialize(numVal)
     * println(buffer) // Outputs the serialized ByteBuffer
     * ```
     *
     * @param value The [IValue] instance to serialize.
     * @return A [ByteBuffer] containing the serialized representation of the value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): ByteBuffer = when (value) {
        // 1 byte type 
        is NullVal -> byteBufferOf(Type.NULL.byte)
        // 1 byte type | 1 byte for the string length | bytes for the string
        is StrVal -> {
            val strCore = value.core.toByteArray()
            val bufferLen = 1 + 4 + strCore.size
            ByteBuffer.allocate(bufferLen).put(Type.STR.byte).putInt(strCore.size).put(strCore).flip()
        }
        // 1 byte type | 1 byte for the boolean value
        is BoolVal -> byteBufferOf(if (value.core) Type.BOOL_TRUE.byte else Type.BOOL_FALSE.byte)
        // 1 byte type
        is Unsure -> byteBufferOf(
            when (value) {
                Unsure.NUM -> Type.UNSURE_NUM.byte
                Unsure.STR -> Type.UNSURE_STR.byte
                Unsure.BOOL -> Type.UNSURE_BOOL.byte
                else -> Type.UNSURE_ANY.byte
            }
        )

        is NumVal -> when (val num = value.core) {
            is Byte -> ByteBuffer.allocate(2).put(Type.NUM_BYTE.byte).put(num).flip()
            is Short -> ByteBuffer.allocate(3).put(Type.NUM_SHORT.byte).putShort(num).flip()
            is Int -> ByteBuffer.allocate(5).put(Type.NUM_INT.byte).putInt(num).flip()
            is Long -> ByteBuffer.allocate(9).put(Type.NUM_LONG.byte).putLong(num).flip()
            is Float -> ByteBuffer.allocate(5).put(Type.NUM_FLOAT.byte).putFloat(num).flip()
            is Double -> ByteBuffer.allocate(9).put(Type.NUM_DOUBLE.byte).putDouble(num).flip()
            else -> byteBufferOf(Type.NUM_OTHERS.byte, *num.toString().toByteArray())
        }

        is RangeVal -> { // 1 byte type | element1 | element2
            val rangeBuffer = ByteBuffer.allocate(1 + 4 + 4).put(Type.RANGE.byte)
            rangeBuffer.putInt(value.first.toInt()).putInt(value.last.toInt()).flip()
        }

        is ListVal -> { // 1 byte type | count | element1 | element2 | ...
            val allElements = value.map { element -> serialize(element) }
            val bufferSize = 1 + 4 + allElements.sumOf { array -> array.limit() }
            val buffer = ByteBuffer.allocate(bufferSize).put(Type.LIST).putInt(allElements.size)
            allElements.fold(buffer) { acc, element -> acc.put(element) }.flip()
        }

        is SetVal -> { // 1 byte type | count | element1 | element2 | ...
            val allElements = value.map { element -> serialize(element) }
            val bufferSize = 1 + 4 + allElements.sumOf { array -> array.limit() }
            val buffer = ByteBuffer.allocate(bufferSize).put(Type.SET).putInt(allElements.size)
            allElements.fold(buffer) { acc, element -> acc.put(element) }.flip()
        }

        is MapVal -> { // 1 byte type | count | size_keyN | keyN | size_valueN | valueN
            val elements = value.map { (k, v) -> k.toByteArray() to serialize(v) }
            val bufferLength = 1 + 4 + elements.sumOf { (k, v) -> 4 + k.size + v.limit() }
            val buffer = ByteBuffer.allocate(bufferLength).put(Type.MAP).putInt(value.size)
            elements.forEach { (k, v) -> buffer.putInt(k.size).put(k).put(v) }
            buffer.flip()
        }

        else -> throw IllegalArgumentException("Unknown value type: $value")
    }

    /**
     * Deserializes a [ByteBuffer] into an [IValue] instance.
     *
     * This method reconstructs an [IValue] from a binary format previously produced by [serialize].
     * Supported types include null, strings, booleans, numeric values, ranges, lists, sets, and maps.
     *
     * Example usage:
     * ```kotlin
     * val buffer = ByteBuffer.wrap(byteArrayOf(Type.NUM_INT.byte, 0, 0, 0, 42))
     * val value = DftByteBufferSerializerImpl.deserialize(buffer)
     * println(value) // Outputs: NumVal{42}
     * ```
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
            Type.NUM_BYTE.byte -> NumVal(material.get())
            Type.NUM_SHORT.byte -> NumVal(material.getShort())
            Type.NUM_INT.byte -> NumVal(material.getInt())
            Type.NUM_LONG.byte -> NumVal(material.getLong())
            Type.NUM_FLOAT.byte -> NumVal(material.getFloat())
            Type.NUM_DOUBLE.byte -> NumVal(material.getDouble())
            Type.NUM_OTHERS.byte -> NumVal(material.getString().asNumber())
            Type.UNSURE_ANY.byte -> Unsure.ANY
            Type.UNSURE_NUM.byte -> Unsure.NUM
            Type.UNSURE_STR.byte -> Unsure.STR
            Type.UNSURE_BOOL.byte -> Unsure.BOOL
            // element1 | element2
            Type.RANGE.byte -> RangeVal(start = material.getInt(), endInclude = material.getInt())
            Type.LIST.byte -> { // count | element1 | element2 | ...
                val listDataCount = material.getInt()
                val container = ListVal(size = listDataCount)
                repeat(listDataCount) {
                    val element = deserialize(material)
                    container.plusAssign(value = element)
                }
                container.core.toMutableSet()
                container // Return the container with all elements
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