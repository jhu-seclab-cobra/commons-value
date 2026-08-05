package edu.jhu.cobra.commons.value

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for the numeric range check extension properties derived from the design doc.
 *
 * - `should return true from isInLongRange for Long MAX_VALUE` — isInLongRange boundary
 * - `should return true from isInLongRange for Long MIN_VALUE` — isInLongRange boundary
 * - `should return true from isInLongRange for zero` — isInLongRange representative
 * - `should return true from isInLongRange for Byte Short Int` — isInLongRange smaller types
 * - `should return false from isInLongRange for value exceeding Long MAX` — isInLongRange out
 * - `should return true from isInIntRange for Int MAX_VALUE` — isInIntRange boundary
 * - `should return true from isInIntRange for Int MIN_VALUE` — isInIntRange boundary
 * - `should return true from isInIntRange for zero` — isInIntRange representative
 * - `should return false from isInIntRange for Long MAX_VALUE` — isInIntRange out
 * - `should return false from isInLongRange for NaN` — isInLongRange non-finite
 * - `should return false from isInIntRange for NaN` — isInIntRange non-finite
 * - `should return false from isInShortRange for NaN` — isInShortRange non-finite
 * - `should return false from isInByteRange for NaN` — isInByteRange non-finite
 * - `should return false from isInIntRange for positive infinity` — isInIntRange non-finite
 * - `should return false from isInIntRange for negative infinity` — isInIntRange non-finite
 * - `should return true from isInIntRange for fractional value inside range` — isInIntRange fractional in
 * - `should return false from isInIntRange for fractional value beyond Int MAX` — isInIntRange fractional out
 * - `should return false from isInShortRange for double beyond Long precision` — isInShortRange exact magnitude
 * - `should return true from isInShortRange for Short MAX_VALUE` — isInShortRange boundary
 * - `should return true from isInShortRange for Short MIN_VALUE` — isInShortRange boundary
 * - `should return true from isInShortRange for zero` — isInShortRange representative
 * - `should return false from isInShortRange for Int MAX_VALUE` — isInShortRange out
 * - `should return true from isInByteRange for Byte MAX_VALUE` — isInByteRange boundary
 * - `should return true from isInByteRange for Byte MIN_VALUE` — isInByteRange boundary
 * - `should return true from isInByteRange for zero` — isInByteRange representative
 * - `should return false from isInByteRange for Short MAX_VALUE` — isInByteRange out
 */
internal class NumericRangeChecksTest {
    // --- Number.isInLongRange ---

    @Test
    fun `should return true from isInLongRange for Long MAX_VALUE`() {
        assertTrue(Long.MAX_VALUE.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for Long MIN_VALUE`() {
        assertTrue(Long.MIN_VALUE.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for zero`() {
        assertTrue(0L.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for Byte Short Int`() {
        assertTrue(Byte.MAX_VALUE.isInLongRange)
        assertTrue(Short.MAX_VALUE.isInLongRange)
        assertTrue(Int.MAX_VALUE.isInLongRange)
    }

    @Test
    fun `should return false from isInLongRange for value exceeding Long MAX`() {
        val bigValue = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)
        assertFalse(bigValue.isInLongRange)
    }

    // --- Number.isInIntRange ---

    @Test
    fun `should return true from isInIntRange for Int MAX_VALUE`() {
        assertTrue(Int.MAX_VALUE.isInIntRange)
    }

    @Test
    fun `should return true from isInIntRange for Int MIN_VALUE`() {
        assertTrue(Int.MIN_VALUE.isInIntRange)
    }

    @Test
    fun `should return true from isInIntRange for zero`() {
        assertTrue(0.isInIntRange)
    }

    @Test
    fun `should return false from isInIntRange for Long MAX_VALUE`() {
        assertFalse(Long.MAX_VALUE.isInIntRange)
    }

    // --- non-finite and fractional inputs (BigDecimal-exact path) ---

    @Test
    fun `should return false from isInLongRange for NaN`() {
        assertFalse(Double.NaN.isInLongRange)
    }

    @Test
    fun `should return false from isInIntRange for NaN`() {
        assertFalse(Double.NaN.isInIntRange)
    }

    @Test
    fun `should return false from isInShortRange for NaN`() {
        assertFalse(Double.NaN.isInShortRange)
    }

    @Test
    fun `should return false from isInByteRange for NaN`() {
        assertFalse(Double.NaN.isInByteRange)
    }

    @Test
    fun `should return false from isInIntRange for positive infinity`() {
        assertFalse(Double.POSITIVE_INFINITY.isInIntRange)
    }

    @Test
    fun `should return false from isInIntRange for negative infinity`() {
        assertFalse(Double.NEGATIVE_INFINITY.isInIntRange)
    }

    @Test
    fun `should return true from isInIntRange for fractional value inside range`() {
        assertTrue(2.5.isInIntRange)
    }

    @Test
    fun `should return false from isInIntRange for fractional value beyond Int MAX`() {
        assertFalse((Int.MAX_VALUE.toDouble() + 0.5).isInIntRange)
    }

    @Test
    fun `should return false from isInShortRange for double beyond Long precision`() {
        assertFalse(1.0E19.isInShortRange)
    }

    // --- Number.isInShortRange ---

    @Test
    fun `should return true from isInShortRange for Short MAX_VALUE`() {
        assertTrue(Short.MAX_VALUE.isInShortRange)
    }

    @Test
    fun `should return true from isInShortRange for Short MIN_VALUE`() {
        assertTrue(Short.MIN_VALUE.isInShortRange)
    }

    @Test
    fun `should return true from isInShortRange for zero`() {
        assertTrue(0.isInShortRange)
    }

    @Test
    fun `should return false from isInShortRange for Int MAX_VALUE`() {
        assertFalse(Int.MAX_VALUE.isInShortRange)
    }

    // --- Number.isInByteRange ---

    @Test
    fun `should return true from isInByteRange for Byte MAX_VALUE`() {
        assertTrue(Byte.MAX_VALUE.isInByteRange)
    }

    @Test
    fun `should return true from isInByteRange for Byte MIN_VALUE`() {
        assertTrue(Byte.MIN_VALUE.isInByteRange)
    }

    @Test
    fun `should return true from isInByteRange for zero`() {
        assertTrue(0.isInByteRange)
    }

    @Test
    fun `should return false from isInByteRange for Short MAX_VALUE`() {
        assertFalse(Short.MAX_VALUE.isInByteRange)
    }
}
