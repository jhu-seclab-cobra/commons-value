package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

/**
 * Tests for the top-level `Any?.toVal` extension property specified in design-utils.md.
 *
 * - `should convert null to NullVal` — null input produces NullVal.
 * - `should convert Int to NumVal` — Int input produces NumVal wrapping that Int.
 * - `should convert Double to NumVal` — Double input produces NumVal wrapping that Double.
 * - `should convert String to StrVal` — String input produces StrVal wrapping that String.
 * - `should convert true to BoolVal` — true produces BoolVal with core true.
 * - `should convert false to BoolVal` — false produces BoolVal with core false.
 * - `should convert List to ListVal` — List input produces ListVal with converted elements.
 * - `should convert Map to MapVal` — Map input produces MapVal with converted entries.
 * - `should convert IntRange to RangeVal` — IntRange produces RangeVal with matching bounds.
 * - `should convert Set to SetVal` — Set input produces SetVal with converted elements.
 * - `should return IValue identity` — IValue input returned as same instance.
 * - `should throw IllegalArgumentException for unsupported type` — Unsupported type rejected.
 */
internal class UtilsTest {

    @Test
    fun `should convert null to NullVal`() {
        val result = null.toVal
        assertIs<NullVal>(result)
    }

    @Test
    fun `should convert Int to NumVal`() {
        val result = 42.toVal
        assertIs<NumVal>(result)
        assertEquals(42, result.core)
    }

    @Test
    fun `should convert Double to NumVal`() {
        val result = 3.14.toVal
        assertIs<NumVal>(result)
        assertEquals(3.14, result.core)
    }

    @Test
    fun `should convert String to StrVal`() {
        val result = "test".toVal
        assertIs<StrVal>(result)
        assertEquals("test", result.core)
    }

    @Test
    fun `should convert true to BoolVal`() {
        val result = true.toVal
        assertIs<BoolVal>(result)
        assertEquals(true, result.core)
    }

    @Test
    fun `should convert false to BoolVal`() {
        val result = false.toVal
        assertIs<BoolVal>(result)
        assertEquals(false, result.core)
    }

    @Test
    fun `should convert List to ListVal`() {
        val result = listOf(1, "two", true).toVal
        assertIs<ListVal>(result)
        assertEquals(NumVal(1), result[0])
        assertEquals(StrVal("two"), result[1])
        assertEquals(BoolVal.T, result[2])
    }

    @Test
    fun `should convert Map to MapVal`() {
        val result = mapOf("key" to "value").toVal
        assertIs<MapVal>(result)
        assertEquals(StrVal("value"), result["key"])
    }

    @Test
    fun `should convert IntRange to RangeVal`() {
        val result = (1..10).toVal
        assertIs<RangeVal>(result)
        assertEquals(1, result.first)
        assertEquals(10, result.last)
    }

    @Test
    fun `should convert Set to SetVal`() {
        val result = setOf(1, 2, 3).toVal
        assertIs<SetVal>(result)
        assertEquals(3, result.size)
    }

    @Test
    fun `should return IValue identity`() {
        val numVal = NumVal(42)
        val result = (numVal as Any?).toVal
        assertSame(numVal, result)
    }

    @Test
    fun `should throw IllegalArgumentException for unsupported type`() {
        assertFailsWith<IllegalArgumentException> {
            Object().toVal
        }
    }
}
