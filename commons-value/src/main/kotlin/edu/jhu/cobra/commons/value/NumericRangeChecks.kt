package edu.jhu.cobra.commons.value

import java.math.BigDecimal

private val BIG_LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE)
private val BIG_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE)
private val BIG_INT_MAX_VALUE = BigDecimal.valueOf(Int.MAX_VALUE.toLong())
private val BIG_INT_MIN_VALUE = BigDecimal.valueOf(Int.MIN_VALUE.toLong())
private val BIG_SHORT_MAX_VALUE = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
private val BIG_SHORT_MIN_VALUE = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
private val BIG_BYTE_MAX_VALUE = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
private val BIG_BYTE_MIN_VALUE = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())

// Exact numeric bound check shared by every isIn*Range property. NaN and infinities have no
// numeric value, so they are outside every range; all other numbers compare exactly via BigDecimal.
private fun Number.isNumericallyIn(
    min: BigDecimal,
    max: BigDecimal,
): Boolean =
    when {
        this is Double && !isFinite() -> false
        this is Float && !isFinite() -> false
        else -> BigDecimal(toString()) in min..max
    }

/**
 * Checks if this number is numerically within [Long.MIN_VALUE]..[Long.MAX_VALUE].
 *
 * For types that always fit ([Byte], [Short], [Int], [Long]), returns `true` immediately.
 * Other types are compared exactly via [BigDecimal]; a fractional value inside the bounds
 * counts as in range. `NaN` and infinities are never in range.
 *
 * @return `true` if the number lies within the [Long] bounds, `false` otherwise
 */
public val Number.isInLongRange: Boolean
    get() =
        when (this) {
            is Byte, is Short, is Int, is Long -> true
            else -> isNumericallyIn(BIG_LONG_MIN_VALUE, BIG_LONG_MAX_VALUE)
        }

/**
 * Checks if this number is numerically within [Int.MIN_VALUE]..[Int.MAX_VALUE].
 *
 * For types that always fit ([Byte], [Short], [Int]), returns `true` immediately.
 * Other types are compared exactly via [BigDecimal]; a fractional value inside the bounds
 * counts as in range. `NaN` and infinities are never in range.
 *
 * @return `true` if the number lies within the [Int] bounds, `false` otherwise
 */
public val Number.isInIntRange: Boolean
    get() =
        when (this) {
            is Byte, is Short, is Int -> true
            else -> isNumericallyIn(BIG_INT_MIN_VALUE, BIG_INT_MAX_VALUE)
        }

/**
 * Checks if this number is numerically within [Short.MIN_VALUE]..[Short.MAX_VALUE].
 *
 * For types that always fit ([Byte], [Short]), returns `true` immediately.
 * Other types are compared exactly via [BigDecimal]; a fractional value inside the bounds
 * counts as in range. `NaN` and infinities are never in range.
 *
 * @return `true` if the number lies within the [Short] bounds, `false` otherwise
 */
public val Number.isInShortRange: Boolean
    get() =
        when (this) {
            is Byte, is Short -> true
            else -> isNumericallyIn(BIG_SHORT_MIN_VALUE, BIG_SHORT_MAX_VALUE)
        }

/**
 * Checks if this number is numerically within [Byte.MIN_VALUE]..[Byte.MAX_VALUE].
 *
 * [Byte] returns `true` immediately. Other types are compared exactly via [BigDecimal];
 * a fractional value inside the bounds counts as in range. `NaN` and infinities are never in range.
 *
 * @return `true` if the number lies within the [Byte] bounds, `false` otherwise
 */
public val Number.isInByteRange: Boolean
    get() =
        when (this) {
            is Byte -> true
            else -> isNumericallyIn(BIG_BYTE_MIN_VALUE, BIG_BYTE_MAX_VALUE)
        }
