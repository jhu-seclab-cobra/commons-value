package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.IPrimitiveVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for [StrVal] derived from the design doc.
 *
 * - `should store string core` — primary constructor
 * - `should default to empty string` — default constructor
 * - `should return true from startsWith when prefix matches` — startsWith match
 * - `should return false from startsWith when prefix does not match` — startsWith mismatch
 * - `should return substring after delimiter` — substringAfter found
 * - `should return full string when substringAfter delimiter not found` — substringAfter missing
 * - `should return substring before delimiter` — substringBefore found
 * - `should return full string when substringBefore delimiter not found` — substringBefore missing
 * - `should return true from equals when string matches case-sensitive` — equals(String) exact
 * - `should return false from equals when case differs and ignoreCase false` — equals(String) case mismatch
 * - `should return true from equals when case differs and ignoreCase true` — equals(String) ignore case
 * - `should return true from equals when IPrimitiveVal is same StrVal` — equals(IPrimitiveVal) match
 * - `should return false from equals when IPrimitiveVal is different StrVal` — equals(IPrimitiveVal) mismatch
 * - `should return true from equals when IPrimitiveVal case differs and ignoreCase true` — equals(IPrimitiveVal) ignore case
 * - `should return true from equals when IPrimitiveVal core matches as string` — equals(IPrimitiveVal) cross-type match
 * - `should return false from equals when IPrimitiveVal core differs as string` — equals(IPrimitiveVal) cross-type mismatch
 * - `should return uppercase StrVal` — uppercase
 * - `should return lowercase StrVal` — lowercase
 * - `should return trimmed StrVal` — trim
 * - `should return true from contains when substring present` — contains match
 * - `should return false from contains when substring absent` — contains mismatch
 * - `should return char at positive index` — get(Int) positive
 * - `should return char at negative index counting from end` — get(Int) negative
 * - `should throw IndexOutOfBoundsException for out-of-range index` — get(Int) error
 * - `should return char at NumVal index` — get(NumVal) positive
 * - `should return char at negative NumVal index` — get(NumVal) negative
 * - `should throw IndexOutOfBoundsException for out-of-range NumVal index` — get(NumVal) error
 * - `should return correct length` — length property
 * - `should return zero length for empty string` — length boundary
 * - `should implement IPrimitiveVal` — type hierarchy
 */
internal class StrValTest {

    // --- Constructors ---

    @Test
    fun `should store string core`() {
        assertEquals("hello", StrVal("hello").core)
    }

    @Test
    fun `should default to empty string`() {
        assertEquals("", StrVal().core)
    }

    // --- startsWith ---

    @Test
    fun `should return true from startsWith when prefix matches`() {
        assertTrue(StrVal("Hello World").startsWith("Hello"))
    }

    @Test
    fun `should return false from startsWith when prefix does not match`() {
        assertFalse(StrVal("Hello World").startsWith("World"))
    }

    @Test
    fun `should return true from startsWith with empty prefix`() {
        assertTrue(StrVal("Hello").startsWith(""))
    }

    // --- substringAfter ---

    @Test
    fun `should return substring after delimiter`() {
        assertEquals("World", StrVal("Hello World").substringAfter("Hello "))
    }

    @Test
    fun `should return full string when substringAfter delimiter not found`() {
        assertEquals("Hello World", StrVal("Hello World").substringAfter("missing"))
    }

    // --- substringBefore ---

    @Test
    fun `should return substring before delimiter`() {
        assertEquals("Hello", StrVal("Hello World").substringBefore(" World"))
    }

    @Test
    fun `should return full string when substringBefore delimiter not found`() {
        assertEquals("Hello World", StrVal("Hello World").substringBefore("missing"))
    }

    // --- equals(String, ignoreCase) ---

    @Test
    fun `should return true from equals when string matches case-sensitive`() {
        assertTrue(StrVal("Test").equals("Test", ignoreCase = false))
    }

    @Test
    fun `should return false from equals when case differs and ignoreCase false`() {
        assertFalse(StrVal("Test").equals("test", ignoreCase = false))
    }

    @Test
    fun `should return true from equals when case differs and ignoreCase true`() {
        assertTrue(StrVal("Test").equals("test", ignoreCase = true))
    }

    // --- equals(IPrimitiveVal, ignoreCase) ---

    @Test
    fun `should return true from equals when IPrimitiveVal is same StrVal`() {
        assertTrue(StrVal("Test").equals(StrVal("Test"), ignoreCase = false))
    }

    @Test
    fun `should return false from equals when IPrimitiveVal is different StrVal`() {
        assertFalse(StrVal("Test").equals(StrVal("Other"), ignoreCase = false))
    }

    @Test
    fun `should return true from equals when IPrimitiveVal case differs and ignoreCase true`() {
        assertTrue(StrVal("Test").equals(StrVal("test"), ignoreCase = true))
    }

    @Test
    fun `should return true from equals when IPrimitiveVal core matches as string`() {
        assertTrue(StrVal("42").equals(NumVal(42), ignoreCase = false))
    }

    @Test
    fun `should return false from equals when IPrimitiveVal core differs as string`() {
        assertFalse(StrVal("hello").equals(NumVal(42), ignoreCase = false))
    }

    // --- uppercase / lowercase / trim ---

    @Test
    fun `should return uppercase StrVal`() {
        assertEquals("HELLO", StrVal("hello").uppercase().core)
    }

    @Test
    fun `should return lowercase StrVal`() {
        assertEquals("hello", StrVal("HELLO").lowercase().core)
    }

    @Test
    fun `should return trimmed StrVal`() {
        assertEquals("hello", StrVal("  hello  ").trim().core)
    }

    @Test
    fun `should return StrVal type from uppercase`() {
        assertTrue(StrVal("x").uppercase() is StrVal)
    }

    @Test
    fun `should return StrVal type from lowercase`() {
        assertTrue(StrVal("X").lowercase() is StrVal)
    }

    @Test
    fun `should return StrVal type from trim`() {
        assertTrue(StrVal(" x ").trim() is StrVal)
    }

    // --- contains ---

    @Test
    fun `should return true from contains when substring present`() {
        assertTrue(StrVal("Hello World").contains("World"))
    }

    @Test
    fun `should return false from contains when substring absent`() {
        assertFalse(StrVal("Hello World").contains("missing"))
    }

    @Test
    fun `should return true from contains with empty substring`() {
        assertTrue(StrVal("Hello").contains(""))
    }

    // --- get(Int) ---

    @Test
    fun `should return char at positive index`() {
        assertEquals('H', StrVal("Hello")[0])
    }

    @Test
    fun `should return last char at positive index`() {
        assertEquals('o', StrVal("Hello")[4])
    }

    @Test
    fun `should return char at negative index counting from end`() {
        assertEquals('o', StrVal("Hello")[-1])
    }

    @Test
    fun `should return first char at largest negative index`() {
        assertEquals('H', StrVal("Hello")[-5])
    }

    @Test
    fun `should throw IndexOutOfBoundsException for positive out-of-range index`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("Hello")[5]
        }
    }

    @Test
    fun `should throw IndexOutOfBoundsException for negative out-of-range index`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("Hello")[-6]
        }
    }

    @Test
    fun `should throw IndexOutOfBoundsException for index on empty string`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("")[0]
        }
    }

    // --- get(NumVal) ---

    @Test
    fun `should return char at NumVal positive index`() {
        assertEquals('H', StrVal("Hello")[NumVal(0)])
    }

    @Test
    fun `should return char at NumVal negative index`() {
        assertEquals('o', StrVal("Hello")[NumVal(-1)])
    }

    @Test
    fun `should throw IndexOutOfBoundsException for out-of-range NumVal index`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("Hello")[NumVal(10)]
        }
    }

    // --- length ---

    @Test
    fun `should return correct length`() {
        assertEquals(11, StrVal("Hello World").length)
    }

    @Test
    fun `should return zero length for empty string`() {
        assertEquals(0, StrVal().length)
    }

    // --- Type hierarchy ---

    @Test
    fun `should implement IPrimitiveVal`() {
        val value: IPrimitiveVal = StrVal("test")
        assertTrue(value is StrVal)
    }
}
