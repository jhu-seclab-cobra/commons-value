package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Black-box tests for [MapVal] read operations, derived from design-collection.md.
 *
 * Constructors:
 * - `should create empty map with default constructor`
 * - `should create empty map with sized constructor`
 * - `should copy entries from map constructor`
 * - `should initialize from vararg pairs`
 * - `should initialize from sequence of pairs`
 * - `should initialize from list of pairs`
 *
 * get:
 * - `should return value for existing key`
 * - `should return null for missing key`
 *
 * keys / values / contains:
 * - `should return all keys`
 * - `should return all values`
 * - `should return true when key present`
 * - `should return false when key absent`
 *
 * isEmpty:
 * - `should return true for isEmpty on empty map`
 * - `should return false for isEmpty on non-empty map`
 *
 * size:
 * - `should return zero for empty map`
 * - `should return correct size after mutations`
 *
 * forEach / map / mapValues / flatMap / toList:
 * - `should iterate all entries with forEach`
 * - `should transform entries with map`
 * - `should transform values with mapValues`
 * - `should flatten entries with flatMap`
 * - `should convert to pair list with toList`
 *
 * Iteration order:
 * - `should iterate entries in insertion order` — keys chosen so hash order differs from insertion order.
 */
internal class MapValTest {
    // -- Constructors --

    @Test
    fun `should create empty map with default constructor`() {
        val map = MapVal()
        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
    }

    @Test
    fun `should create empty map with sized constructor`() {
        val map = MapVal(16)
        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
    }

    @Test
    fun `should copy entries from map constructor`() {
        val source = mapOf("k1" to StrVal("v1"), "k2" to IntVal(2L))
        val map = MapVal(source)
        assertEquals(2, map.size)
        assertEquals(StrVal("v1"), map["k1"])
        assertEquals(IntVal(2L), map["k2"])
    }

    @Test
    fun `should initialize from vararg pairs`() {
        val map = MapVal("a" to StrVal("1"), "b" to IntVal(2L))
        assertEquals(2, map.size)
        assertEquals(StrVal("1"), map["a"])
        assertEquals(IntVal(2L), map["b"])
    }

    @Test
    fun `should initialize from sequence of pairs`() {
        val seq = sequenceOf("x" to StrVal("y"), "z" to BoolVal.T)
        val map = MapVal(seq)
        assertEquals(2, map.size)
        assertEquals(StrVal("y"), map["x"])
        assertEquals(BoolVal.T, map["z"])
    }

    @Test
    fun `should initialize from list of pairs`() {
        val pairs = listOf("p" to IntVal(10L), "q" to StrVal("r"))
        val map = MapVal(pairs)
        assertEquals(2, map.size)
        assertEquals(IntVal(10L), map["p"])
        assertEquals(StrVal("r"), map["q"])
    }

    // -- get --

    @Test
    fun `should return value for existing key`() {
        val map = MapVal("k" to StrVal("v"))
        assertEquals(StrVal("v"), map["k"])
    }

    @Test
    fun `should return null for missing key`() {
        val map = MapVal("k" to StrVal("v"))
        assertNull(map["missing"])
    }

    // -- keys / values / contains --

    @Test
    fun `should return all keys`() {
        val map = MapVal("a" to IntVal(1L), "b" to IntVal(2L))
        val keys = map.keys
        assertEquals(2, keys.size)
        assertTrue(keys.contains("a"))
        assertTrue(keys.contains("b"))
    }

    @Test
    fun `should return all values`() {
        val map = MapVal("a" to IntVal(1L), "b" to IntVal(2L))
        val values = map.values
        assertEquals(2, values.size)
        assertTrue(values.contains(IntVal(1L)))
        assertTrue(values.contains(IntVal(2L)))
    }

    @Test
    fun `should return true when key present`() {
        val map = MapVal("k" to StrVal("v"))
        assertTrue("k" in map)
    }

    @Test
    fun `should return false when key absent`() {
        val map = MapVal("k" to StrVal("v"))
        assertFalse("missing" in map)
    }

    // -- isEmpty --

    @Test
    fun `should return true for isEmpty on empty map`() {
        assertTrue(MapVal().isEmpty())
    }

    @Test
    fun `should return false for isEmpty on non-empty map`() {
        assertFalse(MapVal("k" to StrVal("v")).isEmpty())
    }

    // -- size --

    @Test
    fun `should return zero for empty map`() {
        assertEquals(0, MapVal().size)
    }

    @Test
    fun `should return correct size after mutations`() {
        val map = MapVal()
        map["a"] = IntVal(1L)
        map["b"] = IntVal(2L)
        assertEquals(2, map.size)
        map.remove("a")
        assertEquals(1, map.size)
    }

    // -- forEach --

    @Test
    fun `should iterate all entries with forEach`() {
        val map = MapVal("a" to IntVal(1L), "b" to IntVal(2L))
        val keys = mutableListOf<String>()
        map.forEach { keys.add(it.key) }
        assertEquals(2, keys.size)
        assertTrue(keys.contains("a"))
        assertTrue(keys.contains("b"))
    }

    // -- map --

    @Test
    fun `should transform entries with map`() {
        val map = MapVal("a" to IntVal(1L), "b" to IntVal(2L))
        val result = map.map { "${it.key}=${it.value}" }
        assertEquals(2, result.size)
        assertTrue(result.contains("a=IntVal{1}"))
        assertTrue(result.contains("b=IntVal{2}"))
    }

    // -- mapValues --

    @Test
    fun `should transform values with mapValues`() {
        val map = MapVal("a" to IntVal(1L), "b" to IntVal(2L))
        val result = map.mapValues { (it.value as IntVal).toInt() * 10 }
        assertEquals(10, result["a"])
        assertEquals(20, result["b"])
    }

    // -- flatMap --

    @Test
    fun `should flatten entries with flatMap`() {
        val map = MapVal("a" to IntVal(1L))
        val result = map.flatMap { listOf(it.key, it.value.toString()) }
        assertEquals(2, result.size)
        assertTrue(result.contains("a"))
        assertTrue(result.contains("IntVal{1}"))
    }

    // -- toList --

    @Test
    fun `should convert to pair list with toList`() {
        val map = MapVal("a" to StrVal("1"), "b" to StrVal("2"))
        val list = map.toList()
        assertEquals(2, list.size)
        assertTrue(list.any { it.first == "a" && it.second == StrVal("1") })
        assertTrue(list.any { it.first == "b" && it.second == StrVal("2") })
    }

    // -- Iteration order --

    @Test
    fun `should iterate entries in insertion order`() {
        // "b" hashes after "a": a hash-ordered map would iterate a,b and fail this assertion.
        val map = MapVal("b" to IntVal(2L), "a" to IntVal(1L))
        assertEquals(listOf("b", "a"), map.keys.toList())
    }
}
