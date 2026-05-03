package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [ListVal] derived from design-collection.md.
 *
 * Constructors:
 * - `should create empty list with default constructor`
 * - `should create empty list with sized constructor`
 * - `should copy elements from list constructor`
 * - `should initialize from vararg elements`
 *
 * get / set:
 * - `should return element at valid index`
 * - `should throw IndexOutOfBoundsException when get index negative`
 * - `should throw IndexOutOfBoundsException when get index equals size`
 * - `should replace element at valid index`
 * - `should throw IndexOutOfBoundsException when set index out of bounds`
 *
 * contains / containsAll:
 * - `should return true when element present`
 * - `should return false when element absent`
 * - `should return true when all elements present`
 * - `should return false when some elements missing`
 *
 * indexOf / lastIndexOf:
 * - `should return first index of duplicate element`
 * - `should return last index of duplicate element`
 * - `should return negative one when element not found for indexOf`
 * - `should return negative one when element not found for lastIndexOf`
 *
 * subList:
 * - `should return sub-range as new ListVal`
 * - `should return empty ListVal when fromIndex equals toIndex`
 * - `should throw IndexOutOfBoundsException when subList indices out of bounds`
 * - `should throw IllegalArgumentException when fromIndex greater than toIndex`
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
 * map / flatMap / forEach / asSequence / toMutableSet:
 * - `should transform each element with map`
 * - `should flatten transformed lists with flatMap`
 * - `should iterate all elements with forEach`
 * - `should return lazy sequence with asSequence`
 * - `should convert to linked set removing duplicates with toMutableSet`
 *
 * Boundary:
 * - `should handle single element list`
 * - `should handle nested IValue elements`
 */
internal class ListValTest {

    // -- Constructors --

    @Test
    fun `should create empty list with default constructor`() {
        val list = ListVal()
        assertTrue(list.isEmpty())
        assertEquals(0, list.size)
    }

    @Test
    fun `should create empty list with sized constructor`() {
        val list = ListVal(10)
        assertTrue(list.isEmpty())
        assertEquals(0, list.size)
    }

    @Test
    fun `should copy elements from list constructor`() {
        val source = listOf(StrVal("a"), IntVal(1L))
        val list = ListVal(source)
        assertEquals(2, list.size)
        assertEquals(StrVal("a"), list[0])
        assertEquals(IntVal(1L), list[1])
    }

    @Test
    fun `should initialize from vararg elements`() {
        val list = ListVal(StrVal("x"), IntVal(7L), BoolVal.T)
        assertEquals(3, list.size)
        assertEquals(StrVal("x"), list[0])
        assertEquals(IntVal(7L), list[1])
        assertEquals(BoolVal.T, list[2])
    }

    // -- get --

    @Test
    fun `should return element at valid index`() {
        val list = ListVal(StrVal("a"), StrVal("b"))
        assertEquals(StrVal("a"), list[0])
        assertEquals(StrVal("b"), list[1])
    }

    @Test
    fun `should throw IndexOutOfBoundsException when get index negative`() {
        val list = ListVal(StrVal("a"))
        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
    }

    @Test
    fun `should throw IndexOutOfBoundsException when get index equals size`() {
        val list = ListVal(StrVal("a"))
        assertFailsWith<IndexOutOfBoundsException> { list[1] }
    }

    // -- set --

    @Test
    fun `should replace element at valid index`() {
        val list = ListVal(StrVal("a"), StrVal("b"))
        list[1] = IntVal(99L)
        assertEquals(IntVal(99L), list[1])
    }

    @Test
    fun `should throw IndexOutOfBoundsException when set index out of bounds`() {
        val list = ListVal(StrVal("a"))
        assertFailsWith<IndexOutOfBoundsException> { list[2] = StrVal("z") }
    }

    // -- contains / containsAll --

    @Test
    fun `should return true when element present`() {
        val list = ListVal(StrVal("a"), IntVal(5L))
        assertTrue(list.contains(StrVal("a")))
        assertTrue(list.contains(IntVal(5L)))
    }

    @Test
    fun `should return false when element absent`() {
        val list = ListVal(StrVal("a"))
        assertFalse(list.contains(StrVal("b")))
    }

    @Test
    fun `should return true when all elements present`() {
        val list = ListVal(StrVal("a"), IntVal(1L), BoolVal.F)
        assertTrue(list.containsAll(listOf(StrVal("a"), IntVal(1L))))
    }

    @Test
    fun `should return false when some elements missing`() {
        val list = ListVal(StrVal("a"))
        assertFalse(list.containsAll(listOf(StrVal("a"), StrVal("b"))))
    }

    // -- indexOf / lastIndexOf --

    @Test
    fun `should return first index of duplicate element`() {
        val list = ListVal(StrVal("a"), IntVal(1L), StrVal("a"))
        assertEquals(0, list.indexOf(StrVal("a")))
    }

    @Test
    fun `should return last index of duplicate element`() {
        val list = ListVal(StrVal("a"), IntVal(1L), StrVal("a"))
        assertEquals(2, list.lastIndexOf(StrVal("a")))
    }

    @Test
    fun `should return negative one when element not found for indexOf`() {
        val list = ListVal(StrVal("a"))
        assertEquals(-1, list.indexOf(StrVal("z")))
    }

    @Test
    fun `should return negative one when element not found for lastIndexOf`() {
        val list = ListVal(StrVal("a"))
        assertEquals(-1, list.lastIndexOf(StrVal("z")))
    }

    // -- subList --

    @Test
    fun `should return sub-range as new ListVal`() {
        val list = ListVal(StrVal("a"), StrVal("b"), StrVal("c"), StrVal("d"))
        val sub = list.subList(1, 3)
        assertEquals(2, sub.size)
        assertEquals(StrVal("b"), sub[0])
        assertEquals(StrVal("c"), sub[1])
    }

    @Test
    fun `should return empty ListVal when fromIndex equals toIndex`() {
        val list = ListVal(StrVal("a"), StrVal("b"))
        val sub = list.subList(1, 1)
        assertEquals(0, sub.size)
    }

    @Test
    fun `should throw IndexOutOfBoundsException when subList indices out of bounds`() {
        val list = ListVal(StrVal("a"))
        assertFailsWith<IndexOutOfBoundsException> { list.subList(0, 5) }
    }

    @Test
    fun `should throw IllegalArgumentException when fromIndex greater than toIndex`() {
        val list = ListVal(StrVal("a"), StrVal("b"), StrVal("c"))
        assertFailsWith<IllegalArgumentException> { list.subList(2, 1) }
    }

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

    // -- toMutableSet --

    @Test
    fun `should convert to linked set removing duplicates with toMutableSet`() {
        val list = ListVal(StrVal("a"), StrVal("a"), IntVal(1L))
        val set = list.toMutableSet()
        assertEquals(2, set.size)
        assertTrue(set.contains(StrVal("a")))
        assertTrue(set.contains(IntVal(1L)))
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
}
