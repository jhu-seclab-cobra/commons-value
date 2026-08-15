package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.IValue
import java.nio.BufferUnderflowException

// Bounds recursive nesting in serialize and deserialize: keeps stack use finite on adversarial
// material and rejects cyclic value graphs at serialize.
internal const val MAX_NESTING_DEPTH = 1000

/**
 * Validates a recursive value-nesting depth against [MAX_NESTING_DEPTH].
 *
 * @param depth The current nesting depth, zero for the top-level value
 * @return The validated depth
 * @throws IllegalArgumentException if [depth] exceeds [MAX_NESTING_DEPTH]
 */
internal fun checkNestingDepth(depth: Int): Int {
    require(depth <= MAX_NESTING_DEPTH) { "Value nesting exceeds $MAX_NESTING_DEPTH levels" }
    return depth
}

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
 * Runs a deserialization body and normalizes every decoding failure to [ValFormatException].
 *
 * Truncated material surfaces as [BufferUnderflowException], unparsable numbers as
 * [NumberFormatException], and validation failures as [IllegalArgumentException]; all three
 * are rethrown as [ValFormatException] with the original failure as cause.
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
