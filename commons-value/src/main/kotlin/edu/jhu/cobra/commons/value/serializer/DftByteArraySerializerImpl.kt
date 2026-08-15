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
 * A serializer implementation of [IValSerializer] for [IValue] instances that provides serialization
 * and deserialization of various value types into byte arrays. This implementation supports primitive values,
 * collections, and complex types, enabling compact and efficient encoding for storage or transmission.
 *
 * This serializer is designed to handle all supported [IValue] subtypes, providing methods to serialize
 * them into byte arrays and reconstruct them from those arrays.
 */
public object DftByteArraySerializerImpl : IValSerializer<ByteArray> {
    // Serialized RANGE layout: type byte, Int size prefix of the first bound, then two tagged longs.
    private const val RANGE_FIRST_TAG_OFFSET = TYPE_TAG_BYTES + SIZE_PREFIX_BYTES
    private const val RANGE_SECOND_TAG_OFFSET = RANGE_FIRST_TAG_OFFSET + TAGGED_LONG_BYTES

    /**
     * Serializes an [IValue] instance into a byte array.
     *
     * This method encodes the given value into a format that can be transmitted or stored,
     * preserving its type and content. Supported types include null, strings, booleans, numeric values,
     * lists, sets, maps, ranges, and uncertain values.
     *
     * @param value The [IValue] instance to serialize.
     * @return A byte array representing the serialized value.
     * @throws IllegalArgumentException If the value type is unknown or unsupported.
     */
    override fun serialize(value: IValue): ByteArray =
        when (value) {
            is NullVal -> byteArrayOf(Type.NULL.byte)
            is StrVal -> {
                val bytes = value.core.toByteArray()
                ByteArray(TYPE_TAG_BYTES + bytes.size).also {
                    it[0] = Type.STR.byte
                    bytes.copyInto(it, TYPE_TAG_BYTES)
                }
            }
            is BoolVal -> byteArrayOf(Type.BOOL.byte, if (value.core) 1 else 0)
            is Unsure ->
                when (value) {
                    Unsure.NUM -> byteArrayOf(Type.UNSURE_NUM.byte)
                    Unsure.STR -> byteArrayOf(Type.UNSURE_STR.byte)
                    Unsure.BOOL -> byteArrayOf(Type.UNSURE_BOOL.byte)
                    else -> byteArrayOf(Type.UNSURE_ANY.byte)
                }

            is IntVal -> longToBytes(Type.INT.byte, value.core)
            is FloatVal -> longToBytes(Type.FLOAT.byte, value.core.toRawBits())

            is RangeVal -> {
                val result = ByteArray(TYPE_TAG_BYTES + SIZE_PREFIX_BYTES + 2 * TAGGED_LONG_BYTES)
                result[0] = Type.RANGE.byte
                intInto(result, TYPE_TAG_BYTES, TAGGED_LONG_BYTES)
                result[RANGE_FIRST_TAG_OFFSET] = Type.INT.byte
                longInto(result, RANGE_FIRST_TAG_OFFSET + TYPE_TAG_BYTES, value.start.core)
                result[RANGE_SECOND_TAG_OFFSET] = Type.INT.byte
                longInto(result, RANGE_SECOND_TAG_OFFSET + TYPE_TAG_BYTES, value.endInclusive.core)
                result
            }

            is ListVal -> containerToBytes(Type.LIST.byte, value.map { serialize(it) })

            is SetVal -> containerToBytes(Type.SET.byte, value.map { serialize(it) })

            is MapVal -> {
                val mapEntriesBytes = value.map { (k, v) -> k.toByteArray() to serialize(v) }
                val entriesLength = mapEntriesBytes.sumOf { (k, v) -> SIZE_PREFIX_BYTES + k.size + SIZE_PREFIX_BYTES + v.size }
                val result = ByteArray(TYPE_TAG_BYTES + entriesLength)
                result[0] = Type.MAP.byte
                var offset = TYPE_TAG_BYTES
                mapEntriesBytes.forEach { (keyBytes, valueBytes) ->
                    intInto(result, offset, keyBytes.size)
                    offset += SIZE_PREFIX_BYTES
                    keyBytes.copyInto(result, offset)
                    offset += keyBytes.size
                    intInto(result, offset, valueBytes.size)
                    offset += SIZE_PREFIX_BYTES
                    valueBytes.copyInto(result, offset)
                    offset += valueBytes.size
                }
                result
            }
        }

    // LIST and SET share one container layout: a type byte, then size-prefixed element blocks.
    private fun containerToBytes(
        typeByte: Byte,
        elements: List<ByteArray>,
    ): ByteArray {
        val result = ByteArray(TYPE_TAG_BYTES + elements.sumOf { SIZE_PREFIX_BYTES + it.size })
        result[0] = typeByte
        var offset = TYPE_TAG_BYTES
        elements.forEach { bytes ->
            intInto(result, offset, bytes.size)
            offset += SIZE_PREFIX_BYTES
            bytes.copyInto(result, offset)
            offset += bytes.size
        }
        return result
    }

    private fun longToBytes(
        type: Byte,
        v: Long,
    ): ByteArray {
        val arr = ByteArray(TAGGED_LONG_BYTES)
        arr[0] = type
        longInto(arr, TYPE_TAG_BYTES, v)
        return arr
    }

    private fun intInto(
        arr: ByteArray,
        offset: Int,
        v: Int,
    ) {
        arr[offset] = (v shr 24).toByte()
        arr[offset + 1] = (v shr 16).toByte()
        arr[offset + 2] = (v shr 8).toByte()
        arr[offset + 3] = v.toByte()
    }

    private fun longInto(
        arr: ByteArray,
        offset: Int,
        v: Long,
    ) {
        arr[offset] = (v shr 56).toByte()
        arr[offset + 1] = (v shr 48).toByte()
        arr[offset + 2] = (v shr 40).toByte()
        arr[offset + 3] = (v shr 32).toByte()
        arr[offset + 4] = (v shr 24).toByte()
        arr[offset + 5] = (v shr 16).toByte()
        arr[offset + 6] = (v shr 8).toByte()
        arr[offset + 7] = v.toByte()
    }

    /**
     * Deserializes a byte array into an [IValue] instance.
     *
     * This method decodes a byte array previously serialized with [serialize] back into an
     * [IValue] instance. The type and content of the value are reconstructed based on
     * the data contained in the byte array.
     *
     * @param material The byte array to deserialize.
     * @return The deserialized [IValue] instance.
     * @throws ValFormatException If the material is malformed.
     */
    override fun deserialize(material: ByteArray): IValue =
        decodeMaterial {
            require(material.isNotEmpty()) { "Empty byte array" }
            val buffer = ByteBuffer.wrap(material)
            val value = deserializeFrom(buffer)
            require(!buffer.hasRemaining()) { "Trailing material: ${buffer.remaining()} bytes after value" }
            value
        }

    // Shared-buffer deserialization: reads directly from a ByteBuffer, avoiding per-value wrap allocations.
    // For collections, uses limit-based windowing instead of copying sub-arrays.
    private fun deserializeFrom(buffer: ByteBuffer): IValue =
        when (buffer.get()) {
            Type.NULL.byte -> NullVal
            Type.STR.byte -> {
                val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
                StrVal(bytes.decodeToString())
            }
            Type.BOOL.byte -> {
                val payload = buffer.get()
                require(payload in 0..1) { "Invalid BOOL payload: $payload" }
                BoolVal(payload == 1.toByte())
            }
            Type.UNSURE_ANY.byte -> Unsure.ANY
            Type.UNSURE_NUM.byte -> Unsure.NUM
            Type.UNSURE_STR.byte -> Unsure.STR
            Type.UNSURE_BOOL.byte -> Unsure.BOOL
            Type.INT.byte -> IntVal(buffer.long)
            Type.FLOAT.byte -> FloatVal(buffer.double)
            Type.RANGE.byte -> {
                val firstSize = checkSizePrefix(buffer.getInt(), buffer.remaining(), "range bound size")
                val savedLimit = buffer.limit()
                buffer.limit(buffer.position() + firstSize)
                val first = requireDecodedType<IntVal>(deserializeFrom(buffer), "range start")
                buffer.limit(savedLimit)
                val second = requireDecodedType<IntVal>(deserializeFrom(buffer), "range end")
                RangeVal(first, second)
            }
            Type.LIST.byte -> ListVal().also { list -> forEachContainerElement(buffer) { list.plusAssign(it) } }
            Type.SET.byte -> SetVal().also { set -> forEachContainerElement(buffer) { set.plusAssign(it) } }
            Type.MAP.byte -> {
                val map = MapVal()
                while (buffer.hasRemaining()) {
                    val keySize = checkSizePrefix(buffer.getInt(), buffer.remaining(), "key size")
                    val keyBytes = ByteArray(keySize).also { buffer.get(it) }
                    val key = keyBytes.decodeToString()
                    val valueSize = checkSizePrefix(buffer.getInt(), buffer.remaining(), "value size")
                    val savedLimit = buffer.limit()
                    buffer.limit(buffer.position() + valueSize)
                    map[key] = deserializeFrom(buffer)
                    buffer.limit(savedLimit)
                }
                map
            }
            else -> throw ValFormatException("Unknown value type: ${buffer.get(buffer.position() - 1)}")
        }

    // LIST and SET share one container layout: size-prefixed element blocks read via limit windowing.
    private inline fun forEachContainerElement(
        buffer: ByteBuffer,
        action: (IValue) -> Unit,
    ) {
        while (buffer.hasRemaining()) {
            val elementSize = checkSizePrefix(buffer.getInt(), buffer.remaining(), "element size")
            val savedLimit = buffer.limit()
            buffer.limit(buffer.position() + elementSize)
            action(deserializeFrom(buffer))
            buffer.limit(savedLimit)
        }
    }
}
