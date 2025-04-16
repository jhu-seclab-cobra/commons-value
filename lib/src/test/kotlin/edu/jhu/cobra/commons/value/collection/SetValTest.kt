package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SetValTest {
    @Test
    fun testDefaultConstructor() {
        val setVal = SetVal()
        assertTrue(setVal.isEmpty())
        assertEquals(0, setVal.size)
    }

    @Test
    fun testSizeConstructor() {
        val setVal = SetVal(10)
        assertTrue(setVal.isEmpty())
        assertEquals(0, setVal.size)
    }

    @Test
    fun testCollectionConstructor() {
        val initialSet = setOf(StrVal("Item1"), NumVal(42))
        val setVal = SetVal(initialSet)
        assertEquals(2, setVal.size)
        assertTrue(setVal.contains(StrVal("Item1")))
        assertTrue(setVal.contains(NumVal(42)))
    }

    @Test
    fun testVarargConstructor() {
        val setVal = SetVal(StrVal("Item1"), NumVal(42), BoolVal.T)
        assertEquals(3, setVal.size)
        assertTrue(setVal.contains(StrVal("Item1")))
        assertTrue(setVal.contains(NumVal(42)))
        assertTrue(setVal.contains(BoolVal.T))
    }

    @Test
    fun testSequenceConstructor() {
        val sequence = sequenceOf(StrVal("Item1"), NumVal(42))
        val setVal = SetVal(sequence)
        assertEquals(2, setVal.size)
        assertTrue(setVal.contains(StrVal("Item1")))
        assertTrue(setVal.contains(NumVal(42)))
    }

    @Test
    fun testAdd() {
        val setVal = SetVal()
        assertTrue(setVal.add(StrVal("Item1")))
        assertFalse(setVal.add(StrVal("Item1"))) // Adding duplicate
        assertEquals(1, setVal.size)
    }

    @Test
    fun testRemove() {
        val setVal = SetVal(StrVal("Item1"), NumVal(42))
        assertTrue(setVal.remove(StrVal("Item1")))
        assertFalse(setVal.remove(StrVal("Item1"))) // Removing non-existent
        assertEquals(1, setVal.size)
    }

    @Test
    fun testPlusOperator() {
        val originalSet = SetVal(StrVal("A"), StrVal("B"))
        val newSet = originalSet + StrVal("C")
        assertEquals(2, originalSet.size)
        assertEquals(3, newSet.size)
        assertTrue(newSet.contains(StrVal("C")))
    }

    @Test
    fun testPlusAssignOperator() {
        val set = SetVal(StrVal("A"), StrVal("B"))
        set += StrVal("C")
        assertEquals(3, set.size)
        assertTrue(set.contains(StrVal("C")))
    }

    @Test
    fun testMinusOperator() {
        val originalSet = SetVal(StrVal("A"), StrVal("B"), StrVal("C"))
        val newSet = originalSet - StrVal("B")
        assertEquals(3, originalSet.size)
        assertEquals(2, newSet.size)
        assertFalse(newSet.contains(StrVal("B")))
    }

    @Test
    fun testMinusAssignOperator() {
        val set = SetVal(StrVal("A"), StrVal("B"), StrVal("C"))
        set -= StrVal("B")
        assertEquals(2, set.size)
        assertFalse(set.contains(StrVal("B")))
    }

    @Test
    fun testContains() {
        val setVal = SetVal(StrVal("Item1"), NumVal(42))
        assertTrue(setVal.contains(StrVal("Item1")))
        assertTrue(setVal.contains(NumVal(42)))
        assertFalse(setVal.contains(StrVal("Item2")))
    }

    @Test
    fun testContainsAll() {
        val setVal = SetVal(StrVal("Item1"), NumVal(42))
        val itemsToCheck = setOf(StrVal("Item1"), NumVal(42))
        val missingItems = setOf(StrVal("Item2"))
        assertTrue(setVal.containsAll(itemsToCheck))
        assertFalse(setVal.containsAll(missingItems))
    }

    @Test
    fun testMap() {
        val setVal = SetVal(StrVal("A"), NumVal(42))
        val mapped = setVal.map { it.toString() }
        assertEquals(2, mapped.size)
        assertTrue(mapped.contains("StrVal{A}"))
        assertTrue(mapped.contains("NumVal{42}"))
    }

    @Test
    fun testToList() {
        val setVal = SetVal(StrVal("A"), NumVal(42))
        val list = setVal.toList()
        assertEquals(2, list.size)
        assertTrue(list.contains(StrVal("A")))
        assertTrue(list.contains(NumVal(42)))
    }

    @Test
    fun testForEach() {
        val setVal = SetVal(StrVal("A"), NumVal(42))
        var count = 0
        setVal.forEach { count++ }
        assertEquals(2, count)
    }

    @Test
    fun testAsSequence() {
        val setVal = SetVal(StrVal("A"), NumVal(42))
        val sequence = setVal.asSequence()
        assertEquals(2, sequence.count())
    }

    @Test
    fun testIsEmpty() {
        val emptySet = SetVal()
        val nonEmptySet = SetVal(StrVal("A"))
        assertTrue(emptySet.isEmpty())
        assertFalse(nonEmptySet.isEmpty())
    }

    @Test
    fun testIsNotEmpty() {
        val emptySet = SetVal()
        val nonEmptySet = SetVal(StrVal("A"))
        assertFalse(emptySet.isNotEmpty())
        assertTrue(nonEmptySet.isNotEmpty())
    }

    @Test
    fun testToString() {
        val setVal = SetVal(StrVal("A"), NumVal(42))
        val str = setVal.toString()
        assertTrue(str.startsWith("{"))
        assertTrue(str.endsWith("}"))
        assertTrue(str.contains("StrVal{A}"))
        assertTrue(str.contains("NumVal{42}"))
    }
} 