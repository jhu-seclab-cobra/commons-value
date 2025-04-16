package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.NumVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumValTest {
    @Test
    fun testDefaultConstructor() {
        val defaultNum = NumVal()
        assertEquals(0, defaultNum.core)
    }

    @Test
    fun testParameterizedConstructor() {
        // Test integer
        val intVal = NumVal(42)
        assertEquals(42, intVal.core)

        // Test double
        val doubleVal = NumVal(3.14)
        assertEquals(3.14, doubleVal.core)

        // Test long
        val longVal = NumVal(Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE, longVal.core)

        // Test float
        val floatVal = NumVal(1.5f)
        assertEquals(1.5f, floatVal.core)

        // Test short
        val shortVal = NumVal(42.toShort())
        assertEquals(42.toShort(), shortVal.core)

        // Test byte
        val byteVal = NumVal(42.toByte())
        assertEquals(42.toByte(), byteVal.core)
    }

    @Test
    fun testToInt() {
        assertEquals(42, NumVal(42).toInt())
        assertEquals(3, NumVal(3.14).toInt())
        assertEquals(Int.MAX_VALUE, NumVal(Int.MAX_VALUE.toLong()).toInt())
    }

    @Test
    fun testToLong() {
        assertEquals(42L, NumVal(42).toLong())
        assertEquals(3L, NumVal(3.14).toLong())
        assertEquals(Long.MAX_VALUE, NumVal(Long.MAX_VALUE).toLong())
    }

    @Test
    fun testToDouble() {
        assertEquals(42.0, NumVal(42).toDouble())
        assertEquals(3.14, NumVal(3.14).toDouble())
        assertEquals(Long.MAX_VALUE.toDouble(), NumVal(Long.MAX_VALUE).toDouble())
    }

    @Test
    fun testToFloat() {
        assertEquals(42.0f, NumVal(42).toFloat())
        assertEquals(3.14f, NumVal(3.14).toFloat())
        assertEquals(Long.MAX_VALUE.toFloat(), NumVal(Long.MAX_VALUE).toFloat())
    }

    @Test
    fun testToShort() {
        assertEquals(42.toShort(), NumVal(42).toShort())
        assertEquals(3.toShort(), NumVal(3.14).toShort())
    }

    @Test
    fun testTypeChecks() {
        // Test isInt
        assertTrue(NumVal(42).isInt)
        assertFalse(NumVal(42L).isInt)

        // Test isLong
        assertTrue(NumVal(42L).isLong)
        assertFalse(NumVal(42).isLong)

        // Test isShort
        assertTrue(NumVal(42.toShort()).isShort)
        assertFalse(NumVal(42).isShort)

        // Test isByte
        assertTrue(NumVal(42.toByte()).isByte)
        assertFalse(NumVal(42).isByte)

        // Test isFloat
        assertTrue(NumVal(42.0f).isFloat)
        assertFalse(NumVal(42.0).isFloat)

        // Test isDouble
        assertTrue(NumVal(42.0).isDouble)
        assertFalse(NumVal(42).isDouble)

        // Test isPrimitiveIntegerType
        assertTrue(NumVal(42).isPrimitiveIntegerType)
        assertTrue(NumVal(42L).isPrimitiveIntegerType)
        assertTrue(NumVal(42.toShort()).isPrimitiveIntegerType)
        assertTrue(NumVal(42.toByte()).isPrimitiveIntegerType)
        assertFalse(NumVal(42.0).isPrimitiveIntegerType)

        // Test isPrimitiveFloatingType
        assertTrue(NumVal(42.0f).isPrimitiveFloatingType)
        assertTrue(NumVal(42.0).isPrimitiveFloatingType)
        assertFalse(NumVal(42).isPrimitiveFloatingType)
    }

    @Test
    fun testToString() {
        assertEquals("NumVal{42}", NumVal(42).toString())
        assertEquals("NumVal{3.14}", NumVal(3.14).toString())
        assertEquals("NumVal{${Long.MAX_VALUE}}", NumVal(Long.MAX_VALUE).toString())
    }

    @Test
    fun testCompareTo() {
        assertTrue(NumVal(42) > 40)
        assertTrue(NumVal(42) < 50)
        assertEquals(0, NumVal(42).compareTo(42))
    }

    @Test
    fun testTruncate() {
        // Test truncate to Byte
        assertEquals(Byte.MAX_VALUE, NumVal.truncate(NumVal(Byte.MAX_VALUE)).core)

        // Test truncate to Short
        assertEquals(Short.MAX_VALUE, NumVal.truncate(NumVal(Short.MAX_VALUE)).core)

        // Test truncate to Int
        assertEquals(Int.MAX_VALUE, NumVal.truncate(NumVal(Int.MAX_VALUE)).core)

        // Test truncate to Long
        assertEquals(Long.MAX_VALUE, NumVal.truncate(NumVal(Long.MAX_VALUE)).core)
    }
} 