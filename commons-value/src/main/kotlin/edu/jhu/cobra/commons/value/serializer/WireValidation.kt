package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.indexOfByteOutsideUtf8
import edu.jhu.cobra.commons.value.isByteOutsideUtf8
import java.nio.BufferUnderflowException
import java.nio.charset.CharacterCodingException

/**
 * Validates that a string contains no unpaired UTF-16 surrogate characters.
 *
 * An unpaired surrogate cannot survive a charset transcoding round-trip, so serializers reject
 * it at serialize instead of corrupting the value silently.
 *
 * @return The validated string
 * @throws IllegalArgumentException if the string contains an unpaired surrogate
 */
internal fun String.requireWellFormedUtf16(): String {
    var index = 0
    while (index < length) {
        // Single mask-compare per char: 0xD800..0xDFFF are exactly the chars with these five bits.
        if (this[index].code and 0xF800 == 0xD800) return requireSurrogatesPaired(from = index)
        index++
    }
    return this
}

// Pairing check from the first surrogate onward; runs only for strings that contain surrogates.
private fun String.requireSurrogatesPaired(from: Int): String {
    var index = from
    while (index < length) {
        val char = this[index]
        if (char.isHighSurrogate()) {
            require(index + 1 < length && this[index + 1].isLowSurrogate()) {
                "Unpaired high surrogate at index $index"
            }
            index += 2
        } else {
            require(!char.isLowSurrogate()) { "Unpaired low surrogate at index $index" }
            index++
        }
    }
    return this
}

/**
 * Whether this string holds an unpaired unit U+DC80..U+DCFF, the unit of one byte outside UTF-8.
 *
 * Decoding a [Type.STR_BYTES] payload rejects a string for which this is `false`, and decoding a
 * binary [Type.STR] payload rejects a string for which this is `true`, so each string has one material
 * per format.
 *
 * @return `true` if [StrVal.core] holds an unpaired unit U+DC80..U+DCFF
 */
internal fun StrVal.holdsByteOutsideUtf8(): Boolean = core.indexOfByteOutsideUtf8() >= 0

/**
 * The type tag the serializers write for this string: [Type.STR_BYTES] when [StrVal.core] holds an
 * unpaired unit U+DC80..U+DCFF, [Type.STR] otherwise.
 *
 * @return The type tag of this string
 * @throws IllegalArgumentException if [StrVal.core] holds an unpaired surrogate outside U+DC80..U+DCFF
 */
internal fun StrVal.stringTag(): Type {
    var holdsByte = false
    var index = 0
    while (index < core.length) {
        val char = core[index]
        when {
            char.isHighSurrogate() && index + 1 < core.length && core[index + 1].isLowSurrogate() -> {
                index++
            }

            char.isByteOutsideUtf8() -> {
                holdsByte = true
            }

            else -> {
                require(!char.isSurrogate()) { "Unpaired surrogate at index $index outside U+DC80..U+DCFF" }
            }
        }
        index++
    }
    return if (holdsByte) Type.STR_BYTES else Type.STR
}

/**
 * Validates that a decoded binary string payload holds no byte outside UTF-8.
 *
 * @return The validated string
 * @throws IllegalArgumentException if the payload holds a byte outside well-formed UTF-8
 */
internal fun StrVal.requireWellFormedUtf8(): StrVal {
    require(!holdsByteOutsideUtf8()) { "${Type.STR} payload is not well-formed UTF-8" }
    return this
}

/**
 * Validates that a decoded string-bytes payload holds a byte outside UTF-8.
 *
 * @return The validated string
 * @throws IllegalArgumentException if every byte of the payload is part of well-formed UTF-8
 */
internal fun StrVal.requireByteOutsideUtf8(): StrVal {
    require(holdsByteOutsideUtf8()) { "${Type.STR_BYTES} payload is well-formed UTF-8: expected ${Type.STR}" }
    return this
}

/**
 * Runs a deserialization body and normalizes every decoding failure to [ValFormatException].
 *
 * Truncated material surfaces as [BufferUnderflowException], unparsable numbers as
 * [NumberFormatException], malformed UTF-8 as [CharacterCodingException], and validation
 * failures as [IllegalArgumentException]; all are rethrown as [ValFormatException] with the
 * original failure as cause.
 *
 * @param block The deserialization body to run
 * @return The value produced by [block]
 * @throws ValFormatException if [block] fails with any decoding exception
 */
internal inline fun <T> decodeMaterial(block: () -> T): T =
    try {
        block()
    } catch (e: ValFormatException) {
        throw e
    } catch (e: BufferUnderflowException) {
        throw ValFormatException("Truncated material", e)
    } catch (e: NumberFormatException) {
        throw ValFormatException("Unparsable number in material", e)
    } catch (e: CharacterCodingException) {
        throw ValFormatException("Malformed UTF-8 in material", e)
    } catch (e: IllegalArgumentException) {
        throw ValFormatException(e.message ?: "Malformed material", e)
    }

/**
 * Validates a size or count prefix decoded from serialized material.
 *
 * @param size The decoded size or count value
 * @param remaining The number of units remaining in the material
 * @param context The name of the prefixed quantity, included in the error message
 * @return The validated size
 * @throws IllegalArgumentException if [size] is negative or exceeds [remaining]
 */
internal fun checkSizePrefix(
    size: Int,
    remaining: Int,
    context: String,
): Int {
    require(size in 0..remaining) { "Invalid $context $size: expected 0..$remaining" }
    return size
}

/**
 * Validates that a value decoded from serialized material has the expected type.
 *
 * @param value The decoded value
 * @param context The name of the decoded quantity, included in the error message
 * @return The value as an instance of [T]
 * @throws IllegalArgumentException if [value] is not an instance of [T]
 */
internal inline fun <reified T : IValue> requireDecodedType(
    value: IValue,
    context: String,
): T {
    require(value is T) { "Invalid $context: expected ${T::class.simpleName}, got $value" }
    return value
}
