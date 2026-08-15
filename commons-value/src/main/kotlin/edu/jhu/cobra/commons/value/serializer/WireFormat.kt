package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import java.nio.CharBuffer

// Every serialized value starts with one type identifier byte.
internal const val TYPE_TAG_BYTES = 1

// Size and count prefixes are encoded as big-endian Int values.
internal const val SIZE_PREFIX_BYTES = 4

// A tagged long payload: one type byte followed by eight big-endian value bytes.
internal const val TAGGED_LONG_BYTES = TYPE_TAG_BYTES + Long.SIZE_BYTES

/**
 * Converts a hexadecimal string into an integer.
 *
 * @return The decimal integer value of the hexadecimal string
 * @throws NumberFormatException if the string is not a valid hexadecimal number
 */
public fun String.asHexInt(): Int = Integer.parseInt(this, 16)

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
 */
public fun ByteBuffer.getString(size: Int? = null): String = getArray(size ?: getInt()).decodeToString()

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
 * JVM compatibility extension function for ByteBuffer.flip().
 *
 * This function exists because in Java 8, Buffer.flip() returns Buffer instead of ByteBuffer.
 * To maintain type safety and avoid casting, this extension function ensures ByteBuffer is returned
 * while maintaining the same functionality as the original flip() method.
 *
 * @return The same [ByteBuffer] with its position set to zero and its limit set to the previous position
 */
public fun ByteBuffer.typedFlip(): ByteBuffer = apply { flip() }

/**
 * JVM compatibility extension function for CharBuffer.flip().
 *
 * This function exists because in Java 8, Buffer.flip() returns Buffer instead of CharBuffer.
 * To maintain type safety and avoid casting, this extension function ensures CharBuffer is returned
 * while maintaining the same functionality as the original flip() method.
 *
 * @return The same [CharBuffer] with its position set to zero and its limit set to the previous position
 */
public fun CharBuffer.typedFlip(): CharBuffer = apply { flip() }

/**
 * JVM compatibility extension function for CharBuffer.position().
 *
 * This function exists because in Java 8, Buffer.position() returns Buffer instead of CharBuffer.
 * To maintain type safety and avoid casting, this extension function ensures CharBuffer is returned
 * while maintaining the same functionality as the original position() method.
 *
 * @param pos The new position value
 * @return The same [CharBuffer] with its position set to the specified value
 * @throws IllegalArgumentException if pos is negative or larger than the buffer's limit
 */
public fun CharBuffer.typedPosition(pos: Int): CharBuffer = apply { position(pos) }

/**
 * Consumes characters from the buffer up to and including the first occurrence of [until].
 *
 * If the character is absent, all remaining characters are consumed.
 *
 * @param until The character to stop consuming at
 * @return true if the character was found, false otherwise
 */
public fun CharBuffer.remove(until: Char): Boolean {
    val (curPos, maxPos) = position() to limit()
    repeat(maxPos - curPos) {
        if (get() == until) return true
    }
    return false
}

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
    val (curPos, maxPos) = position() to limit()
    val searchRange = 0 until maxPos - curPos
    val length = searchRange.firstOrNull { get(it + curPos) == until }
    val newBuffer = CharBuffer.allocate(length ?: (maxPos - curPos))
    repeat(newBuffer.limit()) { newBuffer.put(this.get()) }
    if (length != null) get() // remove the found character
    return newBuffer.typedFlip()
}

/**
 * Reads a specified number of characters from the buffer into a new [CharBuffer].
 *
 * @param size The number of characters to read
 * @return A new [CharBuffer] containing the characters read
 * @throws IllegalArgumentException if [size] is negative or exceeds the remaining characters
 */
public fun CharBuffer.getBuffer(size: Int): CharBuffer {
    val newBuffer = CharBuffer.allocate(checkSizePrefix(size, remaining(), "buffer size"))
    repeat(newBuffer.limit()) { newBuffer.put(this.get()) }
    return newBuffer.typedFlip()
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
