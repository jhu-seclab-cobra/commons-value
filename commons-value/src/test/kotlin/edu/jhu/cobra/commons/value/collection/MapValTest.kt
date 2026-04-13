package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Black-box tests for [MapVal] derived from design-collection.md.
 *
 * Constructors:
 * - `should create empty map with default constructor`
 * - `should create empty map with sized constructor`
 * - `should copy entries from map constructor`
 * - `should initialize from vararg pairs`
 * - `should initialize from sequence of pairs`
 * - `should initialize from list of pairs`
 *
 * get / set / add / plus:
 * - `should return value for existing key`
 * - `should return null for missing key`
 * - `should set key-value pair via set operator`
 * - `should overwrite existing key via set operator`
 * - `should add key-value pair via add method`
 * - `should add key-value pair via plus operator`
 *
 * minus / remove:
 * - `should remove entry by key via minus operator`
 * - `should remove entry by key via remove method`
 * - `should return null when removing missing key`
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
 * forEach / map / mapValues / flatMap / toList / toTypeArray:
 * - `should iterate all entries with forEach`
 * - `should transform entries with map`
 * - `should transform values with mapValues`
 * - `should flatten entries with flatMap`
 * - `should convert to pair list with toList`
 * - `should convert to pair array with toTypeArray`
 *
 * Boundary:
 * - `should handle empty map operations`
 * - `should enforce String keys only`
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
        val source = mapOf("k1" to StrVal("v1"), "k2" to NumVal(2))
        val map = MapVal(source)
        assertEquals(2, map.size)
        assertEquals(StrVal("v1"), map["k1"])
        assertEquals(NumVal(2), map["k2"])
    }

    @Test
    fun `should initialize from vararg pairs`() {
        val map = MapVal("a" to StrVal("1"), "b" to NumVal(2))
        assertEquals(2, map.size)
        assertEquals(StrVal("1"), map["a"])
        assertEquals(NumVal(2), map["b"])
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
        val pairs = listOf("p" to NumVal(10), "q" to StrVal("r"))
        val map = MapVal(pairs)
        assertEquals(2, map.size)
        assertEquals(NumVal(10), map["p"])
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

    // -- add --

    @Test
    fun `should add key-value pair via add method`() {
        val map = MapVal()
        val prev = map.add("k", StrVal("v"))
        assertNull(prev)
        assertEquals(StrVal("v"), map["k"])
    }

    // -- plus --

    @Test
    fun `should add key-value pair via plus operator`() {
        val map = MapVal()
        map + ("k" to StrVal("v"))
        assertEquals(1, map.size)
        assertEquals(StrVal("v"), map["k"])
    }

    // -- minus / remove --

    @Test
    fun `should remove entry by key via minus operator`() {
        val map = MapVal("k" to StrVal("v"))
        val removed = map - "k"
        assertEquals(0, map.size)
        assertEquals(StrVal("v"), removed)
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

    // -- keys / values / contains --

    @Test
    fun `should return all keys`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val keys = map.keys()
        assertEquals(2, keys.size)
        assertTrue(keys.contains("a"))
        assertTrue(keys.contains("b"))
    }

    @Test
    fun `should return all values`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val values = map.values()
        assertEquals(2, values.size)
        assertTrue(values.contains(NumVal(1)))
        assertTrue(values.contains(NumVal(2)))
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
        map["a"] = NumVal(1)
        map["b"] = NumVal(2)
        assertEquals(2, map.size)
        map.remove("a")
        assertEquals(1, map.size)
    }

    // -- forEach --

    @Test
    fun `should iterate all entries with forEach`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val keys = mutableListOf<String>()
        map.forEach { keys.add(it.key) }
        assertEquals(2, keys.size)
        assertTrue(keys.contains("a"))
        assertTrue(keys.contains("b"))
    }

    // -- map --

    @Test
    fun `should transform entries with map`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val result = map.map { "${it.key}=${it.value}" }
        assertEquals(2, result.size)
        assertTrue(result.contains("a=NumVal{1}"))
        assertTrue(result.contains("b=NumVal{2}"))
    }

    // -- mapValues --

    @Test
    fun `should transform values with mapValues`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val result = map.mapValues { (it.value as NumVal).toInt() * 10 }
        assertEquals(10, result["a"])
        assertEquals(20, result["b"])
    }

    // -- flatMap --

    @Test
    fun `should flatten entries with flatMap`() {
        val map = MapVal("a" to NumVal(1))
        val result = map.flatMap { listOf(it.key, it.value.toString()) }
        assertEquals(2, result.size)
        assertTrue(result.contains("a"))
        assertTrue(result.contains("NumVal{1}"))
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

    // -- toTypeArray --

    @Test
    fun `should convert to pair array with toTypeArray`() {
        val map = MapVal("a" to NumVal(1), "b" to NumVal(2))
        val array = map.toTypeArray()
        assertEquals(2, array.size)
        assertTrue(array.any { it.first == "a" && it.second == NumVal(1) })
        assertTrue(array.any { it.first == "b" && it.second == NumVal(2) })
    }

    // -- Boundary --

    @Test
    fun `should handle empty map operations`() {
        val map = MapVal()
        assertNull(map["any"])
        assertEquals(0, map.keys().size)
        assertEquals(0, map.values().size)
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
