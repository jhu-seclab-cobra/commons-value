package cobra.common.value.serializer

import cobra.common.value.*
import org.apache.commons.lang3.math.NumberUtils
import java.nio.ByteBuffer


/**
 * A serializer implementation of [IValSerializer] for [IValue] instances that provides serialization
 * and deserialization of various value types into byte arrays. This implementation supports primitive values,
 * collections, and complex types, enabling compact and efficient encoding for storage or transmission.
 *
 * This serializer is designed to handle all supported [IValue] subtypes, providing methods to serialize
 * them into byte arrays and reconstruct them from those arrays.
 */
object DftByteArraySerializerImpl : IValSerializer<ByteArray> {

    /**
     * Serializes an [IValue] instance into a byte array.
     *
     * This method encodes the given value into a format that can be transmitted or stored,
     * preserving its type and content. Supported types include null, strings, booleans, numeric values,
     * lists, sets, maps, ranges, and uncertain values.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * val serialized = DftByteArraySerializerImpl.serialize(numVal)
     * println(serialized.joinToString(", ") { it.toString() }) // Outputs the byte array
     * ```
     *
     * @param value The [IValue] instance to serialize.
     * @return A byte array representing the serialized value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): ByteArray = when (value) {
        is NullVal -> byteArrayOf(Type.NULL.byte)
        is StrVal -> byteArrayOf(Type.STR.byte, *value.core.toByteArray())
        is BoolVal -> byteArrayOf(Type.BOOL.byte, if (value.core) 1 else 0)
        is Unsure -> when (value) {
            Unsure.NUM -> byteArrayOf(Type.UNSURE_NUM.byte)
            Unsure.STR -> byteArrayOf(Type.UNSURE_STR.byte)
            Unsure.BOOL -> byteArrayOf(Type.UNSURE_BOOL.byte)
            else -> byteArrayOf(Type.UNSURE_ANY.byte)
        }

        is NumVal -> when (val num = value.core) {
            is Byte -> byteArrayOf(Type.NUM_BYTE.byte, num)
            is Short -> ByteBuffer.allocate(3).put(Type.NUM_SHORT.byte).putShort(num).array()
            is Int -> ByteBuffer.allocate(5).put(Type.NUM_INT.byte).putInt(num).array()
            is Long -> ByteBuffer.allocate(9).put(Type.NUM_LONG.byte).putLong(num).array()
            is Float -> ByteBuffer.allocate(5).put(Type.NUM_FLOAT.byte).putFloat(num).array()
            is Double -> ByteBuffer.allocate(9).put(Type.NUM_DOUBLE.byte).putDouble(num).array()
            else -> byteArrayOf(Type.NUM_OTHERS.byte, *num.toString().toByteArray())
        }

        is RangeVal -> { // type | first size | first bytes | second bytes
            val (firstBytes, secondBytes) = serialize(value.core[0]) to serialize(value.core[1])
            val buffer = ByteBuffer.allocate(1 + 4 + firstBytes.size + secondBytes.size)
            buffer.apply { put(Type.RANGE.byte); putInt(firstBytes.size); put(firstBytes); put(secondBytes) }.array()
        }

        is ListVal -> { // type | size1 | element1 | size2 | element2 | ...
            val listBytes = value.map { serialize(it) } // serialize each element
            val bufferSize = 1 + listBytes.sumOf { 4 + it.size }
            val buffer = ByteBuffer.allocate(bufferSize)
            buffer.put(Type.LIST.byte) // list type indicator
            listBytes.forEach { buffer.putInt(it.size); buffer.put(it) }
            buffer.array()
        }

        is SetVal -> { // type | size1 | element1 | size2 | element2 | ...
            val listBytes = value.map { serialize(it) } // serialize each element
            val bufferSize = 1 + listBytes.sumOf { 4 + it.size }
            val buffer = ByteBuffer.allocate(bufferSize)
            buffer.put(Type.SET.byte) // list type indicator
            listBytes.forEach { buffer.putInt(it.size); buffer.put(it) }
            buffer.array()
        }

        is MapVal -> { // type | size_keyN | keyN | size_valueN | valueN
            val mapEntriesBytes = value.map { (k, v) -> k.toByteArray() to serialize(v) }
            val bufferSize = 1 + mapEntriesBytes.sumOf { (k, v) -> 4 + k.size + 4 + v.size }
            val buffer = ByteBuffer.allocate(bufferSize).apply { put(Type.MAP.byte) }
            mapEntriesBytes.forEach { (keyBytes, valueBytes) ->
                buffer.putInt(keyBytes.size); buffer.put(keyBytes)
                buffer.putInt(valueBytes.size); buffer.put(valueBytes)
            }
            buffer.array()
        }

        else -> throw IllegalArgumentException("Unknown value type: $value")
    }


    /**
     * Deserializes a byte array into an [IValue] instance.
     *
     * This method decodes a byte array previously serialized with [serialize] back into an
     * [IValue] instance. The type and content of the value are reconstructed based on
     * the data contained in the byte array.
     *
     * Example usage:
     * ```kotlin
     * val serialized = byteArrayOf(Type.NUM_INT.byte, 0, 0, 0, 42)
     * val deserialized = DftByteArraySerializerImpl.deserialize(serialized)
     * println(deserialized) // Outputs: NumVal{42}
     * ```
     *
     * @param material The byte array to deserialize.
     * @return The deserialized [IValue] instance.
     * @throws IllegalArgumentException If the material contains an unknown or unsupported type identifier.
     */
    override fun deserialize(material: ByteArray): IValue = when (material.firstOrNull()) {
        Type.NULL.byte -> NullVal
        Type.STR.byte -> StrVal(material.decodeToString(1))
        Type.BOOL.byte -> BoolVal(material[1] == 1.toByte())
        Type.UNSURE_ANY.byte -> Unsure.ANY
        Type.UNSURE_NUM.byte -> Unsure.NUM
        Type.UNSURE_STR.byte -> Unsure.STR
        Type.UNSURE_BOOL.byte -> Unsure.BOOL
        Type.NUM_BYTE.byte -> NumVal(material[1])
        Type.NUM_SHORT.byte -> NumVal(ByteBuffer.wrap(material, 1, 2).short)
        Type.NUM_INT.byte -> NumVal(ByteBuffer.wrap(material, 1, 4).int)
        Type.NUM_LONG.byte -> NumVal(ByteBuffer.wrap(material, 1, 8).long)
        Type.NUM_FLOAT.byte -> NumVal(ByteBuffer.wrap(material, 1, 4).float)
        Type.NUM_DOUBLE.byte -> NumVal(ByteBuffer.wrap(material, 1, 8).double)
        Type.NUM_OTHERS.byte -> NumVal(NumberUtils.createNumber(material.decodeToString(1)))
        Type.RANGE.byte -> { // type | first size | first bytes | second bytes
            val buffer = ByteBuffer.wrap(material, 1, material.size - 1)
            val firstBytesSize = buffer.getInt() // read the size of the first bytes
            val firstBytes = ByteArray(firstBytesSize).also { buffer.get(it) }
            val firstNumber = deserialize(firstBytes) as NumVal
            val secondBytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            val secondNumber = deserialize(secondBytes) as NumVal
            RangeVal(firstNumber, secondNumber)
        }

        Type.LIST.byte -> { // type | size_keyN | keyN | size_valueN | valueN
            val buffer = if (material.size == 1) ByteBuffer.allocate(0)
            else ByteBuffer.wrap(material, 1, material.size - 1)
            val list = ListVal()
            while (buffer.hasRemaining()) {
                val elementSize = buffer.getInt() // read the size of the element bytes
                val elementBytes = ByteArray(elementSize).also { buffer.get(it) }
                list.plusAssign(deserialize(elementBytes))
            }
            list
        }

        Type.SET.byte -> { // type | size_keyN | keyN | size_valueN | valueN
            val buffer = if (material.size == 1) ByteBuffer.allocate(0)
            else ByteBuffer.wrap(material, 1, material.size - 1)
            val set = SetVal()
            while (buffer.hasRemaining()) {
                val elementSize = buffer.getInt() // read the size of the element bytes
                val elementBytes = ByteArray(elementSize).also { buffer.get(it) }
                set.plusAssign(deserialize(elementBytes))
            }
            set
        }

        Type.MAP.byte -> { // type | size_keyN | keyN | size_valueN | valueN
            val map = MapVal()
            val buffer = if (material.size == 1) ByteBuffer.allocate(0)
            else ByteBuffer.wrap(material, 1, material.size - 1)
            while (buffer.hasRemaining()) {
                val keySize = buffer.getInt() // read the size of the key bytes
                val keyBytes = ByteArray(keySize).also { buffer.get(it) }
                val key = keyBytes.decodeToString()
                val valueSize = buffer.getInt() // read the size of the value bytes
                val valueBytes = ByteArray(valueSize).also { buffer.get(it) }
                map[key] = deserialize(valueBytes)
            }
            map
        }

        else -> throw IllegalArgumentException("Unknown value type: ${material.firstOrNull()}")
    }
}