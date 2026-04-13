package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.RangeVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [RangeVal] derived from design-collection.md.
 *
 * Constructors:
 * - `should create range from NumVal pair`
 * - `should create range from Number pair`
 *
 * Properties (start, endInclusive, first, last, core):
 * - `should expose start and endInclusive as NumVal`
 * - `should expose first and last as Number`
 * - `should return core as list of start and endInclusive`
 *
 * contains(Number):
 * - `should contain number at start boundary`
 * - `should contain number at end boundary`
 * - `should contain number within range`
 * - `should not contain number below start`
 * - `should not contain number above end`
 *
 * contains(NumVal):
 * - `should contain NumVal within range`
 * - `should not contain NumVal outside range`
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
 * - `should preserve NumVal precision on plus`
 *
 * map:
 * - `should transform start and end via map`
 *
 * Boundary:
 * - `should handle single-point range`
 * - `should handle negative range`
 * - `should handle floating-point range`
 */
internal class RangeValTest {

    // -- Constructors --

    @Test
    fun `should create range from NumVal pair`() {
        val range = RangeVal(NumVal(1), NumVal(10))
        assertEquals(NumVal(1), range.start)
        assertEquals(NumVal(10), range.endInclusive)
    }

    @Test
    fun `should create range from Number pair`() {
        val range = RangeVal(1, 10)
        assertEquals(1, range.first)
        assertEquals(10, range.last)
    }

    // -- Properties --

    @Test
    fun `should expose start and endInclusive as NumVal`() {
        val range = RangeVal(NumVal(3), NumVal(7))
        assertEquals(NumVal(3), range.start)
        assertEquals(NumVal(7), range.endInclusive)
    }

    @Test
    fun `should expose first and last as Number`() {
        val range = RangeVal(NumVal(3), NumVal(7))
        assertEquals(3, range.first)
        assertEquals(7, range.last)
    }

    @Test
    fun `should return core as list of start and endInclusive`() {
        val range = RangeVal(NumVal(2), NumVal(8))
        val core = range.core
        assertEquals(2, core.size)
        assertEquals(NumVal(2), core[0])
        assertEquals(NumVal(8), core[1])
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

    // -- contains(NumVal) --

    @Test
    fun `should contain NumVal within range`() {
        val range = RangeVal(1, 5)
        assertTrue(NumVal(3) in range)
        assertTrue(NumVal(1) in range)
        assertTrue(NumVal(5) in range)
    }

    @Test
    fun `should not contain NumVal outside range`() {
        val range = RangeVal(1, 5)
        assertFalse(NumVal(0) in range)
        assertFalse(NumVal(6) in range)
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
        assertEquals(1, combined.first)
        assertEquals(10, combined.last)
    }

    @Test
    fun `should combine overlapping ranges into union bounds`() {
        val r1 = RangeVal(1, 5)
        val r2 = RangeVal(3, 8)
        val combined = r1 + r2
        assertEquals(1, combined.first)
        assertEquals(8, combined.last)
    }

    @Test
    fun `should preserve NumVal precision on plus`() {
        val r1 = RangeVal(NumVal(1.5), NumVal(3.5))
        val r2 = RangeVal(NumVal(2.0), NumVal(5.0))
        val combined = r1 + r2
        assertEquals(NumVal(1.5), combined.start)
        assertEquals(NumVal(5.0), combined.endInclusive)
    }

    // -- map --

    @Test
    fun `should transform start and end via map`() {
        val range = RangeVal(2, 6)
        val mapped = range.map { it.core.toInt() * 3 }
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
    fun `should handle floating-point range`() {
        val range = RangeVal(1.5, 3.5)
        assertTrue(1.5 in range)
        assertTrue(2.0 in range)
        assertTrue(3.5 in range)
        assertFalse(1.4 in range)
        assertFalse(3.6 in range)
    }
}
