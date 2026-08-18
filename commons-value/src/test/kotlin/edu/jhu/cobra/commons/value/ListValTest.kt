package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [ListVal] read-only operations, derived from design-collection.md.
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
}
