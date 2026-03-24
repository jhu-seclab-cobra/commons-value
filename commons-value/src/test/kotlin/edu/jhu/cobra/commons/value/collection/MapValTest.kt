package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.*

class MapValTest {
    @Test
    fun testDefaultConstructor() {
        val mapVal = MapVal()
        assertTrue(mapVal.isEmpty())
        assertEquals(0, mapVal.size)
    }

    @Test
    fun testSizeConstructor() {
        val mapVal = MapVal(10)
        assertTrue(mapVal.isEmpty())
        assertEquals(0, mapVal.size)
    }

    @Test
    fun testMapConstructor() {
        val initialMap = mapOf("key1" to StrVal("value1"), "key2" to NumVal(42))
        val mapVal = MapVal(initialMap)
        assertEquals(2, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
        assertEquals(NumVal(42), mapVal["key2"])
    }

    @Test
    fun testVarargConstructor() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        assertEquals(2, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
        assertEquals(NumVal(42), mapVal["key2"])
    }

    @Test
    fun testSequenceConstructor() {
        val sequence = sequenceOf("key1" to StrVal("value1"), "key2" to NumVal(42))
        val mapVal = MapVal(sequence)
        assertEquals(2, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
        assertEquals(NumVal(42), mapVal["key2"])
    }

    @Test
    fun testListConstructor() {
        val list = listOf("key1" to StrVal("value1"), "key2" to NumVal(42))
        val mapVal = MapVal(list)
        assertEquals(2, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
        assertEquals(NumVal(42), mapVal["key2"])
    }

    @Test
    fun testGetOperator() {
        val mapVal = MapVal("key1" to StrVal("value1"))
        assertEquals(StrVal("value1"), mapVal["key1"])
        assertNull(mapVal["nonexistent"])
    }

    @Test
    fun testSetOperator() {
        val mapVal = MapVal()
        mapVal["key1"] = StrVal("value1")
        assertEquals(1, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
    }

    @Test
    fun testAdd() {
        val mapVal = MapVal()
        mapVal.add("key1", StrVal("value1"))
        assertEquals(1, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
    }

    @Test
    fun testPlusOperator() {
        val mapVal = MapVal()
        mapVal + ("key1" to StrVal("value1"))
        assertEquals(1, mapVal.size)
        assertEquals(StrVal("value1"), mapVal["key1"])
    }

    @Test
    fun testMinusOperator() {
        val mapVal = MapVal("key1" to StrVal("value1"))
        mapVal - "key1"
        assertEquals(0, mapVal.size)
        assertNull(mapVal["key1"])
    }

    @Test
    fun testRemove() {
        val mapVal = MapVal("key1" to StrVal("value1"))
        val removed = mapVal.remove("key1")
        assertEquals(0, mapVal.size)
        assertEquals(StrVal("value1"), removed)
        assertNull(mapVal.remove("nonexistent"))
    }

    @Test
    fun testKeys() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val keys = mapVal.keys()
        assertEquals(2, keys.size)
        assertTrue(keys.contains("key1"))
        assertTrue(keys.contains("key2"))
    }

    @Test
    fun testValues() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val values = mapVal.values()
        assertEquals(2, values.size)
        assertTrue(values.contains(StrVal("value1")))
        assertTrue(values.contains(NumVal(42)))
    }

    @Test
    fun testForEach() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        var count = 0
        mapVal.forEach { count++ }
        assertEquals(2, count)
    }

    @Test
    fun testMap() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val mapped = mapVal.map { it.key + "=" + it.value.toString() }
        assertEquals(2, mapped.size)
        assertTrue(mapped.contains("key1=StrVal{value1}"))
        assertTrue(mapped.contains("key2=NumVal{42}"))
    }

    @Test
    fun testMapValues() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val mapped = mapVal.mapValues { it.value.toString() }
        assertEquals(2, mapped.size)
        assertEquals("StrVal{value1}", mapped["key1"])
        assertEquals("NumVal{42}", mapped["key2"])
    }

    @Test
    fun testFlatMap() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val flatMapped = mapVal.flatMap { listOf(it.key, it.value.toString()) }
        assertEquals(4, flatMapped.size)
        assertTrue(flatMapped.contains("key1"))
        assertTrue(flatMapped.contains("StrVal{value1}"))
        assertTrue(flatMapped.contains("key2"))
        assertTrue(flatMapped.contains("NumVal{42}"))
    }

    @Test
    fun testIsEmpty() {
        val emptyMap = MapVal()
        val nonEmptyMap = MapVal("key1" to StrVal("value1"))
        assertTrue(emptyMap.isEmpty())
        assertFalse(nonEmptyMap.isEmpty())
    }

    @Test
    fun testContains() {
        val mapVal = MapVal("key1" to StrVal("value1"))
        assertTrue("key1" in mapVal)
        assertFalse("nonexistent" in mapVal)
    }

    @Test
    fun testToTypeArray() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val array = mapVal.toTypeArray()
        assertEquals(2, array.size)
        assertTrue(array.any { it.first == "key1" && it.second == StrVal("value1") })
        assertTrue(array.any { it.first == "key2" && it.second == NumVal(42) })
    }

    @Test
    fun testToList() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val list = mapVal.toList()
        assertEquals(2, list.size)
        assertTrue(list.any { it.first == "key1" && it.second == StrVal("value1") })
        assertTrue(list.any { it.first == "key2" && it.second == NumVal(42) })
    }

    @Test
    fun testToString() {
        val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        val str = mapVal.toString()
        assertTrue(str.startsWith("{"))
        assertTrue(str.endsWith("}"))
        assertTrue(str.contains("key1=StrVal{value1}"))
        assertTrue(str.contains("key2=NumVal{42}"))
    }
} 