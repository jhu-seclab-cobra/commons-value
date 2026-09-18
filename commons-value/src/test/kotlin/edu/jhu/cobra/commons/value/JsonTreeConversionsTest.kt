package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Tests for the `IValue.toJsonTree` extension function specified in design-conversions.md.
 *
 * - `should project NullVal to null` — NullVal renders as null.
 * - `should project primitives to plain JVM values` — StrVal, IntVal, FloatVal, BoolVal render as
 *   String, Long, Double, Boolean.
 * - `should project Unsure to its enum name` — Unsure.STR renders as "STR".
 * - `should project RangeVal to bound pair` — RangeVal renders as the two-element list of bounds.
 * - `should keep ListVal element order` — ListVal renders elements in list order.
 * - `should sort MapVal entries by key` — MapVal renders keys in ascending order regardless of insertion.
 * - `should sort SetVal elements canonically` — SetVal renders elements by kind rank then content.
 * - `should project equal sets onto equal trees` — two sets with different insertion order render equal.
 * - `should sort nested maps inside sets` — maps inside a set compare by their sorted entries.
 * - `should reject nesting beyond the depth limit` — a chain deeper than MAX_NESTING_DEPTH throws.
 */
internal class JsonTreeConversionsTest {
    @Test
    fun `should project NullVal to null`() {
        assertNull(NullVal.toJsonTree())
    }

    @Test
    fun `should project primitives to plain JVM values`() {
        assertEquals("a", StrVal("a").toJsonTree())
        assertEquals(7L, IntVal(7).toJsonTree())
        assertEquals(1.5, FloatVal(1.5).toJsonTree())
        assertEquals(true, BoolVal.T.toJsonTree())
    }

    @Test
    fun `should project Unsure to its enum name`() {
        assertEquals("STR", Unsure.STR.toJsonTree())
    }

    @Test
    fun `should project RangeVal to bound pair`() {
        assertEquals(listOf(1L, 3L), RangeVal(IntVal(1), IntVal(3)).toJsonTree())
    }

    @Test
    fun `should keep ListVal element order`() {
        val list = ListVal(arrayListOf(IntVal(2), IntVal(1)))
        assertEquals(listOf(2L, 1L), list.toJsonTree())
    }

    @Test
    fun `should sort MapVal entries by key`() {
        val map = MapVal(linkedMapOf("b" to IntVal(2), "a" to IntVal(1)))
        val tree = map.toJsonTree() as Map<*, *>
        assertEquals(listOf("a", "b"), tree.keys.toList())
        assertEquals(mapOf("a" to 1L, "b" to 2L), tree)
    }

    @Test
    fun `should sort SetVal elements canonically`() {
        val set = SetVal(linkedSetOf(StrVal("b"), IntVal(3), StrVal("a"), NullVal, BoolVal.T, IntVal(2)))
        assertEquals(listOf(null, true, 2L, 3L, "a", "b"), set.toJsonTree())
    }

    @Test
    fun `should project equal sets onto equal trees`() {
        val first = SetVal(linkedSetOf(IntVal(1), StrVal("x")))
        val second = SetVal(linkedSetOf(StrVal("x"), IntVal(1)))
        assertEquals(first.toJsonTree(), second.toJsonTree())
    }

    @Test
    fun `should sort nested maps inside sets`() {
        val small = MapVal(linkedMapOf("k" to IntVal(1)))
        val large = MapVal(linkedMapOf("k" to IntVal(2)))
        val set = SetVal(linkedSetOf(large, small))
        assertEquals(listOf(mapOf("k" to 1L), mapOf("k" to 2L)), set.toJsonTree())
    }

    @Test
    fun `should reject nesting beyond the depth limit`() {
        var value: IValue = IntVal(0)
        repeat(MAX_NESTING_DEPTH + 1) { value = ListVal(arrayListOf(value)) }
        assertFailsWith<IllegalArgumentException> { value.toJsonTree() }
    }
}
