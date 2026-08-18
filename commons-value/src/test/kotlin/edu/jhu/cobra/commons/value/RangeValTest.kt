package edu.jhu.cobra.commons.value

import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [RangeVal] constructors, access, and containment
 * derived from design-collection.md.
 *
 * Constructors:
 * - `should create range from IntVal pair`
 * - `should create range from Number pair`
 *
 * Properties (start, endInclusive, first, last, core):
 * - `should expose start and endInclusive as IntVal`
 * - `should expose first and last as Long`
 * - `should return core as list of start and endInclusive`
 *
 * contains(Number):
 * - `should contain number at start boundary`
 * - `should contain number at end boundary`
 * - `should contain number within range`
 * - `should not contain number below start`
 * - `should not contain number above end`
 * - `should contain fractional Double within range`
 * - `should compare integral Number arguments without Double rounding`
 * - `should compare floating Number arguments exactly at large bounds`
 * - `should not contain non-finite Double`
 * - `should compare BigInteger argument exactly at large bounds`
 * - `should compare BigDecimal argument exactly at boundary`
 * - `should compare AtomicLong argument exactly at large bounds`
 *
 * contains(IntVal):
 * - `should contain IntVal within range`
 * - `should not contain IntVal outside range`
 *
 * contains(RangeVal):
 * - `should contain sub-range fully inside`
 * - `should contain identical range`
 * - `should not contain range extending below start`
 * - `should not contain range extending above end`
 */
internal class RangeValTest {
    // -- Constructors --

    @Test
    fun `should create range from IntVal pair`() {
        val range = RangeVal(IntVal(1L), IntVal(10L))
        assertEquals(IntVal(1L), range.start)
        assertEquals(IntVal(10L), range.endInclusive)
    }

    @Test
    fun `should create range from Number pair`() {
        val range = RangeVal(1, 10)
        assertEquals(1L, range.first)
        assertEquals(10L, range.last)
    }

    // -- Properties --

    @Test
    fun `should expose start and endInclusive as IntVal`() {
        val range = RangeVal(IntVal(3L), IntVal(7L))
        assertEquals(IntVal(3L), range.start)
        assertEquals(IntVal(7L), range.endInclusive)
    }

    @Test
    fun `should expose first and last as Long`() {
        val range = RangeVal(IntVal(3L), IntVal(7L))
        assertEquals(3L, range.first)
        assertEquals(7L, range.last)
    }

    @Test
    fun `should return core as list of start and endInclusive`() {
        val range = RangeVal(IntVal(2L), IntVal(8L))
        val core = range.core
        assertEquals(2, core.size)
        assertEquals(IntVal(2L), core[0])
        assertEquals(IntVal(8L), core[1])
    }

    // -- contains(Number) --

    @Test
    fun `should contain number at start boundary`() {
        val range = RangeVal(1, 5)
        assertTrue(1 in range)
    }

    @Test
    fun `should contain number at end boundary`() {
        val range = RangeVal(1, 5)
        assertTrue(5 in range)
    }

    @Test
    fun `should contain number within range`() {
        val range = RangeVal(1, 5)
        assertTrue(3 in range)
    }

    @Test
    fun `should not contain number below start`() {
        val range = RangeVal(1, 5)
        assertFalse(0 in range)
    }

    @Test
    fun `should not contain number above end`() {
        val range = RangeVal(1, 5)
        assertFalse(6 in range)
    }

    @Test
    fun `should contain fractional Double within range`() {
        val range = RangeVal(1, 5)
        assertTrue(2.5 in range)
        assertFalse(0.5 in range)
        assertFalse(5.5 in range)
    }

    @Test
    fun `should compare integral Number arguments without Double rounding`() {
        val range = RangeVal(Long.MAX_VALUE - 100, Long.MAX_VALUE)
        val below: Number = Long.MAX_VALUE - 1024
        val inside: Number = Long.MAX_VALUE - 50
        assertFalse(below in range)
        assertTrue(inside in range)
    }

    @Test
    fun `should compare floating Number arguments exactly at large bounds`() {
        val range = RangeVal(0, Long.MAX_VALUE)
        val above: Number = Long.MAX_VALUE.toDouble()
        assertFalse(above in range)
    }

    @Test
    fun `should not contain non-finite Double`() {
        val range = RangeVal(Long.MIN_VALUE, Long.MAX_VALUE)
        assertFalse(Double.NaN in range)
        assertFalse(Double.POSITIVE_INFINITY in range)
        assertFalse(Double.NEGATIVE_INFINITY in range)
    }

    @Test
    fun `should compare BigInteger argument exactly at large bounds`() {
        val range = RangeVal(0, Long.MAX_VALUE)
        val atEnd: Number = BigInteger.valueOf(Long.MAX_VALUE)
        assertTrue(atEnd in range)
    }

    @Test
    fun `should compare BigDecimal argument exactly at boundary`() {
        val range = RangeVal(1, 10)
        val justAbove: Number = BigDecimal("10.0000000000000000001")
        assertFalse(justAbove in range)
    }

    @Test
    fun `should compare AtomicLong argument exactly at large bounds`() {
        val range = RangeVal(0, Long.MAX_VALUE)
        val atEnd: Number = AtomicLong(Long.MAX_VALUE)
        assertTrue(atEnd in range)
    }

    // -- contains(IntVal) --

    @Test
    fun `should contain IntVal within range`() {
        val range = RangeVal(1, 5)
        assertTrue(IntVal(3L) in range)
        assertTrue(IntVal(1L) in range)
        assertTrue(IntVal(5L) in range)
    }

    @Test
    fun `should not contain IntVal outside range`() {
        val range = RangeVal(1, 5)
        assertFalse(IntVal(0L) in range)
        assertFalse(IntVal(6L) in range)
    }

    // -- contains(RangeVal) --

    @Test
    fun `should contain sub-range fully inside`() {
        val range = RangeVal(1, 10)
        assertTrue(RangeVal(2, 5) in range)
    }

    @Test
    fun `should contain identical range`() {
        val range = RangeVal(1, 10)
        assertTrue(RangeVal(1, 10) in range)
    }

    @Test
    fun `should not contain range extending below start`() {
        val range = RangeVal(1, 10)
        assertFalse(RangeVal(0, 5) in range)
    }

    @Test
    fun `should not contain range extending above end`() {
        val range = RangeVal(1, 10)
        assertFalse(RangeVal(5, 11) in range)
    }

}
