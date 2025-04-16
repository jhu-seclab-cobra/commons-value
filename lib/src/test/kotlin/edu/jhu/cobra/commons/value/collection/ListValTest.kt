package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListValTest {
    @Test
    fun testDefaultConstructor() {
        val listVal = ListVal()
        assertTrue(listVal.isEmpty())
        assertEquals(0, listVal.size)
    }

    @Test
    fun testSizeConstructor() {
        val listVal = ListVal(10)
        assertTrue(listVal.isEmpty())
        assertEquals(0, listVal.size)
    }

    @Test
    fun testListConstructor() {
        val initialList = listOf(StrVal("Item1"), NumVal(42))
        val listVal = ListVal(initialList)
        assertEquals(2, listVal.size)
        assertEquals(StrVal("Item1"), listVal[0])
        assertEquals(NumVal(42), listVal[1])
    }

    @Test
    fun testVarargConstructor() {
        val listVal = ListVal(StrVal("Item1"), NumVal(42), BoolVal.T)
        assertEquals(3, listVal.size)
        assertEquals(StrVal("Item1"), listVal[0])
        assertEquals(NumVal(42), listVal[1])
        assertEquals(BoolVal.T, listVal[2])
    }

    @Test
    fun testGetOperator() {
        val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
        assertEquals(StrVal("Item1"), listVal[0])
        assertEquals(StrVal("Item2"), listVal[1])
    }

    @Test
    fun testSetOperator() {
        val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
        listVal[1] = NumVal(42)
        assertEquals(StrVal("Item1"), listVal[0])
        assertEquals(NumVal(42), listVal[1])
    }

    @Test
    fun testContains() {
        val listVal = ListVal(StrVal("Item1"), NumVal(42))
        assertTrue(listVal.contains(StrVal("Item1")))
        assertTrue(listVal.contains(NumVal(42)))
        assertFalse(listVal.contains(StrVal("Item2")))
    }

    @Test
    fun testContainsAll() {
        val listVal = ListVal(StrVal("Item1"), NumVal(42))
        val itemsToCheck = listOf(StrVal("Item1"), NumVal(42))
        val missingItems = listOf(StrVal("Item2"))
        assertTrue(listVal.containsAll(itemsToCheck))
        assertFalse(listVal.containsAll(missingItems))
    }

    @Test
    fun testIndexOf() {
        val listVal = ListVal(StrVal("Item1"), NumVal(42), StrVal("Item1"))
        assertEquals(0, listVal.indexOf(StrVal("Item1")))
        assertEquals(1, listVal.indexOf(NumVal(42)))
        assertEquals(-1, listVal.indexOf(StrVal("Item2")))
    }

    @Test
    fun testLastIndexOf() {
        val listVal = ListVal(StrVal("Item1"), NumVal(42), StrVal("Item1"))
        assertEquals(2, listVal.lastIndexOf(StrVal("Item1")))
        assertEquals(1, listVal.lastIndexOf(NumVal(42)))
        assertEquals(-1, listVal.lastIndexOf(StrVal("Item2")))
    }

    @Test
    fun testSubList() {
        val listVal = ListVal(StrVal("A"), StrVal("B"), StrVal("C"), StrVal("D"))
        val sublist = listVal.subList(1, 3)
        assertEquals(2, sublist.size)
        assertEquals(StrVal("B"), sublist[0])
        assertEquals(StrVal("C"), sublist[1])
    }

    @Test
    fun testPlusOperator() {
        val originalList = ListVal(StrVal("A"), StrVal("B"))
        val newList = originalList + StrVal("C")
        assertEquals(2, originalList.size)
        assertEquals(3, newList.size)
        assertEquals(StrVal("C"), newList[2])
    }

    @Test
    fun testPlusAssignOperator() {
        val list = ListVal(StrVal("A"), StrVal("B"))
        list += StrVal("C")
        assertEquals(3, list.size)
        assertEquals(StrVal("C"), list[2])
    }

    @Test
    fun testMinusOperator() {
        val originalList = ListVal(StrVal("A"), StrVal("B"), StrVal("C"))
        val newList = originalList - StrVal("B")
        assertEquals(3, originalList.size)
        assertEquals(2, newList.size)
        assertEquals(StrVal("A"), newList[0])
        assertEquals(StrVal("C"), newList[1])
    }

    @Test
    fun testMinusAssignOperator() {
        val list = ListVal(StrVal("A"), StrVal("B"), StrVal("C"))
        list -= StrVal("B")
        assertEquals(2, list.size)
        assertEquals(StrVal("A"), list[0])
        assertEquals(StrVal("C"), list[1])
    }

    @Test
    fun testIsEmpty() {
        val emptyList = ListVal()
        val nonEmptyList = ListVal(StrVal("A"))
        assertTrue(emptyList.isEmpty())
        assertFalse(nonEmptyList.isEmpty())
    }

    @Test
    fun testIsNotEmpty() {
        val emptyList = ListVal()
        val nonEmptyList = ListVal(StrVal("A"))
        assertFalse(emptyList.isNotEmpty())
        assertTrue(nonEmptyList.isNotEmpty())
    }

    @Test
    fun testMap() {
        val listVal = ListVal(StrVal("A"), NumVal(42))
        val mapped = listVal.map { it.toString() }
        assertEquals(2, mapped.size)
        assertEquals("StrVal{A}", mapped[0])
        assertEquals("NumVal{42}", mapped[1])
    }

    @Test
    fun testFlatMap() {
        val listVal = ListVal(StrVal("A"), StrVal("B"))
        val flatMapped = listVal.flatMap { listOf(it.toString(), it.toString()) }
        assertEquals(4, flatMapped.size)
        assertEquals("StrVal{A}", flatMapped[0])
        assertEquals("StrVal{A}", flatMapped[1])
        assertEquals("StrVal{B}", flatMapped[2])
        assertEquals("StrVal{B}", flatMapped[3])
    }

    @Test
    fun testForEach() {
        val listVal = ListVal(StrVal("A"), NumVal(42))
        var count = 0
        listVal.forEach { count++ }
        assertEquals(2, count)
    }

    @Test
    fun testAsSequence() {
        val listVal = ListVal(StrVal("A"), NumVal(42))
        val sequence = listVal.asSequence()
        assertEquals(2, sequence.count())
    }

    @Test
    fun testToMutableSet() {
        val listVal = ListVal(StrVal("A"), StrVal("A"), NumVal(42))
        val set = listVal.toMutableSet()
        assertEquals(2, set.size)
        assertTrue(set.contains(StrVal("A")))
        assertTrue(set.contains(NumVal(42)))
    }

    @Test
    fun testToString() {
        val listVal = ListVal(StrVal("A"), NumVal(42))
        assertEquals("[StrVal{A}, NumVal{42}]", listVal.toString())
    }
} 