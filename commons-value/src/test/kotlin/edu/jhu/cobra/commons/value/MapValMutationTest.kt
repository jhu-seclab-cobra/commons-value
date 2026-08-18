package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Black-box tests for [MapVal] mutation operations, derived from design-collection.md.
 *
 * set:
 * - `should set key-value pair via set operator`
 * - `should overwrite existing key via set operator`
 *
 * put:
 * - `should put key-value pair and return previous value`
 *
 * plus / plusAssign:
 * - `should return new map with pair added via plus operator`
 * - `should add key-value pair via plusAssign operator`
 *
 * minus / minusAssign / remove:
 * - `should return new map without key via minus operator`
 * - `should remove entry by key via minusAssign operator`
 * - `should remove entry by key via remove method`
 * - `should return null when removing missing key`
 *
 * deepCopy:
 * - `should deep copy map values without sharing mutable state`
 * - `should throw IllegalArgumentException when deepCopy meets cyclic map` — Cyclic value graph
 *   rejected with a diagnosable error instead of StackOverflowError.
 *
 * Equality:
 * - `should equal plain map with same content symmetrically` — JDK collection contract.
 *
 * Boundary:
 * - `should handle empty map operations`
 * - `should enforce String keys only`
 */
internal class MapValMutationTest {
    // -- set --

    @Test
    fun `should set key-value pair via set operator`() {
        val map = MapVal()
        map["k"] = StrVal("v")
        assertEquals(1, map.size)
        assertEquals(StrVal("v"), map["k"])
    }

    @Test
    fun `should overwrite existing key via set operator`() {
        val map = MapVal("k" to StrVal("old"))
        map["k"] = StrVal("new")
        assertEquals(1, map.size)
        assertEquals(StrVal("new"), map["k"])
    }

    // -- put --

    @Test
    fun `should put key-value pair and return previous value`() {
        val map = MapVal()
        val prev = map.put("k", StrVal("v"))
        assertNull(prev)
        assertEquals(StrVal("v"), map["k"])
    }

    // -- plus / plusAssign --

    @Test
    fun `should return new map with pair added via plus operator`() {
        val map = MapVal()
        val added = map + ("k" to StrVal("v"))
        assertEquals(0, map.size)
        assertEquals(1, added.size)
        assertEquals(StrVal("v"), added["k"])
    }

    @Test
    fun `should add key-value pair via plusAssign operator`() {
        val map = MapVal()
        map += "k" to StrVal("v")
        assertEquals(1, map.size)
        assertEquals(StrVal("v"), map["k"])
    }

    // -- minus / minusAssign / remove --

    @Test
    fun `should return new map without key via minus operator`() {
        val map = MapVal("k" to StrVal("v"))
        val removed = map - "k"
        assertEquals(1, map.size)
        assertEquals(0, removed.size)
    }

    @Test
    fun `should remove entry by key via minusAssign operator`() {
        val map = MapVal("k" to StrVal("v"))
        map -= "k"
        assertEquals(0, map.size)
    }

    @Test
    fun `should remove entry by key via remove method`() {
        val map = MapVal("k" to StrVal("v"))
        val removed = map.remove("k")
        assertEquals(0, map.size)
        assertEquals(StrVal("v"), removed)
    }

    @Test
    fun `should return null when removing missing key`() {
        val map = MapVal()
        assertNull(map.remove("missing"))
    }

    // -- deepCopy --

    @Test
    fun `should deep copy map values without sharing mutable state`() {
        val inner = ListVal(IntVal(1L))
        val map = MapVal("k" to inner)
        val copy = map.deepCopy()
        assertEquals(map, copy)
        (copy["k"] as ListVal).add(IntVal(2L))
        assertEquals(1, inner.size)
    }

    @Test
    fun `should throw IllegalArgumentException when deepCopy meets cyclic map`() {
        val map = MapVal()
        map["self"] = map
        assertFailsWith<IllegalArgumentException> { map.deepCopy() }
    }

    // -- Equality --

    @Test
    fun `should equal plain map with same content symmetrically`() {
        val mapVal = MapVal("k" to IntVal(1L))
        val plain: Map<String, IValue> = mapOf("k" to IntVal(1L))
        assertTrue(plain == mapVal)
        assertTrue(mapVal == plain)
        assertEquals(plain.hashCode(), mapVal.hashCode())
    }

    // -- Boundary --

    @Test
    fun `should handle empty map operations`() {
        val map = MapVal()
        assertNull(map["any"])
        assertEquals(0, map.keys.size)
        assertEquals(0, map.values.size)
        assertEquals(0, map.toList().size)
        assertFalse("any" in map)
    }

    @Test
    fun `should enforce String keys only`() {
        val map = MapVal("key" to StrVal("v"))
        assertEquals(StrVal("v"), map["key"])
        assertTrue("key" in map)
    }
}
