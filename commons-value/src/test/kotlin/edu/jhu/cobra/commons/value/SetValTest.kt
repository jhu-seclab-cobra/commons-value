package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [SetVal] constructors, queries, and iteration — derived from design-collection.md.
 *
 * Constructors:
 * - `should create empty set with default constructor`
 * - `should create empty set with sized constructor`
 * - `should copy elements from collection constructor`
 * - `should initialize from vararg elements`
 * - `should initialize from sequence`
 *
 * contains / containsAll:
 * - `should return true when element present`
 * - `should return false when element absent`
 * - `should return true when all elements present`
 * - `should return false when some elements missing`
 *
 * isEmpty / isNotEmpty:
 * - `should return true for isEmpty on empty set`
 * - `should return false for isEmpty on non-empty set`
 * - `should return false for isNotEmpty on empty set`
 * - `should return true for isNotEmpty on non-empty set`
 *
 * size:
 * - `should return zero for empty set`
 * - `should not increase size on duplicate add`
 *
 * map / forEach / asSequence / toList:
 * - `should transform each element with map`
 * - `should iterate all elements with forEach`
 * - `should return lazy sequence with asSequence`
 * - `should convert to list preserving elements with toList`
 */
internal class SetValTest {
    // -- Constructors --

    @Test
    fun `should create empty set with default constructor`() {
        val set = SetVal()
        assertTrue(set.isEmpty())
        assertEquals(0, set.size)
    }

    @Test
    fun `should create empty set with sized constructor`() {
        val set = SetVal(16)
        assertTrue(set.isEmpty())
        assertEquals(0, set.size)
    }

    @Test
    fun `should copy elements from collection constructor`() {
        val source = listOf(StrVal("a"), IntVal(1L))
        val set = SetVal(source)
        assertEquals(2, set.size)
        assertTrue(set.contains(StrVal("a")))
        assertTrue(set.contains(IntVal(1L)))
    }

    @Test
    fun `should initialize from vararg elements`() {
        val set = SetVal(StrVal("x"), IntVal(7L), BoolVal.T)
        assertEquals(3, set.size)
        assertTrue(set.contains(StrVal("x")))
        assertTrue(set.contains(IntVal(7L)))
        assertTrue(set.contains(BoolVal.T))
    }

    @Test
    fun `should initialize from sequence`() {
        val seq = sequenceOf(StrVal("a"), IntVal(2L))
        val set = SetVal(seq)
        assertEquals(2, set.size)
        assertTrue(set.contains(StrVal("a")))
        assertTrue(set.contains(IntVal(2L)))
    }

    // -- contains / containsAll --

    @Test
    fun `should return true when element present`() {
        val set = SetVal(StrVal("a"), IntVal(5L))
        assertTrue(set.contains(StrVal("a")))
        assertTrue(set.contains(IntVal(5L)))
    }

    @Test
    fun `should return false when element absent`() {
        val set = SetVal(StrVal("a"))
        assertFalse(set.contains(StrVal("z")))
    }

    @Test
    fun `should return true when all elements present`() {
        val set = SetVal(StrVal("a"), IntVal(1L), BoolVal.F)
        assertTrue(set.containsAll(listOf(StrVal("a"), IntVal(1L))))
    }

    @Test
    fun `should return false when some elements missing`() {
        val set = SetVal(StrVal("a"))
        assertFalse(set.containsAll(listOf(StrVal("a"), StrVal("b"))))
    }

    // -- isEmpty / isNotEmpty --

    @Test
    fun `should return true for isEmpty on empty set`() {
        assertTrue(SetVal().isEmpty())
    }

    @Test
    fun `should return false for isEmpty on non-empty set`() {
        assertFalse(SetVal(StrVal("a")).isEmpty())
    }

    @Test
    fun `should return false for isNotEmpty on empty set`() {
        assertFalse(SetVal().isNotEmpty())
    }

    @Test
    fun `should return true for isNotEmpty on non-empty set`() {
        assertTrue(SetVal(StrVal("a")).isNotEmpty())
    }

    // -- size --

    @Test
    fun `should return zero for empty set`() {
        assertEquals(0, SetVal().size)
    }

    @Test
    fun `should not increase size on duplicate add`() {
        val set = SetVal(StrVal("a"))
        set.add(StrVal("a"))
        assertEquals(1, set.size)
    }

    // -- map --

    @Test
    fun `should transform each element with map`() {
        val set = SetVal(IntVal(1L), IntVal(2L))
        val mapped = set.map { (it as IntVal).toInt() * 10 }
        assertEquals(2, mapped.size)
        assertTrue(mapped.contains(10))
        assertTrue(mapped.contains(20))
    }

    // -- forEach --

    @Test
    fun `should iterate all elements with forEach`() {
        val set = SetVal(StrVal("a"), StrVal("b"))
        val collected = mutableListOf<IValue>()
        set.forEach { collected.add(it) }
        assertEquals(2, collected.size)
    }

    // -- asSequence --

    @Test
    fun `should return lazy sequence with asSequence`() {
        val set = SetVal(IntVal(1L), IntVal(2L), IntVal(3L))
        val seq = set.asSequence()
        assertEquals(3, seq.count())
    }

    // -- toList --

    @Test
    fun `should convert to list preserving elements with toList`() {
        val set = SetVal(StrVal("a"), IntVal(1L))
        val list = set.toList()
        assertEquals(2, list.size)
        assertTrue(list.contains(StrVal("a")))
        assertTrue(list.contains(IntVal(1L)))
    }
}
