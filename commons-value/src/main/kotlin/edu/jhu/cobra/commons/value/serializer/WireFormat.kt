package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import java.nio.CharBuffer

// Every serialized value starts with one type identifier byte.
internal const val TYPE_TAG_BYTES = 1

// Size and count prefixes are encoded as big-endian Int values.
internal const val SIZE_PREFIX_BYTES = 4

// A tagged long payload: one type byte followed by eight big-endian value bytes.
internal const val TAGGED_LONG_BYTES = TYPE_TAG_BYTES + Long.SIZE_BYTES

// Builds a tagged long payload: the type byte followed by the big-endian value bytes.
internal fun longToBytes(
    type: Byte,
    value: Long,
): ByteArray {
    val bytes = ByteArray(TAGGED_LONG_BYTES)
    bytes[0] = type
    longInto(bytes, TYPE_TAG_BYTES, value)
    return bytes
}

// Writes a big-endian Int at [offset]; matches ByteBuffer.putInt so all serializers share one layout.
internal fun intInto(
    bytes: ByteArray,
    offset: Int,
    value: Int,
) {
    bytes[offset] = (value shr 24).toByte()
    bytes[offset + 1] = (value shr 16).toByte()
    bytes[offset + 2] = (value shr 8).toByte()
    bytes[offset + 3] = value.toByte()
}

// Writes a big-endian Long at [offset]; matches ByteBuffer.putLong so all serializers share one layout.
internal fun longInto(
    bytes: ByteArray,
    offset: Int,
    value: Long,
) {
    bytes[offset] = (value shr 56).toByte()
    bytes[offset + 1] = (value shr 48).toByte()
    bytes[offset + 2] = (value shr 40).toByte()
    bytes[offset + 3] = (value shr 32).toByte()
    bytes[offset + 4] = (value shr 24).toByte()
    bytes[offset + 5] = (value shr 16).toByte()
    bytes[offset + 6] = (value shr 8).toByte()
    bytes[offset + 7] = value.toByte()
}

/**
 * Converts a hexadecimal string into an integer.
 *
 * The parse is unsigned so every [Int.asHexString] output round-trips, including the 8-digit
 * two's-complement rendering of a negative value.
 *
 * @return The integer whose unsigned hexadecimal representation is this string
 * @throws NumberFormatException if the string is not a valid hexadecimal number
 */
public fun String.asHexInt(): Int = Integer.parseUnsignedInt(this, 16)

/**
 * Converts an integer into its hexadecimal string representation.
 *
 * @return The hexadecimal string representation of the integer
 */
public fun Int.asHexString(): String = Integer.toHexString(this)

/**
 * Reads a byte array of the specified size from the [ByteBuffer].
 *
 * @param size The number of bytes to read
 * @return A byte array containing the read bytes
 * @throws IllegalArgumentException if [size] is negative or exceeds the remaining bytes
 */
public fun ByteBuffer.getArray(size: Int): ByteArray = ByteArray(checkSizePrefix(size, remaining(), "byte array size")).also { get(it) }

/**
 * Reads a string from the [ByteBuffer].
 *
 * If size is provided, reads exactly that many bytes. Otherwise, reads an integer prefix
 * that specifies the string length, then reads that many bytes.
 *
 * @param size The size of the string to read, or null to use the integer prefix
 * @return The decoded string from the buffer
 * @throws IllegalArgumentException if the size is negative or exceeds the remaining bytes
 * @throws CharacterCodingException if the bytes are not well-formed UTF-8
 */
public fun ByteBuffer.getString(size: Int? = null): String = getArray(size ?: getInt()).decodeToString(throwOnInvalidSequence = true)

/**
 * Creates a [ByteBuffer] from a variable number of byte elements.
 *
 * @param elements The byte elements to wrap into a buffer
 * @return A [ByteBuffer] containing the provided elements
 */
public fun byteBufferOf(vararg elements: Byte): ByteBuffer = ByteBuffer.wrap(elements)

/**
 * Puts a [Type] into the [ByteBuffer] by adding its byte representation.
 *
 * @param type The [Type] to add to the buffer
 * @return The updated [ByteBuffer] for chaining
 * @throws BufferOverflowException if there is no space remaining
 */
public fun ByteBuffer.put(type: Type): ByteBuffer = put(type.byte)

/**
 * Converts a [String] into a [CharBuffer].
 *
 * @return A [CharBuffer] representing the string
 */
public fun String.asCharBuffer(): CharBuffer = CharBuffer.wrap(toCharArray())

/**
 * Reads characters from the buffer into a new [CharBuffer] until a specified character is encountered.
 *
 * The delimiter itself is consumed but not included in the result. If the delimiter is absent,
 * all remaining characters are read.
 *
 * @param until The character to stop reading at
 * @return A new [CharBuffer] containing the characters read
 */
public fun CharBuffer.getBuffer(until: Char): CharBuffer {
    val start = position()
    val delimiterIndex = (start until limit()).firstOrNull { get(it) == until }
    val chars = CharArray(if (delimiterIndex == null) remaining() else delimiterIndex - start).also { get(it) }
    if (delimiterIndex != null) get() // consume the delimiter without returning it
    return CharBuffer.wrap(chars)
}

/**
 * Reads a specified number of characters from the buffer into a new [CharBuffer].
 *
 * @param size The number of characters to read
 * @return A new [CharBuffer] containing the characters read
 * @throws IllegalArgumentException if [size] is negative or exceeds the remaining characters
 */
public fun CharBuffer.getBuffer(size: Int): CharBuffer {
    val chars = CharArray(checkSizePrefix(size, remaining(), "buffer size")).also { get(it) }
    return CharBuffer.wrap(chars)
}

/**
 * Reads characters from the buffer into a string until a specified character is encountered.
 *
 * The delimiter itself is consumed but not included in the result. If the delimiter is absent,
 * all remaining characters are read.
 *
 * @param until The character to stop reading at
 * @return A string containing the characters read
 */
public fun CharBuffer.getString(until: Char): String = getBuffer(until).toString()

/**
 * Reads a specified number of characters from the buffer into a string.
 *
 * @param size The number of characters to read
 * @return A string containing the characters read
 * @throws IllegalArgumentException if [size] is negative or exceeds the remaining characters
 */
public fun CharBuffer.getString(size: Int): String = getBuffer(size).toString()
