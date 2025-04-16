package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.*

class StrValTest {
    @Test
    fun testDefaultConstructor() {
        val emptyStr = StrVal()
        assertEquals("", emptyStr.core)
    }

    @Test
    fun testParameterizedConstructor() {
        val strVal = StrVal("test")
        assertEquals("test", strVal.core)
    }

    @Test
    fun testStartsWith() {
        val strVal = StrVal("Hello World")
        assertTrue(strVal.startsWith("Hello"))
        assertFalse(strVal.startsWith("World"))
    }

    @Test
    fun testSubstringAfter() {
        val strVal = StrVal("Hello World")
        assertEquals("World", strVal.substringAfter("Hello "))
        assertEquals("Hello World", strVal.substringAfter("not found"))
    }

    @Test
    fun testSubstringBefore() {
        val strVal = StrVal("Hello World")
        assertEquals("Hello", strVal.substringBefore(" World"))
        assertEquals("Hello World", strVal.substringBefore("not found"))
    }

    @Test
    fun testEqualsString() {
        val strVal = StrVal("Test")
        assertTrue(strVal.equals("Test", ignoreCase = false))
        assertTrue(strVal.equals("test", ignoreCase = true))
        assertFalse(strVal.equals("test", ignoreCase = false))
    }

    @Test
    fun testEqualsIPrimitiveVal() {
        val strVal = StrVal("Test")
        val otherStrVal = StrVal("Test")
        val differentStrVal = StrVal("Different")
        val numVal = NumVal(42)

        assertTrue(strVal.equals(otherStrVal, ignoreCase = false))
        assertTrue(strVal.equals(StrVal("test"), ignoreCase = true))
        assertFalse(strVal.equals(differentStrVal, ignoreCase = false))
        assertFalse(strVal.equals(numVal, ignoreCase = false))
    }

    @Test
    fun testUppercase() {
        val strVal = StrVal("test")
        assertEquals("TEST", strVal.uppercase().core)
    }

    @Test
    fun testLowercase() {
        val strVal = StrVal("TEST")
        assertEquals("test", strVal.lowercase().core)
    }

    @Test
    fun testTrim() {
        val strVal = StrVal("  test  ")
        assertEquals("test", strVal.trim().core)
    }

    @Test
    fun testContains() {
        val strVal = StrVal("Hello World")
        assertTrue(strVal.contains("Hello"))
        assertTrue(strVal.contains("World"))
        assertFalse(strVal.contains("Not"))
    }

    @Test
    fun testLength() {
        assertEquals(0, StrVal().length)
        assertEquals(11, StrVal("Hello World").length)
    }

    @Test
    fun testGetOperator() {
        val strVal = StrVal("Hello")
        assertEquals('H', strVal[0])
        assertEquals('e', strVal[1])
        assertEquals('o', strVal[-1])
        assertEquals('l', strVal[-2])
    }

    @Test
    fun testGetOperatorWithNumVal() {
        val strVal = StrVal("Hello")
        assertEquals('H', strVal[NumVal(0)])
        assertEquals('e', strVal[NumVal(1)])
        assertEquals('o', strVal[NumVal(-1)])
        assertEquals('l', strVal[NumVal(-2)])
    }

    @Test
    fun testToString() {
        assertEquals("StrVal{test}", StrVal("test").toString())
        assertEquals("StrVal{}", StrVal().toString())
    }

    @Test
    fun testEquality() {
        assertEquals(StrVal("test"), StrVal("test"))
        assertNotEquals(StrVal("test"), StrVal("different"))
    }

    @Test
    fun testHashCode() {
        assertEquals(StrVal("test").hashCode(), StrVal("test").hashCode())
        assertNotEquals(StrVal("test").hashCode(), StrVal("different").hashCode())
    }
} 