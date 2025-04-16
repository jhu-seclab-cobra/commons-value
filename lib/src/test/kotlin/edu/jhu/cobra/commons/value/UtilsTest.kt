package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class UtilsTest {
    @Test
    fun testNullToVal() {
        val result = null.toVal
        assertIs<NullVal>(result)
    }

    @Test
    fun testNumberToVal() {
        val intVal = 42.toVal
        assertIs<NumVal>(intVal)
        assertEquals(42, intVal.core)

        val doubleVal = 3.14.toVal
        assertIs<NumVal>(doubleVal)
        assertEquals(3.14, doubleVal.core)
    }

    @Test
    fun testStringToVal() {
        val result = "test".toVal
        assertIs<StrVal>(result)
        assertEquals("test", result.core)
    }

    @Test
    fun testBooleanToVal() {
        val trueVal = true.toVal
        assertIs<BoolVal>(trueVal)
        assertEquals(true, trueVal.core)

        val falseVal = false.toVal
        assertIs<BoolVal>(falseVal)
        assertEquals(false, falseVal.core)
    }

    @Test
    fun testListToVal() {
        val list = listOf(1, 2, 3)
        val result = list.toVal
        assertIs<ListVal>(result)
        assertEquals(list, result.map { it.core })
    }

    @Test
    fun testMapToVal() {
        val map = mapOf("key" to "value")
        val result = map.toVal
        assertIs<MapVal>(result)
        assertEquals(map, result.mapValues { it.value.core })
    }

    @Test
    fun testRangeToVal() {
        val range = 1..10
        val result = range.toVal
        assertIs<RangeVal>(result)
        assertEquals(range.first, result.first)
        assertEquals(range.last, result.last)
    }

    @Test
    fun testSetToVal() {
        val set = setOf(1, 2, 3)
        val result = set.toVal
        assertIs<SetVal>(result)
        assertEquals(set, result.map { it.core }.toSet())
    }

    @Test
    fun testIValueToVal() {
        val numVal = NumVal(42)
        val result = numVal.toVal
        assertIs<NumVal>(result)
        assertEquals(numVal, result)
    }

    @Test
    fun testInvalidTypeToVal() {
        assertFailsWith<IllegalArgumentException> {
            Object().toVal
        }
    }
} 