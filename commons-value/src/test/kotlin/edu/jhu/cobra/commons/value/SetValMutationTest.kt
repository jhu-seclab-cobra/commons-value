package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [SetVal] mutation, operators, copy, and equality — derived from design-collection.md.
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
 * deepCopy:
 * - `should deep copy set without sharing mutable state`
 * - `should throw IllegalArgumentException when deepCopy meets cyclic set` — Cyclic value graph
 *   rejected with a diagnosable error instead of StackOverflowError.
 *
 * Equality:
 * - `should equal plain set with same content symmetrically` — JDK collection contract.
 * - `should not equal list with same elements`
 *
 * Boundary:
 * - `should preserve insertion order`
 * - `should handle empty set operations`
 */
internal class SetValMutationTest {
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
    fun `should throw IllegalArgumentException when deepCopy meets cyclic set`() {
        val set = SetVal()
        set.add(set)
        assertFailsWith<IllegalArgumentException> { set.deepCopy() }
    }

    // -- Equality --

    @Test
    fun `should equal plain set with same content symmetrically`() {
        val setVal = SetVal(IntVal(1L), IntVal(2L))
        val plain: Set<IValue> = setOf(IntVal(1L), IntVal(2L))
        assertTrue(plain == setVal)
        assertTrue(setVal == plain)
        assertEquals(plain.hashCode(), setVal.hashCode())
    }

    @Test
    fun `should not equal list with same elements`() {
        val setVal = SetVal(IntVal(1L))
        assertFalse(setVal.equals(ListVal(IntVal(1L))))
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

    @Test
    fun `should handle empty set operations`() {
        val set = SetVal()
        assertFalse(set.contains(NullVal))
        assertFalse(set.remove(StrVal("x")))
        assertEquals(0, set.map { it }.size)
        assertEquals(0, set.toList().size)
    }
}
