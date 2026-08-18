package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [ListVal] mutation and derived operations, derived from design-collection.md.
 *
 * plus / plusAssign:
 * - `should return new ListVal with element appended`
 * - `should not modify original list on plus`
 * - `should mutably append element on plusAssign`
 *
 * minus / minusAssign:
 * - `should return new ListVal with element removed`
 * - `should not modify original list on minus`
 * - `should mutably remove element on minusAssign`
 * - `should return unchanged list when minus element absent`
 *
 * isEmpty / isNotEmpty:
 * - `should return true for isEmpty on empty list`
 * - `should return false for isEmpty on non-empty list`
 * - `should return false for isNotEmpty on empty list`
 * - `should return true for isNotEmpty on non-empty list`
 *
 * size:
 * - `should return zero size for empty list`
 * - `should return correct size after mutations`
 *
 * map / flatMap / forEach / asSequence:
 * - `should transform each element with map`
 * - `should flatten transformed lists with flatMap`
 * - `should iterate all elements with forEach`
 * - `should return lazy sequence with asSequence`
 *
 * deepCopy:
 * - `should deep copy nested list without sharing mutable state`
 * - `should throw IllegalArgumentException when deepCopy meets cyclic list` — Cyclic value graph
 *   rejected with a diagnosable error instead of StackOverflowError.
 *
 * Equality:
 * - `should equal plain list with same content symmetrically` — JDK collection contract.
 * - `should not equal set with same elements`
 *
 * Boundary:
 * - `should handle single element list`
 * - `should handle nested IValue elements`
 */
internal class ListValMutationTest {
    // -- plus / plusAssign --

    @Test
    fun `should return new ListVal with element appended`() {
        val original = ListVal(StrVal("a"))
        val result = original + StrVal("b")
        assertEquals(2, result.size)
        assertEquals(StrVal("b"), result[1])
    }

    @Test
    fun `should not modify original list on plus`() {
        val original = ListVal(StrVal("a"))
        original + StrVal("b")
        assertEquals(1, original.size)
    }

    @Test
    fun `should mutably append element on plusAssign`() {
        val list = ListVal(StrVal("a"))
        list += StrVal("b")
        assertEquals(2, list.size)
        assertEquals(StrVal("b"), list[1])
    }

    // -- minus / minusAssign --

    @Test
    fun `should return new ListVal with element removed`() {
        val original = ListVal(StrVal("a"), StrVal("b"), StrVal("c"))
        val result = original - StrVal("b")
        assertEquals(2, result.size)
        assertEquals(StrVal("a"), result[0])
        assertEquals(StrVal("c"), result[1])
    }

    @Test
    fun `should not modify original list on minus`() {
        val original = ListVal(StrVal("a"), StrVal("b"))
        original - StrVal("b")
        assertEquals(2, original.size)
    }

    @Test
    fun `should mutably remove element on minusAssign`() {
        val list = ListVal(StrVal("a"), StrVal("b"))
        list -= StrVal("b")
        assertEquals(1, list.size)
        assertEquals(StrVal("a"), list[0])
    }

    @Test
    fun `should return unchanged list when minus element absent`() {
        val original = ListVal(StrVal("a"))
        val result = original - StrVal("z")
        assertEquals(1, result.size)
        assertEquals(StrVal("a"), result[0])
    }

    // -- isEmpty / isNotEmpty --

    @Test
    fun `should return true for isEmpty on empty list`() {
        assertTrue(ListVal().isEmpty())
    }

    @Test
    fun `should return false for isEmpty on non-empty list`() {
        assertFalse(ListVal(StrVal("a")).isEmpty())
    }

    @Test
    fun `should return false for isNotEmpty on empty list`() {
        assertFalse(ListVal().isNotEmpty())
    }

    @Test
    fun `should return true for isNotEmpty on non-empty list`() {
        assertTrue(ListVal(StrVal("a")).isNotEmpty())
    }

    // -- size --

    @Test
    fun `should return zero size for empty list`() {
        assertEquals(0, ListVal().size)
    }

    @Test
    fun `should return correct size after mutations`() {
        val list = ListVal(StrVal("a"))
        list += StrVal("b")
        assertEquals(2, list.size)
        list -= StrVal("a")
        assertEquals(1, list.size)
    }

    // -- map --

    @Test
    fun `should transform each element with map`() {
        val list = ListVal(IntVal(1L), IntVal(2L))
        val mapped = list.map { (it as IntVal).toInt() * 10 }
        assertEquals(listOf(10, 20), mapped)
    }

    // -- flatMap --

    @Test
    fun `should flatten transformed lists with flatMap`() {
        val list = ListVal(StrVal("a"), StrVal("b"))
        val result = list.flatMap { listOf(it, it) }
        assertEquals(4, result.size)
        assertEquals(StrVal("a"), result[0])
        assertEquals(StrVal("a"), result[1])
        assertEquals(StrVal("b"), result[2])
        assertEquals(StrVal("b"), result[3])
    }

    // -- forEach --

    @Test
    fun `should iterate all elements with forEach`() {
        val list = ListVal(StrVal("a"), StrVal("b"), StrVal("c"))
        val collected = mutableListOf<IValue>()
        list.forEach { collected.add(it) }
        assertEquals(3, collected.size)
        assertEquals(StrVal("a"), collected[0])
    }

    // -- asSequence --

    @Test
    fun `should return lazy sequence with asSequence`() {
        val list = ListVal(IntVal(1L), IntVal(2L), IntVal(3L))
        val seq = list.asSequence()
        assertEquals(3, seq.count())
        assertEquals(IntVal(1L), seq.first())
    }

    // -- Boundary --

    @Test
    fun `should handle single element list`() {
        val list = ListVal(NullVal)
        assertEquals(1, list.size)
        assertEquals(NullVal, list[0])
        assertEquals(0, list.indexOf(NullVal))
    }

    @Test
    fun `should handle nested IValue elements`() {
        val inner = ListVal(IntVal(1L), IntVal(2L))
        val outer = ListVal(inner, StrVal("x"))
        assertEquals(2, outer.size)
        assertTrue(outer[0] is ListVal)
        assertEquals(inner, outer[0])
    }

    // -- deepCopy --

    @Test
    fun `should deep copy nested list without sharing mutable state`() {
        val inner = ListVal(IntVal(1L))
        val outer = ListVal(inner, StrVal("x"))
        val copy = outer.deepCopy()
        assertEquals(outer, copy)
        (copy[0] as ListVal).add(IntVal(2L))
        assertEquals(1, inner.size)
    }

    @Test
    fun `should throw IllegalArgumentException when deepCopy meets cyclic list`() {
        val list = ListVal()
        list.add(list)
        assertFailsWith<IllegalArgumentException> { list.deepCopy() }
    }

    // -- Equality --

    @Test
    fun `should equal plain list with same content symmetrically`() {
        val listVal = ListVal(IntVal(1L), IntVal(2L))
        val plain: List<IValue> = listOf(IntVal(1L), IntVal(2L))
        assertTrue(plain == listVal)
        assertTrue(listVal == plain)
        assertEquals(plain.hashCode(), listVal.hashCode())
    }

    @Test
    fun `should not equal set with same elements`() {
        val listVal = ListVal(IntVal(1L))
        assertFalse(listVal.equals(SetVal(IntVal(1L))))
    }
}
