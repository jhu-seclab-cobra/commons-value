package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Black-box specification tests for [FloatVal] derived from design-primitive.md.
 *
 * Core storage:
 * - `should store positive Double` -- primary constructor with positive value
 * - `should store negative Double` -- primary constructor with negative value
 * - `should store zero` -- primary constructor with zero
 * - `should store fractional value` -- primary constructor with 3.14
 * - `should store Double MAX_VALUE` -- upper boundary
 * - `should store Double MIN_VALUE` -- smallest positive nonzero
 * - `should store NaN` -- IEEE 754 NaN
 * - `should store positive infinity` -- IEEE 754 +Inf
 * - `should store negative infinity` -- IEEE 754 -Inf
 * - `should default to zero when no argument` -- default constructor
 *
 * Equality:
 * - `should be equal when same value` -- data class equality
 * - `should widen Float to Double in core` -- Float.floatVal stores Double
 * - `should equal NaN to NaN via data class equals` -- data class Double.equals treats NaN == NaN
 *
 * Conversions:
 * - `should truncate toInt` -- toInt on fractional value
 * - `should truncate toLong` -- toLong on fractional value
 * - `should truncate toIntVal` -- toIntVal returns IntVal with truncated value
 * - `should convert toFloat` -- toFloat narrowing
 *
 * Extension constructors:
 * - `should construct from Double extension` -- Double.floatVal
 * - `should construct from Float extension` -- Float.floatVal
 * - `should parse valid float string` -- String.floatVal "3.14", "-0.5", "0.0"
 * - `should throw NumberFormatException for non-numeric string` -- "abc"
 * - `should throw NumberFormatException for empty string` -- ""
 * - `should throw NumberFormatException for blank string` -- " "
 *
 * compareTo:
 * - `should return positive when greater than Double` -- compareTo(Double) >
 * - `should return negative when less than Double` -- compareTo(Double) <
 * - `should return zero when equal to Double` -- compareTo(Double) ==
 *
 * toString:
 * - `should format as FloatVal braces` -- "FloatVal{3.14}"
 *
 * Type hierarchy:
 * - `should implement IPrimitiveVal` -- FloatVal is IPrimitiveVal
 */
internal class FloatValTest {
    // --- Core storage ---

    @Test
    fun `should store positive Double`() {
        assertEquals(3.14, FloatVal(3.14).core)
    }

    @Test
    fun `should store negative Double`() {
        assertEquals(-2.5, FloatVal(-2.5).core)
    }

    @Test
    fun `should store zero`() {
        assertEquals(0.0, FloatVal(0.0).core)
    }

    @Test
    fun `should store fractional value`() {
        assertEquals(3.14, FloatVal(3.14).core)
    }

    @Test
    fun `should store Double MAX_VALUE`() {
        assertEquals(Double.MAX_VALUE, FloatVal(Double.MAX_VALUE).core)
    }

    @Test
    fun `should store Double MIN_VALUE`() {
        assertEquals(Double.MIN_VALUE, FloatVal(Double.MIN_VALUE).core)
    }

    @Test
    fun `should store NaN`() {
        assertTrue(FloatVal(Double.NaN).core.isNaN())
    }

    @Test
    fun `should store positive infinity`() {
        assertEquals(Double.POSITIVE_INFINITY, FloatVal(Double.POSITIVE_INFINITY).core)
    }

    @Test
    fun `should store negative infinity`() {
        assertEquals(Double.NEGATIVE_INFINITY, FloatVal(Double.NEGATIVE_INFINITY).core)
    }

    @Test
    fun `should default to zero when no argument`() {
        assertEquals(0.0, FloatVal().core)
    }

    // --- Equality ---

    @Test
    fun `should be equal when same value`() {
        assertEquals(FloatVal(3.14), FloatVal(3.14))
    }

    @Test
    fun `should widen Float to Double in core`() {
        assertEquals(3.14f.toDouble(), 3.14f.floatVal.core)
    }

    @Test
    fun `should equal NaN to NaN via data class equals`() {
        // Kotlin data class equals uses Double.equals(), where NaN.equals(NaN) is true.
        // This diverges from IEEE 754 where NaN != NaN.
        assertEquals(FloatVal(Double.NaN), FloatVal(Double.NaN))
    }

    // --- Conversions ---

    @Test
    fun `should truncate toInt`() {
        assertEquals(3, FloatVal(3.99).toInt())
    }

    @Test
    fun `should truncate toLong`() {
        assertEquals(3L, FloatVal(3.99).toLong())
    }

    @Test
    fun `should truncate toIntVal`() {
        assertEquals(IntVal(3L), FloatVal(3.14).toIntVal())
    }

    @Test
    fun `should convert toFloat`() {
        assertEquals(3.14f, FloatVal(3.14).toFloat())
    }

    // --- Extension constructors ---

    @Test
    fun `should construct from Double extension`() {
        assertEquals(FloatVal(3.14), 3.14.floatVal)
    }

    @Test
    fun `should construct from Float extension`() {
        assertEquals(FloatVal(3.14f.toDouble()), 3.14f.floatVal)
    }

    @Test
    fun `should parse valid float string`() {
        assertEquals(FloatVal(3.14), "3.14".floatVal)
        assertEquals(FloatVal(-0.5), "-0.5".floatVal)
        assertEquals(FloatVal(0.0), "0.0".floatVal)
    }

    @Test
    fun `should throw NumberFormatException for non-numeric string`() {
        assertFailsWith<NumberFormatException> { "abc".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string`() {
        assertFailsWith<NumberFormatException> { "".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string`() {
        assertFailsWith<NumberFormatException> { " ".floatVal }
    }

    // --- compareTo ---

    @Test
    fun `should return positive when greater than Double`() {
        assertTrue(FloatVal(3.14).compareTo(3.0) > 0)
    }

    @Test
    fun `should return negative when less than Double`() {
        assertTrue(FloatVal(3.14).compareTo(4.0) < 0)
    }

    @Test
    fun `should return zero when equal to Double`() {
        assertEquals(0, FloatVal(3.14).compareTo(3.14))
    }

    // --- toString ---

    @Test
    fun `should format as FloatVal braces`() {
        assertEquals("FloatVal{3.14}", FloatVal(3.14).toString())
    }

    // --- Type hierarchy ---

    @Test
    fun `should implement IPrimitiveVal`() {
        assertTrue(FloatVal(3.14) is IPrimitiveVal)
    }
}
