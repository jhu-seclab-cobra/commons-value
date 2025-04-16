package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CollectionUtilsTest {
    @Test
    fun testCollectionToListVal() {
        val collection = listOf(1, "text", true)
        val listVal = collection.listVal

        assertEquals(3, listVal.size)
        assertTrue(listVal[0] is NumVal)
        assertTrue(listVal[1] is StrVal)
        assertTrue(listVal[2] is BoolVal)
        assertEquals(1, (listVal[0] as NumVal).core)
        assertEquals("text", (listVal[1] as StrVal).core)
        assertEquals(true, (listVal[2] as BoolVal).core)
    }

    @Test
    fun testCollectionToSetVal() {
        val collection = listOf(1, 1, 2, "text")
        val setVal = collection.setVal

        assertEquals(3, setVal.size)
        assertTrue(setVal.contains(NumVal(1)))
        assertTrue(setVal.contains(NumVal(2)))
        assertTrue(setVal.contains(StrVal("text")))
    }

    @Test
    fun testListValOrEmpty() {
        val nullList: ListVal? = null
        val emptyList = nullList.orEmpty()

        assertEquals(0, emptyList.size)

        val nonEmptyList = ListVal(StrVal("item"))
        val sameList = nonEmptyList.orEmpty()
        assertEquals(1, sameList.size)
        assertEquals(StrVal("item"), sameList[0])
    }

    @Test
    fun testMapToMapVal() {
        val map = mapOf("key" to 1, "value" to true)
        val mapVal = map.mapVal

        assertEquals(2, mapVal.size)
        assertEquals(NumVal(1), mapVal["key"])
        assertEquals(BoolVal.T, mapVal["value"])
    }

    @Test
    fun testMapValOrEmpty() {
        val nullMap: MapVal? = null
        val emptyMap = nullMap.orEmpty()

        assertEquals(0, emptyMap.size)

        val nonEmptyMap = MapVal("key" to StrVal("value"))
        val sameMap = nonEmptyMap.orEmpty()
        assertEquals(1, sameMap.size)
        assertEquals(StrVal("value"), sameMap["key"])
    }

    @Test
    fun testSetToSetVal() {
        val set = setOf(1, true, "text")
        val setVal = set.setVal

        assertEquals(3, setVal.size)
        assertTrue(setVal.contains(NumVal(1)))
        assertTrue(setVal.contains(BoolVal.T))
        assertTrue(setVal.contains(StrVal("text")))
    }

    @Test
    fun testSetValOrEmpty() {
        val nullSet: SetVal? = null
        val emptySet = nullSet.orEmpty()

        assertEquals(0, emptySet.size)

        val nonEmptySet = SetVal(NumVal(1))
        val sameSet = nonEmptySet.orEmpty()
        assertEquals(1, sameSet.size)
        assertTrue(sameSet.contains(NumVal(1)))
    }

    @Test
    fun testIntRangeToRangeVal() {
        val range = 1..10
        val rangeVal = range.rangeVal

        assertEquals(1, rangeVal.first)
        assertEquals(10, rangeVal.last)
    }

    @Test
    fun testInvalidValueConversion() {
        class InvalidValue

        val collection = listOf(InvalidValue())

        assertFailsWith<IllegalArgumentException> {
            collection.listVal
        }

        assertFailsWith<IllegalArgumentException> {
            collection.setVal
        }

        val map = mapOf("key" to InvalidValue())
        assertFailsWith<IllegalArgumentException> {
            map.mapVal
        }
    }

    @Test
    fun testEmptyCollections() {
        val emptyList = emptyList<Any>().listVal
        assertEquals(0, emptyList.size)

        val emptySet = emptySet<Any>().setVal
        assertEquals(0, emptySet.size)

        val emptyMap = emptyMap<Any, Any>().mapVal
        assertEquals(0, emptyMap.size)
    }

    @Test
    fun testNestedCollections() {
        val nestedList = listOf(listOf(1, 2), listOf("a", "b"))
        val listVal = nestedList.listVal

        assertEquals(2, listVal.size)
        assertTrue(listVal[0] is ListVal)
        assertTrue(listVal[1] is ListVal)

        val innerList1 = listVal[0] as ListVal
        val innerList2 = listVal[1] as ListVal

        assertEquals(2, innerList1.size)
        assertEquals(2, innerList2.size)
        assertEquals(NumVal(1), innerList1[0])
        assertEquals(NumVal(2), innerList1[1])
        assertEquals(StrVal("a"), innerList2[0])
        assertEquals(StrVal("b"), innerList2[1])
    }
} 