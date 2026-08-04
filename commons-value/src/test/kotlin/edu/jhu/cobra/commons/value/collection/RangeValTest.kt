package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.RangeVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [RangeVal] derived from design-collection.md.
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
 *
 * infix before / after:
 * - `should return true when range ends before other starts`
 * - `should return false when ranges overlap for before`
 * - `should return true when range starts after other ends`
 * - `should return false when ranges overlap for after`
 *
 * plus:
 * - `should combine disjoint ranges into union bounds`
 * - `should combine overlapping ranges into union bounds`
 * - `should preserve IntVal precision on plus`
 *
 * map:
 * - `should transform start and end via map`
 *
 * Boundary:
 * - `should handle single-point range`
 * - `should handle negative range`
 * - `should handle large value range`
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

    // -- infix before --

    @Test
    fun `should return true when range ends before other starts`() {
        val r1 = RangeVal(1, 3)
        val r2 = RangeVal(4, 6)
        assertTrue(r1 before r2)
    }

    @Test
    fun `should return false when ranges overlap for before`() {
        val r1 = RangeVal(1, 5)
        val r2 = RangeVal(3, 8)
        assertFalse(r1 before r2)
    }

    // -- infix after --

    @Test
    fun `should return true when range starts after other ends`() {
        val r1 = RangeVal(5, 8)
        val r2 = RangeVal(1, 3)
        assertTrue(r1 after r2)
    }

    @Test
    fun `should return false when ranges overlap for after`() {
        val r1 = RangeVal(3, 8)
        val r2 = RangeVal(1, 5)
        assertFalse(r1 after r2)
    }

    // -- plus --

    @Test
    fun `should combine disjoint ranges into union bounds`() {
        val r1 = RangeVal(1, 3)
        val r2 = RangeVal(7, 10)
        val combined = r1 + r2
        assertEquals(1L, combined.first)
        assertEquals(10L, combined.last)
    }

    @Test
    fun `should combine overlapping ranges into union bounds`() {
        val r1 = RangeVal(1, 5)
        val r2 = RangeVal(3, 8)
        val combined = r1 + r2
        assertEquals(1L, combined.first)
        assertEquals(8L, combined.last)
    }

    @Test
    fun `should preserve IntVal precision on plus`() {
        val r1 = RangeVal(IntVal(1L), IntVal(3L))
        val r2 = RangeVal(IntVal(2L), IntVal(5L))
        val combined = r1 + r2
        assertEquals(IntVal(1L), combined.start)
        assertEquals(IntVal(5L), combined.endInclusive)
    }

    // -- map --

    @Test
    fun `should transform start and end via map`() {
        val range = RangeVal(2, 6)
        val mapped = range.map { it.toInt() * 3 }
        assertEquals(2, mapped.size)
        assertEquals(6, mapped[0])
        assertEquals(18, mapped[1])
    }

    // -- Boundary --

    @Test
    fun `should handle single-point range`() {
        val range = RangeVal(5, 5)
        assertTrue(5 in range)
        assertFalse(4 in range)
        assertFalse(6 in range)
        assertEquals(range.first, range.last)
    }

    @Test
    fun `should handle negative range`() {
        val range = RangeVal(-10, -1)
        assertTrue(-10 in range)
        assertTrue(-5 in range)
        assertTrue(-1 in range)
        assertFalse(-11 in range)
        assertFalse(0 in range)
    }

    @Test
    fun `should handle large value range`() {
        val range = RangeVal(Long.MIN_VALUE, Long.MAX_VALUE)
        assertTrue(0L in range)
        assertTrue(Long.MIN_VALUE in range)
        assertTrue(Long.MAX_VALUE in range)
        assertEquals(Long.MIN_VALUE, range.first)
        assertEquals(Long.MAX_VALUE, range.last)
    }
}
