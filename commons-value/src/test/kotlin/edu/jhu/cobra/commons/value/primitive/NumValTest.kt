package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.IPrimitiveVal
import edu.jhu.cobra.commons.value.NumVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [NumVal] derived from the design doc.
 *
 * - `should store Byte core` — primary constructor with Byte
 * - `should store Short core` — primary constructor with Short
 * - `should store Int core` — primary constructor with Int
 * - `should store Long core` — primary constructor with Long
 * - `should store Float core` — primary constructor with Float
 * - `should store Double core` — primary constructor with Double
 * - `should default to zero when no argument` — default constructor
 * - `should store Byte MIN and MAX` — boundary values
 * - `should store Short MIN and MAX` — boundary values
 * - `should store Int MIN and MAX` — boundary values
 * - `should store Long MIN and MAX` — boundary values
 * - `should store Float MIN and MAX` — boundary values
 * - `should store Double MIN and MAX` — boundary values
 * - `should convert toInt from Int` — toInt identity
 * - `should convert toInt from Double truncating` — toInt truncation
 * - `should convert toDouble from Int` — toDouble widening
 * - `should convert toDouble from Double` — toDouble identity
 * - `should convert toFloat from Int` — toFloat widening
 * - `should convert toFloat from Float` — toFloat identity
 * - `should convert toLong from Int` — toLong widening
 * - `should convert toLong from Long` — toLong identity
 * - `should convert toShort from Int` — toShort narrowing
 * - `should return positive when greater than int` — compareTo(Int) >
 * - `should return negative when less than int` — compareTo(Int) <
 * - `should return zero when equal to int` — compareTo(Int) ==
 * - `should compare Double core against int` — compareTo uses Double comparison
 * - `should truncate value fitting in Byte to Byte` — truncate Byte
 * - `should truncate value fitting in Short to Short` — truncate Short
 * - `should truncate value fitting in Int to Int` — truncate Int
 * - `should truncate Long MAX_VALUE to Long` — truncate Long
 * - `should truncate floating point to smallest integer type` — truncate converts float to integer
 * - `should report isInt true for Int core` — type introspection
 * - `should report isLong true for Long core` — type introspection
 * - `should report isShort true for Short core` — type introspection
 * - `should report isByte true for Byte core` — type introspection
 * - `should report isFloat true for Float core` — type introspection
 * - `should report isDouble true for Double core` — type introspection
 * - `should report isPrimitiveIntegerType for integer subtypes` — Byte, Short, Int, Long
 * - `should report isPrimitiveFloatingType for floating subtypes` — Float, Double
 * - `should implement IPrimitiveVal` — type hierarchy
 */
internal class NumValTest {

    // --- Core storage per Number subtype ---

    @Test
    fun `should store Byte core`() {
        assertEquals(42.toByte(), NumVal(42.toByte()).core)
    }

    @Test
    fun `should store Short core`() {
        assertEquals(42.toShort(), NumVal(42.toShort()).core)
    }

    @Test
    fun `should store Int core`() {
        assertEquals(42, NumVal(42).core)
    }

    @Test
    fun `should store Long core`() {
        assertEquals(42L, NumVal(42L).core)
    }

    @Test
    fun `should store Float core`() {
        assertEquals(3.14f, NumVal(3.14f).core)
    }

    @Test
    fun `should store Double core`() {
        assertEquals(3.14, NumVal(3.14).core)
    }

    // --- Default constructor ---

    @Test
    fun `should default to zero when no argument`() {
        assertEquals(0, NumVal().core)
    }

    // --- Boundary values ---

    @Test
    fun `should store Byte MIN and MAX`() {
        assertEquals(Byte.MIN_VALUE, NumVal(Byte.MIN_VALUE).core)
        assertEquals(Byte.MAX_VALUE, NumVal(Byte.MAX_VALUE).core)
    }

    @Test
    fun `should store Short MIN and MAX`() {
        assertEquals(Short.MIN_VALUE, NumVal(Short.MIN_VALUE).core)
        assertEquals(Short.MAX_VALUE, NumVal(Short.MAX_VALUE).core)
    }

    @Test
    fun `should store Int MIN and MAX`() {
        assertEquals(Int.MIN_VALUE, NumVal(Int.MIN_VALUE).core)
        assertEquals(Int.MAX_VALUE, NumVal(Int.MAX_VALUE).core)
    }

    @Test
    fun `should store Long MIN and MAX`() {
        assertEquals(Long.MIN_VALUE, NumVal(Long.MIN_VALUE).core)
        assertEquals(Long.MAX_VALUE, NumVal(Long.MAX_VALUE).core)
    }

    @Test
    fun `should store Float MIN and MAX`() {
        assertEquals(Float.MIN_VALUE, NumVal(Float.MIN_VALUE).core)
        assertEquals(Float.MAX_VALUE, NumVal(Float.MAX_VALUE).core)
    }

    @Test
    fun `should store Double MIN and MAX`() {
        assertEquals(Double.MIN_VALUE, NumVal(Double.MIN_VALUE).core)
        assertEquals(Double.MAX_VALUE, NumVal(Double.MAX_VALUE).core)
    }

    // --- Conversion methods ---

    @Test
    fun `should convert toInt from Int`() {
        assertEquals(42, NumVal(42).toInt())
    }

    @Test
    fun `should convert toInt from Double truncating`() {
        assertEquals(3, NumVal(3.14).toInt())
    }

    @Test
    fun `should convert toDouble from Int`() {
        assertEquals(42.0, NumVal(42).toDouble())
    }

    @Test
    fun `should convert toDouble from Double`() {
        assertEquals(3.14, NumVal(3.14).toDouble())
    }

    @Test
    fun `should convert toFloat from Int`() {
        assertEquals(42.0f, NumVal(42).toFloat())
    }

    @Test
    fun `should convert toFloat from Float`() {
        assertEquals(1.5f, NumVal(1.5f).toFloat())
    }

    @Test
    fun `should convert toLong from Int`() {
        assertEquals(42L, NumVal(42).toLong())
    }

    @Test
    fun `should convert toLong from Long`() {
        assertEquals(Long.MAX_VALUE, NumVal(Long.MAX_VALUE).toLong())
    }

    @Test
    fun `should convert toShort from Int`() {
        assertEquals(42.toShort(), NumVal(42).toShort())
    }

    // --- compareTo(Int) ---

    @Test
    fun `should return positive when greater than int`() {
        assertTrue(NumVal(42).compareTo(10) > 0)
    }

    @Test
    fun `should return negative when less than int`() {
        assertTrue(NumVal(5).compareTo(10) < 0)
    }

    @Test
    fun `should return zero when equal to int`() {
        assertEquals(0, NumVal(42).compareTo(42))
    }

    @Test
    fun `should compare Double core against int`() {
        assertTrue(NumVal(3.5).compareTo(3) > 0)
    }

    // --- truncate ---

    @Test
    fun `should truncate value fitting in Byte to Byte`() {
        val result = NumVal.truncate(NumVal(Byte.MAX_VALUE.toInt()))
        assertEquals(Byte.MAX_VALUE, result.core)
        assertTrue(result.isByte)
    }

    @Test
    fun `should truncate value fitting in Short to Short`() {
        val result = NumVal.truncate(NumVal(Short.MAX_VALUE.toInt()))
        assertEquals(Short.MAX_VALUE, result.core)
        assertTrue(result.isShort)
    }

    @Test
    fun `should truncate value fitting in Int to Int`() {
        val result = NumVal.truncate(NumVal(Int.MAX_VALUE.toLong()))
        assertEquals(Int.MAX_VALUE, result.core)
        assertTrue(result.isInt)
    }

    @Test
    fun `should truncate Long MAX_VALUE to Long`() {
        val result = NumVal.truncate(NumVal(Long.MAX_VALUE))
        assertEquals(Long.MAX_VALUE, result.core)
        assertTrue(result.isLong)
    }

    @Test
    fun `should truncate floating point to smallest integer type`() {
        val result = NumVal.truncate(NumVal(3.14))
        assertEquals(3.toByte(), result.core)
        assertTrue(result.isByte)
    }

    // --- Type introspection ---

    @Test
    fun `should report isInt true for Int core`() {
        assertTrue(NumVal(42).isInt)
        assertFalse(NumVal(42L).isInt)
    }

    @Test
    fun `should report isLong true for Long core`() {
        assertTrue(NumVal(42L).isLong)
        assertFalse(NumVal(42).isLong)
    }

    @Test
    fun `should report isShort true for Short core`() {
        assertTrue(NumVal(42.toShort()).isShort)
        assertFalse(NumVal(42).isShort)
    }

    @Test
    fun `should report isByte true for Byte core`() {
        assertTrue(NumVal(42.toByte()).isByte)
        assertFalse(NumVal(42).isByte)
    }

    @Test
    fun `should report isFloat true for Float core`() {
        assertTrue(NumVal(1.5f).isFloat)
        assertFalse(NumVal(1.5).isFloat)
    }

    @Test
    fun `should report isDouble true for Double core`() {
        assertTrue(NumVal(1.5).isDouble)
        assertFalse(NumVal(1.5f).isDouble)
    }

    @Test
    fun `should report isPrimitiveIntegerType for Byte`() {
        assertTrue(NumVal(1.toByte()).isPrimitiveIntegerType)
    }

    @Test
    fun `should report isPrimitiveIntegerType for Short`() {
        assertTrue(NumVal(1.toShort()).isPrimitiveIntegerType)
    }

    @Test
    fun `should report isPrimitiveIntegerType for Int`() {
        assertTrue(NumVal(1).isPrimitiveIntegerType)
    }

    @Test
    fun `should report isPrimitiveIntegerType for Long`() {
        assertTrue(NumVal(1L).isPrimitiveIntegerType)
    }

    @Test
    fun `should not report isPrimitiveIntegerType for Double`() {
        assertFalse(NumVal(1.0).isPrimitiveIntegerType)
    }

    @Test
    fun `should report isPrimitiveFloatingType for Float`() {
        assertTrue(NumVal(1.0f).isPrimitiveFloatingType)
    }

    @Test
    fun `should report isPrimitiveFloatingType for Double`() {
        assertTrue(NumVal(1.0).isPrimitiveFloatingType)
    }

    @Test
    fun `should not report isPrimitiveFloatingType for Int`() {
        assertFalse(NumVal(1).isPrimitiveFloatingType)
    }

    // --- IPrimitiveVal hierarchy ---

    @Test
    fun `should implement IPrimitiveVal`() {
        val value: IPrimitiveVal = NumVal(0)
        assertTrue(value is NumVal)
    }
}
