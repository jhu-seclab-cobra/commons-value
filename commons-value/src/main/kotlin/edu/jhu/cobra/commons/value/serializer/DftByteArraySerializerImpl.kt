package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.*
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
        is StrVal -> {
            val bytes = value.core.toByteArray()
            ByteArray(1 + bytes.size).also {
                it[0] = Type.STR.byte
                bytes.copyInto(it, 1)
            }
        }
        is BoolVal -> byteArrayOf(Type.BOOL.byte, if (value.core) 1 else 0)
        is Unsure -> when (value) {
            Unsure.NUM -> byteArrayOf(Type.UNSURE_NUM.byte)
            Unsure.STR -> byteArrayOf(Type.UNSURE_STR.byte)
            Unsure.BOOL -> byteArrayOf(Type.UNSURE_BOOL.byte)
            else -> byteArrayOf(Type.UNSURE_ANY.byte)
        }

        is NumVal -> when (val num = value.core) {
            is Byte -> byteArrayOf(Type.NUM_BYTE.byte, num)
            is Short -> shortToBytes(Type.NUM_SHORT.byte, num)
            is Int -> intToBytes(Type.NUM_INT.byte, num)
            is Long -> longToBytes(Type.NUM_LONG.byte, num)
            is Float -> intToBytes(Type.NUM_FLOAT.byte, java.lang.Float.floatToRawIntBits(num))
            is Double -> longToBytes(Type.NUM_DOUBLE.byte, java.lang.Double.doubleToRawLongBits(num))
            else -> {
                val bytes = num.toString().toByteArray()
                ByteArray(1 + bytes.size).also {
                    it[0] = Type.NUM_OTHERS.byte
                    bytes.copyInto(it, 1)
                }
            }
        }

        is RangeVal -> {
            val firstBytes = serialize(value.start)
            val secondBytes = serialize(value.endInclusive)
            val result = ByteArray(1 + 4 + firstBytes.size + secondBytes.size)
            result[0] = Type.RANGE.byte
            intInto(result, 1, firstBytes.size)
            firstBytes.copyInto(result, 5)
            secondBytes.copyInto(result, 5 + firstBytes.size)
            result
        }

        is ListVal -> {
            val listBytes = value.map { serialize(it) }
            val result = ByteArray(1 + listBytes.sumOf { 4 + it.size })
            result[0] = Type.LIST.byte
            var offset = 1
            listBytes.forEach { bytes ->
                intInto(result, offset, bytes.size); offset += 4
                bytes.copyInto(result, offset); offset += bytes.size
            }
            result
        }

        is SetVal -> {
            val listBytes = value.map { serialize(it) }
            val result = ByteArray(1 + listBytes.sumOf { 4 + it.size })
            result[0] = Type.SET.byte
            var offset = 1
            listBytes.forEach { bytes ->
                intInto(result, offset, bytes.size); offset += 4
                bytes.copyInto(result, offset); offset += bytes.size
            }
            result
        }

        is MapVal -> {
            val mapEntriesBytes = value.map { (k, v) -> k.toByteArray() to serialize(v) }
            val result = ByteArray(1 + mapEntriesBytes.sumOf { (k, v) -> 4 + k.size + 4 + v.size })
            result[0] = Type.MAP.byte
            var offset = 1
            mapEntriesBytes.forEach { (keyBytes, valueBytes) ->
                intInto(result, offset, keyBytes.size); offset += 4
                keyBytes.copyInto(result, offset); offset += keyBytes.size
                intInto(result, offset, valueBytes.size); offset += 4
                valueBytes.copyInto(result, offset); offset += valueBytes.size
            }
            result
        }

        else -> throw IllegalArgumentException("Unknown value type: $value")
    }

    private fun shortToBytes(type: Byte, v: Short): ByteArray {
        val arr = ByteArray(3)
        arr[0] = type
        arr[1] = (v.toInt() shr 8).toByte()
        arr[2] = v.toByte()
        return arr
    }

    private fun intToBytes(type: Byte, v: Int): ByteArray {
        val arr = ByteArray(5)
        arr[0] = type
        arr[1] = (v shr 24).toByte()
        arr[2] = (v shr 16).toByte()
        arr[3] = (v shr 8).toByte()
        arr[4] = v.toByte()
        return arr
    }

    private fun longToBytes(type: Byte, v: Long): ByteArray {
        val arr = ByteArray(9)
        arr[0] = type
        arr[1] = (v shr 56).toByte()
        arr[2] = (v shr 48).toByte()
        arr[3] = (v shr 40).toByte()
        arr[4] = (v shr 32).toByte()
        arr[5] = (v shr 24).toByte()
        arr[6] = (v shr 16).toByte()
        arr[7] = (v shr 8).toByte()
        arr[8] = v.toByte()
        return arr
    }

    // Writes an Int (big-endian) into the array at the given offset.
    private fun intInto(arr: ByteArray, offset: Int, v: Int) {
        arr[offset] = (v shr 24).toByte()
        arr[offset + 1] = (v shr 16).toByte()
        arr[offset + 2] = (v shr 8).toByte()
        arr[offset + 3] = v.toByte()
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
    override fun deserialize(material: ByteArray): IValue {
        require(material.isNotEmpty()) { "Empty byte array" }
        return deserializeFrom(ByteBuffer.wrap(material))
    }

    // Shared-buffer deserialization: reads directly from a ByteBuffer, avoiding per-value wrap allocations.
    // For collections, uses limit-based windowing instead of copying sub-arrays.
    private fun deserializeFrom(buffer: ByteBuffer): IValue = when (buffer.get()) {
        Type.NULL.byte -> NullVal
        Type.STR.byte -> {
            val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            StrVal(bytes.decodeToString())
        }
        Type.BOOL.byte -> BoolVal(buffer.get() == 1.toByte())
        Type.UNSURE_ANY.byte -> Unsure.ANY
        Type.UNSURE_NUM.byte -> Unsure.NUM
        Type.UNSURE_STR.byte -> Unsure.STR
        Type.UNSURE_BOOL.byte -> Unsure.BOOL
        Type.NUM_BYTE.byte -> NumVal(buffer.get())
        Type.NUM_SHORT.byte -> NumVal(buffer.short)
        Type.NUM_INT.byte -> NumVal(buffer.int)
        Type.NUM_LONG.byte -> NumVal(buffer.long)
        Type.NUM_FLOAT.byte -> NumVal(buffer.float)
        Type.NUM_DOUBLE.byte -> NumVal(buffer.double)
        Type.NUM_OTHERS.byte -> {
            val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            NumVal(NumberUtils.createNumber(bytes.decodeToString()))
        }
        Type.RANGE.byte -> {
            val firstSize = buffer.getInt()
            val savedLimit = buffer.limit()
            buffer.limit(buffer.position() + firstSize)
            val first = deserializeFrom(buffer) as NumVal
            buffer.limit(savedLimit)
            val second = deserializeFrom(buffer) as NumVal
            RangeVal(first, second)
        }
        Type.LIST.byte -> {
            val list = ListVal()
            while (buffer.hasRemaining()) {
                val elementSize = buffer.getInt()
                val savedLimit = buffer.limit()
                buffer.limit(buffer.position() + elementSize)
                list.plusAssign(deserializeFrom(buffer))
                buffer.limit(savedLimit)
            }
            list
        }
        Type.SET.byte -> {
            val set = SetVal()
            while (buffer.hasRemaining()) {
                val elementSize = buffer.getInt()
                val savedLimit = buffer.limit()
                buffer.limit(buffer.position() + elementSize)
                set.plusAssign(deserializeFrom(buffer))
                buffer.limit(savedLimit)
            }
            set
        }
        Type.MAP.byte -> {
            val map = MapVal()
            while (buffer.hasRemaining()) {
                val keySize = buffer.getInt()
                val keyBytes = ByteArray(keySize).also { buffer.get(it) }
                val key = keyBytes.decodeToString()
                val valueSize = buffer.getInt()
                val savedLimit = buffer.limit()
                buffer.limit(buffer.position() + valueSize)
                map[key] = deserializeFrom(buffer)
                buffer.limit(savedLimit)
            }
            map
        }
        else -> throw IllegalArgumentException("Unknown value type: ${buffer.get(buffer.position() - 1)}")
    }
}