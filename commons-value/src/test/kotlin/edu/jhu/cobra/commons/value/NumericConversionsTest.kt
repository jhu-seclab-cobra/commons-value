package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Black-box tests for the numeric conversion extension properties derived from the design doc.
 *
 * - `should wrap Long 0 as IntVal` — Long.intVal zero
 * - `should wrap Long 42 as IntVal` — Long.intVal typical
 * - `should wrap Long MAX_VALUE as IntVal` — Long.intVal boundary max
 * - `should wrap Long MIN_VALUE as IntVal` — Long.intVal boundary min
 * - `should wrap Int as IntVal with Long core` — Int.intVal widens to Long
 * - `should wrap Int MAX_VALUE as IntVal` — Int.intVal boundary max
 * - `should wrap Int MIN_VALUE as IntVal` — Int.intVal boundary min
 * - `should wrap Short as IntVal with Long core` — Short.intVal widens to Long
 * - `should wrap Byte as IntVal with Long core` — Byte.intVal widens to Long
 * - `should parse integer string to IntVal` — String.intVal typical
 * - `should parse negative string to IntVal` — String.intVal negative
 * - `should parse zero string to IntVal` — String.intVal zero
 * - `should parse Long MAX_VALUE string to IntVal` — String.intVal boundary
 * - `should throw NumberFormatException for non-integer string intVal` — String.intVal error
 * - `should throw NumberFormatException for decimal string intVal` — String.intVal error decimal
 * - `should throw NumberFormatException for empty string intVal` — String.intVal error empty
 * - `should throw NumberFormatException for blank string intVal` — String.intVal error blank
 * - `should include offending input in intVal NumberFormatException message` — String.intVal error message
 * - `should wrap Double 0 as FloatVal` — Double.floatVal zero
 * - `should wrap Double 3_14 as FloatVal` — Double.floatVal typical
 * - `should wrap Double MAX_VALUE as FloatVal` — Double.floatVal boundary max
 * - `should wrap Float as FloatVal with Double core` — Float.floatVal widens to Double
 * - `should parse decimal string to FloatVal` — String.floatVal typical
 * - `should parse negative string to FloatVal` — String.floatVal negative
 * - `should parse zero string to FloatVal` — String.floatVal zero
 * - `should throw NumberFormatException for non-numeric string floatVal` — String.floatVal error
 * - `should throw NumberFormatException for empty string floatVal` — String.floatVal error empty
 * - `should throw NumberFormatException for blank string floatVal` — String.floatVal error blank
 * - `should include offending input in floatVal NumberFormatException message` — String.floatVal error message
 */
internal class NumericConversionsTest {
    // --- Long.intVal ---

    @Test
    fun `should wrap Long 0 as IntVal`() {
        assertEquals(IntVal(0L), 0L.intVal)
    }

    @Test
    fun `should wrap Long 42 as IntVal`() {
        assertEquals(IntVal(42L), 42L.intVal)
    }

    @Test
    fun `should wrap Long MAX_VALUE as IntVal`() {
        assertEquals(IntVal(Long.MAX_VALUE), Long.MAX_VALUE.intVal)
    }

    @Test
    fun `should wrap Long MIN_VALUE as IntVal`() {
        assertEquals(IntVal(Long.MIN_VALUE), Long.MIN_VALUE.intVal)
    }

    // --- Int.intVal ---

    @Test
    fun `should wrap Int as IntVal with Long core`() {
        val result = 42.intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    @Test
    fun `should wrap Int MAX_VALUE as IntVal`() {
        val result = Int.MAX_VALUE.intVal
        assertEquals(Int.MAX_VALUE.toLong(), result.core)
    }

    @Test
    fun `should wrap Int MIN_VALUE as IntVal`() {
        val result = Int.MIN_VALUE.intVal
        assertEquals(Int.MIN_VALUE.toLong(), result.core)
    }

    // --- Short.intVal ---

    @Test
    fun `should wrap Short as IntVal with Long core`() {
        val result = 42.toShort().intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    // --- Byte.intVal ---

    @Test
    fun `should wrap Byte as IntVal with Long core`() {
        val result = 42.toByte().intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    // --- String.intVal ---

    @Test
    fun `should parse integer string to IntVal`() {
        assertEquals(IntVal(42L), "42".intVal)
    }

    @Test
    fun `should parse negative string to IntVal`() {
        assertEquals(IntVal(-1L), "-1".intVal)
    }

    @Test
    fun `should parse zero string to IntVal`() {
        assertEquals(IntVal(0L), "0".intVal)
    }

    @Test
    fun `should parse Long MAX_VALUE string to IntVal`() {
        assertEquals(IntVal(Long.MAX_VALUE), Long.MAX_VALUE.toString().intVal)
    }

    @Test
    fun `should throw NumberFormatException for non-integer string intVal`() {
        assertFailsWith<NumberFormatException> { "abc".intVal }
    }

    @Test
    fun `should throw NumberFormatException for decimal string intVal`() {
        assertFailsWith<NumberFormatException> { "3.14".intVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string intVal`() {
        assertFailsWith<NumberFormatException> { "".intVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string intVal`() {
        assertFailsWith<NumberFormatException> { " ".intVal }
    }

    @Test
    fun `should include offending input in intVal NumberFormatException message`() {
        val exception = assertFailsWith<NumberFormatException> { "abc".intVal }
        assertTrue(exception.message.orEmpty().contains("abc"))
    }

    // --- Double.floatVal ---

    @Test
    fun `should wrap Double 0 as FloatVal`() {
        assertEquals(FloatVal(0.0), 0.0.floatVal)
    }

    @Test
    fun `should wrap Double 3_14 as FloatVal`() {
        assertEquals(FloatVal(3.14), 3.14.floatVal)
    }

    @Test
    fun `should wrap Double MAX_VALUE as FloatVal`() {
        assertEquals(FloatVal(Double.MAX_VALUE), Double.MAX_VALUE.floatVal)
    }

    // --- Float.floatVal ---

    @Test
    fun `should wrap Float as FloatVal with Double core`() {
        val result = 3.14f.floatVal
        assertIs<FloatVal>(result)
        assertEquals(3.14f.toDouble(), result.core)
    }

    // --- String.floatVal ---

    @Test
    fun `should parse decimal string to FloatVal`() {
        assertEquals(FloatVal(3.14), "3.14".floatVal)
    }

    @Test
    fun `should parse negative string to FloatVal`() {
        assertEquals(FloatVal(-0.5), "-0.5".floatVal)
    }

    @Test
    fun `should parse zero string to FloatVal`() {
        assertEquals(FloatVal(0.0), "0.0".floatVal)
    }

    @Test
    fun `should throw NumberFormatException for non-numeric string floatVal`() {
        assertFailsWith<NumberFormatException> { "abc".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string floatVal`() {
        assertFailsWith<NumberFormatException> { "".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string floatVal`() {
        assertFailsWith<NumberFormatException> { " ".floatVal }
    }

    @Test
    fun `should include offending input in floatVal NumberFormatException message`() {
        val exception = assertFailsWith<NumberFormatException> { "not-a-float".floatVal }
        assertTrue(exception.message.orEmpty().contains("not-a-float"))
    }
}
