package cobra.commons.value.serializer

import org.apache.commons.lang3.math.NumberUtils
import java.io.DataInput
import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.CharBuffer

/**
 * Enum class representing different data types with their associated byte values and string labels.
 *
 * @property byte The byte representation of the type.
 * @property str The string label for the type.
 */
enum class Type(val byte: Byte, val str: String) {
    NULL(10, "Null"),
    STR(20, "Str"),
    BOOL(30, "Bool"),
    BOOL_TRUE(31, "True"),
    BOOL_FALSE(32, "False"),
    UNSURE_ANY(40, "UnANY"),
    UNSURE_STR(41, "UnSTR"),
    UNSURE_NUM(42, "UnNUM"),
    UNSURE_BOOL(43, "UnBOOL"),
    NUM_BYTE(50, "Byte"),
    NUM_SHORT(51, "Short"),
    NUM_INT(52, "Int"),
    NUM_LONG(53, "Long"),
    NUM_FLOAT(54, "Float"),
    NUM_DOUBLE(55, "Double"),
    NUM_OTHERS(56, "Num?"),
    RANGE(60, "Range"),
    LIST(70, "List"),
    SET(71, "Set"),
    MAP(80, "Map"),
}

/**
 * Converts the string representation of a number into a [Number] object.
 *
 * @return The number representation of the string.
 */
fun String.asNumber(): Number = NumberUtils.createNumber(this)

/**
 * Converts a hexadecimal string into an integer.
 *
 * @return The integer value of the hexadecimal string.
 */
fun String.asHexInt(): Int = Integer.parseInt(this, 16)

/**
 * Converts an integer into its hexadecimal string representation.
 *
 * @return The hexadecimal string representation of the integer.
 */
fun Int.asHexString(): String = Integer.toHexString(this)

/**
 * Reads a byte array of the specified size from the [ByteBuffer].
 *
 * @param size The number of bytes to read.
 * @return A byte array containing the read bytes.
 */
fun ByteBuffer.getArray(size: Int) =
    ByteArray(size).also { bs -> repeat(size) { bs[it] = get() } }

/**
 * Reads a string from the [ByteBuffer], either of a specified size or based on the integer prefix.
 *
 * @param size The size of the string to read, or null to use the integer prefix.
 * @return The string read from the buffer.
 */
fun ByteBuffer.getString(size: Int? = null): String =
    getArray(size ?: getInt()).decodeToString()

/**
 * Creates a [ByteBuffer] from a variable number of byte elements.
 *
 * @param elements The byte elements to wrap into a [ByteBuffer].
 * @return A [ByteBuffer] containing the provided elements.
 */
fun byteBufferOf(vararg elements: Byte): ByteBuffer =
    ByteBuffer.wrap(elements)

/**
 * Puts a [Type] into the [ByteBuffer] by adding its byte representation.
 *
 * @param type The [Type] to add to the buffer.
 * @return The updated [ByteBuffer].
 */
fun ByteBuffer.put(type: Type): ByteBuffer = put(type.byte)

/**
 * Converts a [String] into a [CharBuffer].
 *
 * @return A [CharBuffer] representing the string.
 */
fun String.asCharBuffer(): CharBuffer = CharBuffer.wrap(toCharArray())

/**
 * Removes characters from the buffer until a specified character is encountered.
 *
 * @param until The character to remove until.
 * @return True if the character was found, otherwise false.
 */
fun CharBuffer.remove(until: Char): Boolean {
    val (curPos, maxPos) = position() to limit()
    repeat(maxPos - curPos) {
        if (get() == until) return true
    }
    return false
}

/**
 * Reads characters from the buffer into a new [CharBuffer] until a specified character is encountered.
 *
 * @param until The character to stop reading at.
 * @return A new [CharBuffer] containing the characters read.
 */
fun CharBuffer.getBuffer(until: Char): CharBuffer {
    val (curPos, maxPos) = position() to limit()
    val searchRange = 0 until maxPos - curPos
    val length = searchRange.firstOrNull { get(it + curPos) == until }
    val newBuffer = CharBuffer.allocate(length ?: maxPos)
    repeat(newBuffer.limit()) { newBuffer.put(this.get()) }
    if (length != null) get() // remove the found character
    return newBuffer.flip()
}

/**
 * Reads a specified number of characters from the buffer into a new [CharBuffer].
 *
 * @param size The number of characters to read.
 * @return A new [CharBuffer] containing the characters read.
 */
fun CharBuffer.getBuffer(size: Int): CharBuffer {
    val newBuffer = CharBuffer.allocate(size)
    repeat(newBuffer.limit()) { newBuffer.put(this.get()) }
    return newBuffer.flip()
}

/**
 * Reads characters from the buffer into a string until a specified character is encountered.
 *
 * @param until The character to stop reading at.
 * @return A string containing the characters read.
 */
fun CharBuffer.getString(until: Char): String = getBuffer(until).toString()

/**
 * Reads a specified number of characters from the buffer into a string.
 *
 * @param size The number of characters to read.
 * @return A string containing the characters read.
 */
fun CharBuffer.getString(size: Int): String = getBuffer(size).toString()

/**
 * Reads a byte array of a specified size from the [DataInput].
 *
 * @param size The number of bytes to read.
 * @return A byte array containing the read bytes.
 * @throws EOFException if the end of the stream is reached before reading the specified size.
 */
fun DataInput.asByteArray(size: Int): ByteArray {
    if (size == 0) return ByteArray(0)
    return if (size > 0) {
        val buffer = ByteBuffer.allocate(size)
        repeat(size) { buffer.put(readByte()) }
        buffer.flip().array()
    } else buildList {
        val errors = runCatching { while (true) add(readByte()) }
        errors.onFailure { if (it !is EOFException) throw it }
    }.toByteArray()
}

fun DataInput.asByteSequence(available: Int) = sequence {
    if (available == 0) return@sequence // Do nothing if the available byte is zero
    if (available > 0) repeat(available) { yield(readByte()) }
    else kotlin.runCatching { while (true) yield(readByte()) }.onFailure { if (it is EOFException) throw it }
}
