package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Black-box specification tests for [IntVal] derived from design-primitive.md.
 *
 * Core storage:
 * - `should store positive Long` -- primary constructor with positive value
 * - `should store negative Long` -- primary constructor with negative value
 * - `should store zero` -- primary constructor with zero
 * - `should store Long MAX_VALUE` -- upper boundary
 * - `should store Long MIN_VALUE` -- lower boundary
 * - `should default to zero when no argument` -- default constructor
 *
 * Equality:
 * - `should be equal when same value same type` -- data class equality
 * - `should be equal when constructed from Int and Long` -- widening preserves equality
 * - `should not be equal when different values` -- inequality
 *
 * HashCode:
 * - `should have equal hashCode for equal objects` -- hashCode contract
 * - `should have equal hashCode from Int and Long construction` -- widening preserves hashCode
 *
 * Core widening:
 * - `should widen Int to Long in core` -- Int.intVal stores Long
 *
 * Conversions:
 * - `should convert toInt identity` -- toInt on value fitting in Int
 * - `should truncate toInt on Long MAX_VALUE` -- toInt overflow behavior
 * - `should convert toDouble widening` -- toDouble on integer
 * - `should convert toFloat widening` -- toFloat on integer
 *
 * Extension constructors:
 * - `should construct from Long extension` -- Long.intVal
 * - `should construct from Int extension` -- Int.intVal
 * - `should construct from Short extension` -- Short.intVal
 * - `should construct from Byte extension` -- Byte.intVal
 * - `should parse valid integer string` -- String.intVal "42", "-1", "0"
 * - `should parse Long MAX_VALUE string` -- String.intVal boundary
 * - `should parse Long MIN_VALUE string` -- String.intVal boundary
 * - `should throw NumberFormatException for non-numeric string` -- "abc"
 * - `should throw NumberFormatException for floating-point string` -- "3.14"
 * - `should throw NumberFormatException for empty string` -- ""
 * - `should throw NumberFormatException for blank string` -- " "
 *
 * compareTo:
 * - `should return positive when greater than Int` -- compareTo(Int) >
 * - `should return negative when less than Int` -- compareTo(Int) <
 * - `should return zero when equal to Int` -- compareTo(Int) ==
 * - `should return positive when greater than Long` -- compareTo(Long) >
 * - `should return negative when less than Long` -- compareTo(Long) <
 * - `should return zero when equal to Long` -- compareTo(Long) ==
 * - `should compare against Int MAX_VALUE` -- boundary compareTo(Int)
 * - `should compare against Int MIN_VALUE` -- boundary compareTo(Int)
 * - `should return positive from compareTo when Long MAX_VALUE compared to zero` -- Long.MAX_VALUE truncation guard
 *
 * toString:
 * - `should format as IntVal braces` -- "IntVal{42}"
 *
 * Type hierarchy:
 * - `should implement IPrimitiveVal` -- IntVal is IPrimitiveVal
 */
internal class IntValTest {
    // --- Core storage ---

    @Test
    fun `should store positive Long`() {
        assertEquals(42L, IntVal(42L).core)
    }

    @Test
    fun `should store negative Long`() {
        assertEquals(-7L, IntVal(-7L).core)
    }

    @Test
    fun `should store zero`() {
        assertEquals(0L, IntVal(0L).core)
    }

    @Test
    fun `should store Long MAX_VALUE`() {
        assertEquals(Long.MAX_VALUE, IntVal(Long.MAX_VALUE).core)
    }

    @Test
    fun `should store Long MIN_VALUE`() {
        assertEquals(Long.MIN_VALUE, IntVal(Long.MIN_VALUE).core)
    }

    @Test
    fun `should default to zero when no argument`() {
        assertEquals(0L, IntVal().core)
    }

    // --- Equality ---

    @Test
    fun `should be equal when same value same type`() {
        assertEquals(IntVal(42L), IntVal(42L))
    }

    @Test
    fun `should be equal when constructed from Int and Long`() {
        assertEquals(42.intVal, IntVal(42L))
    }

    @Test
    fun `should not be equal when different values`() {
        assertNotEquals(IntVal(1L), IntVal(2L))
    }

    // --- HashCode ---

    @Test
    fun `should have equal hashCode for equal objects`() {
        assertEquals(IntVal(42L).hashCode(), IntVal(42L).hashCode())
    }

    @Test
    fun `should have equal hashCode from Int and Long construction`() {
        assertEquals(42.intVal.hashCode(), 42L.intVal.hashCode())
    }

    // --- Core widening ---

    @Test
    fun `should widen Int to Long in core`() {
        assertEquals(42L, 42.intVal.core)
    }

    // --- Conversions ---

    @Test
    fun `should convert toInt identity`() {
        assertEquals(42, IntVal(42L).toInt())
    }

    @Test
    fun `should truncate toInt on Long MAX_VALUE`() {
        val truncated = IntVal(Long.MAX_VALUE).toInt()
        assertEquals(Long.MAX_VALUE.toInt(), truncated)
    }

    @Test
    fun `should convert toDouble widening`() {
        assertEquals(42.0, IntVal(42L).toDouble())
    }

    @Test
    fun `should convert toFloat widening`() {
        assertEquals(42.0f, IntVal(42L).toFloat())
    }

    // --- Extension constructors ---

    @Test
    fun `should construct from Long extension`() {
        assertEquals(IntVal(42L), 42L.intVal)
    }

    @Test
    fun `should construct from Int extension`() {
        assertEquals(IntVal(42L), 42.intVal)
    }

    @Test
    fun `should construct from Short extension`() {
        assertEquals(IntVal(42L), 42.toShort().intVal)
    }

    @Test
    fun `should construct from Byte extension`() {
        assertEquals(IntVal(42L), 42.toByte().intVal)
    }

    @Test
    fun `should parse valid integer string`() {
        assertEquals(IntVal(42L), "42".intVal)
        assertEquals(IntVal(-1L), "-1".intVal)
        assertEquals(IntVal(0L), "0".intVal)
    }

    @Test
    fun `should parse Long MAX_VALUE string`() {
        assertEquals(IntVal(Long.MAX_VALUE), Long.MAX_VALUE.toString().intVal)
    }

    @Test
    fun `should parse Long MIN_VALUE string`() {
        assertEquals(IntVal(Long.MIN_VALUE), Long.MIN_VALUE.toString().intVal)
    }

    @Test
    fun `should throw NumberFormatException for non-numeric string`() {
        assertFailsWith<NumberFormatException> { "abc".intVal }
    }

    @Test
    fun `should throw NumberFormatException for floating-point string`() {
        assertFailsWith<NumberFormatException> { "3.14".intVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string`() {
        assertFailsWith<NumberFormatException> { "".intVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string`() {
        assertFailsWith<NumberFormatException> { " ".intVal }
    }

    // --- compareTo ---

    @Test
    fun `should return positive when greater than Int`() {
        assertTrue(IntVal(42L).compareTo(41) > 0)
    }

    @Test
    fun `should return negative when less than Int`() {
        assertTrue(IntVal(42L).compareTo(43) < 0)
    }

    @Test
    fun `should return zero when equal to Int`() {
        assertEquals(0, IntVal(42L).compareTo(42))
    }

    @Test
    fun `should return positive when greater than Long`() {
        assertTrue(IntVal(42L).compareTo(41L) > 0)
    }

    @Test
    fun `should return negative when less than Long`() {
        assertTrue(IntVal(42L).compareTo(43L) < 0)
    }

    @Test
    fun `should return zero when equal to Long`() {
        assertEquals(0, IntVal(42L).compareTo(42L))
    }

    @Test
    fun `should compare against Int MAX_VALUE`() {
        assertEquals(0, IntVal(Int.MAX_VALUE.toLong()).compareTo(Int.MAX_VALUE))
    }

    @Test
    fun `should compare against Int MIN_VALUE`() {
        assertEquals(0, IntVal(Int.MIN_VALUE.toLong()).compareTo(Int.MIN_VALUE))
    }

    @Test
    fun `should return positive from compareTo when Long MAX_VALUE compared to zero`() {
        val big = IntVal(Long.MAX_VALUE)
        assertTrue(
            big.compareTo(0) > 0,
            "IntVal(Long.MAX_VALUE).compareTo(0) should be positive, not truncated to Int",
        )
    }

    // --- toString ---

    @Test
    fun `should format as IntVal braces`() {
        assertEquals("IntVal{42}", IntVal(42L).toString())
    }

    // --- Type hierarchy ---

    @Test
    fun `should implement IPrimitiveVal`() {
        assertTrue(IntVal(42L) is IPrimitiveVal)
    }
}
