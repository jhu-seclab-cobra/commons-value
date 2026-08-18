package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Black-box tests for [RangeVal] operations derived from design-collection.md.
 *
 * infix before / after:
 * - `should return true when range ends before other starts`
 * - `should return false when ranges overlap for before`
 * - `should return false from before when ranges share a boundary point`
 * - `should return true when range starts after other ends`
 * - `should return false when ranges overlap for after`
 * - `should return false from after when ranges share a boundary point`
 *
 * plus:
 * - `should combine disjoint ranges into union bounds`
 * - `should combine overlapping ranges into union bounds`
 * - `should preserve IntVal precision on plus`
 *
 * map:
 * - `should transform start and end via map`
 *
 * deepCopy:
 * - `should return same instance from deepCopy`
 *
 * Boundary:
 * - `should handle single-point range`
 * - `should handle negative range`
 * - `should handle large value range`
 */
internal class RangeValOperationsTest {
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

    @Test
    fun `should return false from before when ranges share a boundary point`() {
        val r1 = RangeVal(0, 5)
        val r2 = RangeVal(5, 10)
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

    @Test
    fun `should return false from after when ranges share a boundary point`() {
        val r1 = RangeVal(5, 10)
        val r2 = RangeVal(0, 5)
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

    // -- deepCopy --

    @Test
    fun `should return same instance from deepCopy`() {
        val range = RangeVal(1L, 10L)
        assertSame(range, range.deepCopy())
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
