package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.strVal
import edu.jhu.cobra.commons.value.toByteArray
import java.nio.CharBuffer

// The character-buffer STR_BYTES frame: StrB:<byte count>:<two lowercase hex digits per byte>.

private const val HEX_DIGITS = "0123456789abcdef"
private const val HEX_DIGITS_PER_BYTE = 2
private const val HEX_DIGIT_BITS = 4
private const val HEX_DIGIT_MASK = 0xF
private const val BYTE_MASK = 0xFF

/**
 * Encodes a string holding a unit U+DC80..U+DCFF as its `StrB` frame; the byte count is spelled by
 * [Int.asHexString].
 *
 * @param value The string to encode
 * @return The frame, tag included
 */
internal fun strBytesFrame(value: StrVal): CharBuffer {
    val bytes = value.toByteArray()
    return "${Type.STR_BYTES.str}:${bytes.size.asHexString()}:${bytes.toLowercaseHex()}".asCharBuffer()
}

/**
 * Reads the body of a `StrB` frame after its tag. The byte count must be spelled as [Int.asHexString]
 * emits it and the payload must hold a byte outside UTF-8, so each value has one `StrB` material.
 *
 * @param material The buffer positioned after the tag
 * @return The decoded string
 * @throws IllegalArgumentException if the count spelling, the payload or its length is malformed
 */
internal fun readStrBytesBody(material: CharBuffer): StrVal {
    val spelling = material.getString(until = ':')
    val count = spelling.asHexInt()
    require(count.asHexString() == spelling) {
        "Invalid string byte count '$spelling': expected '${count.asHexString()}'"
    }
    val byteCount = checkSizePrefix(count, material.remaining() / HEX_DIGITS_PER_BYTE, "string byte count")
    return material
        .getString(size = byteCount * HEX_DIGITS_PER_BYTE)
        .lowercaseHexToBytes()
        .strVal
        .requireByteOutsideUtf8()
}

private fun ByteArray.toLowercaseHex(): String {
    val hex = StringBuilder(size * HEX_DIGITS_PER_BYTE)
    for (byte in this) {
        val bits = byte.toInt() and BYTE_MASK
        hex.append(HEX_DIGITS[bits shr HEX_DIGIT_BITS]).append(HEX_DIGITS[bits and HEX_DIGIT_MASK])
    }
    return hex.toString()
}

// Two lowercase hex digits per byte; Integer.parseInt would accept a sign and uppercase digits.
private fun String.lowercaseHexToBytes(): ByteArray =
    ByteArray(length / HEX_DIGITS_PER_BYTE) { index ->
        val high = hexDigitValue(this[index * HEX_DIGITS_PER_BYTE])
        val low = hexDigitValue(this[index * HEX_DIGITS_PER_BYTE + 1])
        ((high shl HEX_DIGIT_BITS) or low).toByte()
    }

private fun hexDigitValue(digit: Char): Int {
    val value = HEX_DIGITS.indexOf(digit)
    require(value >= 0) { "Invalid hex digit '$digit': expected 0-9 or a-f" }
    return value
}
