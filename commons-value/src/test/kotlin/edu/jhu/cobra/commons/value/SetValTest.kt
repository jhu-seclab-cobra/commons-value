package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [SetVal] derived from design-collection.md.
 *
 * Constructors:
 * - `should create empty set with default constructor`
 * - `should create empty set with sized constructor`
 * - `should copy elements from collection constructor`
 * - `should initialize from vararg elements`
 * - `should initialize from sequence`
 *
 * add / remove:
 * - `should return true when adding new element`
 * - `should return false when adding duplicate element`
 * - `should return true when removing present element`
 * - `should return false when removing absent element`
 *
 * plus / plusAssign / minus / minusAssign:
 * - `should return new SetVal with element added on plus`
 * - `should not modify original set on plus`
 * - `should mutably add element on plusAssign`
 * - `should return new SetVal with element removed on minus`
 * - `should not modify original set on minus`
 * - `should mutably remove element on minusAssign`
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
 *
 * deepCopy:
 * - `should deep copy set without sharing mutable state`
 *
 * Boundary:
 * - `should preserve insertion order`
 * - `should handle empty set operations`
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

    // -- add / remove --

    @Test
    fun `should return true when adding new element`() {
        val set = SetVal()
        assertTrue(set.add(StrVal("a")))
        assertEquals(1, set.size)
    }

    @Test
    fun `should return false when adding duplicate element`() {
        val set = SetVal(StrVal("a"))
        assertFalse(set.add(StrVal("a")))
        assertEquals(1, set.size)
    }

    @Test
    fun `should return true when removing present element`() {
        val set = SetVal(StrVal("a"), IntVal(1L))
        assertTrue(set.remove(StrVal("a")))
        assertEquals(1, set.size)
    }

    @Test
    fun `should return false when removing absent element`() {
        val set = SetVal(StrVal("a"))
        assertFalse(set.remove(StrVal("z")))
        assertEquals(1, set.size)
    }

    // -- plus / plusAssign / minus / minusAssign --

    @Test
    fun `should return new SetVal with element added on plus`() {
        val original = SetVal(StrVal("a"))
        val result = original + StrVal("b")
        assertEquals(2, result.size)
        assertTrue(result.contains(StrVal("b")))
    }

    @Test
    fun `should not modify original set on plus`() {
        val original = SetVal(StrVal("a"))
        original + StrVal("b")
        assertEquals(1, original.size)
    }

    @Test
    fun `should mutably add element on plusAssign`() {
        val set = SetVal(StrVal("a"))
        set += StrVal("b")
        assertEquals(2, set.size)
        assertTrue(set.contains(StrVal("b")))
    }

    @Test
    fun `should return new SetVal with element removed on minus`() {
        val original = SetVal(StrVal("a"), StrVal("b"))
        val result = original - StrVal("b")
        assertEquals(1, result.size)
        assertFalse(result.contains(StrVal("b")))
    }

    @Test
    fun `should not modify original set on minus`() {
        val original = SetVal(StrVal("a"), StrVal("b"))
        original - StrVal("b")
        assertEquals(2, original.size)
    }

    @Test
    fun `should mutably remove element on minusAssign`() {
        val set = SetVal(StrVal("a"), StrVal("b"))
        set -= StrVal("b")
        assertEquals(1, set.size)
        assertFalse(set.contains(StrVal("b")))
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

    // -- Boundary --

    @Test
    fun `should preserve insertion order`() {
        val set = SetVal(StrVal("c"), StrVal("a"), StrVal("b"))
        val list = set.toList()
        assertEquals(StrVal("c"), list[0])
        assertEquals(StrVal("a"), list[1])
        assertEquals(StrVal("b"), list[2])
    }

    // -- deepCopy --

    @Test
    fun `should deep copy set without sharing mutable state`() {
        val inner = ListVal(IntVal(1L))
        val set = SetVal(StrVal("x"), inner)
        val copy = set.deepCopy()
        assertEquals(set, copy)
        copy.filterIsInstance<ListVal>().single().add(IntVal(2L))
        assertEquals(1, inner.size)
    }

    @Test
    fun `should handle empty set operations`() {
        val set = SetVal()
        assertFalse(set.contains(NullVal))
        assertFalse(set.remove(StrVal("x")))
        assertEquals(0, set.map { it }.size)
        assertEquals(0, set.toList().size)
    }
}
