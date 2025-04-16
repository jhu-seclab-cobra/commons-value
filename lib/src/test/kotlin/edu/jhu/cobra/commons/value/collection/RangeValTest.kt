package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.RangeVal
import kotlin.test.*

class RangeValTest {
    @Test
    fun testDefaultConstructor() {
        val rangeVal = RangeVal()
        assertFailsWith<NoSuchElementException> { rangeVal.first }
        assertFailsWith<NoSuchElementException> { rangeVal.last }
    }

    @Test
    fun testNumValConstructor() {
        val rangeVal = RangeVal(NumVal(1), NumVal(5))
        assertEquals(1, rangeVal.first)
        assertEquals(5, rangeVal.last)
    }

    @Test
    fun testNumberConstructor() {
        val rangeVal = RangeVal(1, 5)
        assertEquals(1, rangeVal.first)
        assertEquals(5, rangeVal.last)
    }

    @Test
    fun testContainsNumber() {
        val rangeVal = RangeVal(1, 5)
        assertTrue(1 in rangeVal)
        assertTrue(3 in rangeVal)
        assertTrue(5 in rangeVal)
        assertFalse(0 in rangeVal)
        assertFalse(6 in rangeVal)
    }

    @Test
    fun testContainsNumVal() {
        val rangeVal = RangeVal(1, 5)
        assertTrue(NumVal(1) in rangeVal)
        assertTrue(NumVal(3) in rangeVal)
        assertTrue(NumVal(5) in rangeVal)
        assertFalse(NumVal(0) in rangeVal)
        assertFalse(NumVal(6) in rangeVal)
    }

    @Test
    fun testContainsRange() {
        val rangeVal = RangeVal(1, 5)
        assertTrue(RangeVal(2, 4) in rangeVal)
        assertTrue(RangeVal(1, 5) in rangeVal)
        assertFalse(RangeVal(0, 3) in rangeVal)
        assertFalse(RangeVal(3, 6) in rangeVal)
        assertFalse(RangeVal(0, 6) in rangeVal)
    }

    @Test
    fun testBefore() {
        val range1 = RangeVal(1, 3)
        val range2 = RangeVal(4, 6)
        val range3 = RangeVal(2, 4)
        assertTrue(range1 before range2)
        assertFalse(range1 before range3)
        assertFalse(range2 before range1)
    }

    @Test
    fun testAfter() {
        val range1 = RangeVal(4, 6)
        val range2 = RangeVal(1, 3)
        val range3 = RangeVal(2, 5)
        assertTrue(range1 after range2)
        assertFalse(range1 after range3)
        assertFalse(range2 after range1)
    }

    @Test
    fun testPlusOperator() {
        val range1 = RangeVal(1, 3)
        val range2 = RangeVal(4, 6)
        val range3 = RangeVal(2, 5)

        val combined1 = range1 + range2
        assertEquals(1, combined1.first)
        assertEquals(6, combined1.last)

        val combined2 = range1 + range3
        assertEquals(1, combined2.first)
        assertEquals(5, combined2.last)
    }

    @Test
    fun testMap() {
        val rangeVal = RangeVal(1, 3)
        val mapped = rangeVal.map { it.core.toInt() * 2 }
        assertEquals(2, mapped.size)
        assertEquals(2, mapped[0])
        assertEquals(6, mapped[1])
    }

    @Test
    fun testToString() {
        val rangeVal = RangeVal(1, 5)
        assertEquals("1:5", rangeVal.toString())
    }

    @Test
    fun testFloatingPointRanges() {
        val rangeVal = RangeVal(1.5, 5.5)
        assertTrue(1.5 in rangeVal)
        assertTrue(3.0 in rangeVal)
        assertTrue(5.5 in rangeVal)
        assertFalse(1.4 in rangeVal)
        assertFalse(5.6 in rangeVal)
    }

    @Test
    fun testNegativeRanges() {
        val rangeVal = RangeVal(-5, -1)
        assertTrue(-5 in rangeVal)
        assertTrue(-3 in rangeVal)
        assertTrue(-1 in rangeVal)
        assertFalse(-6 in rangeVal)
        assertFalse(0 in rangeVal)
    }

    @Test
    fun testSingleValueRange() {
        val rangeVal = RangeVal(42, 42)
        assertTrue(42 in rangeVal)
        assertFalse(41 in rangeVal)
        assertFalse(43 in rangeVal)
    }

    @Test
    fun testLargeRanges() {
        val rangeVal = RangeVal(Int.MIN_VALUE, Int.MAX_VALUE)
        assertTrue(Int.MIN_VALUE in rangeVal)
        assertTrue(0 in rangeVal)
        assertTrue(Int.MAX_VALUE in rangeVal)
    }
} 