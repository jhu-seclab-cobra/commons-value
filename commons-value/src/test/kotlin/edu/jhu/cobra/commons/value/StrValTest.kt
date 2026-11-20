package edu.jhu.cobra.commons.value

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Black-box tests for [StrVal] derived from the design doc.
 *
 * - `should store string core` — primary constructor
 * - `should default to empty string` — default constructor
 * - `should store text without unpaired surrogate as the same instance` — fast path keeps the argument
 * - `should store canonical text holding units unchanged` — canonical units are kept
 * - `should combine units forming well-formed UTF-8 into the character` — run re-decoding
 * - `should combine units after a unit that starts no sequence` — run re-decoding from a later position
 * - `should keep units that form no well-formed UTF-8` — invalid runs stay units
 * - `should keep a lone high surrogate` — D800 and the first half of a split pair stay
 * - `should keep a low surrogate outside DC80 to DCFF` — DC00 and the second half of a split pair stay
 * - `should re-decode the runs of units on each side of a kept surrogate` — a kept surrogate ends a run
 * - `should give its own core for a canonical core` — construction is idempotent
 * - `should join the two halves of a split pair into the character` — concatenated halves are the pair
 * - `should keep well-formed surrogate pair` — supplementary characters stay
 * - `should equal and hash by canonical core` — equality after canonicalization
 * - `should render core in toString` — toString form
 * - `should canonicalize concatenated cores of single units` — join of byte units
 * - `should encode characters as UTF-8 and units as their bytes` — toByteArray mapping
 * - `should encode lone high surrogate as ED A0 80` — toByteArray of a kept surrogate
 * - `should share the bytes of a kept surrogate with the value of those bytes` — U+D83D and DCED DCA0 DCBD
 * - `should encode concatenation as concatenated bytes` — homomorphism over concatenation
 * - `should encode a pair whose low unit is in DC80 to DCFF as its four bytes` — U+1F480 is F0 9F 92 80
 * - `should find no byte outside UTF-8 in text and pairs` — indexOfByteOutsideUtf8 reads a pair as one character
 * - `should find unpaired units in DC80 to DCFF from the start index` — indexOfByteOutsideUtf8 hits and start index
 * - `should not report surrogates outside DC80 to DCFF` — other unpaired surrogates are not byte units
 * - `should return true from startsWith when prefix matches` — startsWith match
 * - `should return false from startsWith when prefix does not match` — startsWith mismatch
 * - `should return true from startsWith with empty prefix` — startsWith empty boundary
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
 * - `should return false from equals when IPrimitiveVal is NullVal` — "null" text never equals the null value
 * - `should return uppercase StrVal` — uppercase
 * - `should return lowercase StrVal` — lowercase
 * - `should return trimmed StrVal` — trim
 * - `should return StrVal type from uppercase` — uppercase returns StrVal type
 * - `should return StrVal type from lowercase` — lowercase returns StrVal type
 * - `should return StrVal type from trim` — trim returns StrVal type
 * - `should return true from contains when substring present` — contains match
 * - `should return false from contains when substring absent` — contains mismatch
 * - `should return true from contains with empty substring` — contains empty boundary
 * - `should return char at positive index` — get(Int) positive
 * - `should return last char at positive index` — get(Int) last element
 * - `should return char at negative index counting from end` — get(Int) negative
 * - `should return first char at largest negative index` — get(Int) first via negative index
 * - `should throw IndexOutOfBoundsException for positive out-of-range index` — get(Int) positive OOB
 * - `should throw IndexOutOfBoundsException for negative out-of-range index` — get(Int) negative OOB
 * - `should throw IndexOutOfBoundsException for index on empty string` — get(Int) empty string OOB
 * - `should return char at IntVal positive index` — get(IntVal) positive
 * - `should return char at IntVal negative index` — get(IntVal) negative
 * - `should throw IndexOutOfBoundsException for out-of-range IntVal index` — get(IntVal) error
 * - `should throw IndexOutOfBoundsException for IntVal index beyond Int range` — get(IntVal) no truncation
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

    // --- Canonical form ---

    @Test
    fun `should store text without unpaired surrogate as the same instance`() {
        val text = "café 😀 �"
        assertSame(text, StrVal(text).core)
    }

    @Test
    fun `should store canonical text holding units unchanged`() {
        val text = "a\uDCFF\uDCC3b\uDCA9"
        assertEquals(text, StrVal(text).core)
    }

    @Test
    fun `should combine units forming well-formed UTF-8 into the character`() {
        assertEquals("é", StrVal("\uDCC3\uDCA9").core)
        assertEquals("x€y", StrVal("x\uDCE2\uDC82\uDCACy").core)
        assertEquals("😀", StrVal("\uDCF0\uDC9F\uDC98\uDC80").core)
    }

    @Test
    fun `should combine units after a unit that starts no sequence`() {
        assertEquals("\uDCFFé", StrVal("\uDCFF\uDCC3\uDCA9").core)
        assertEquals("é\uDCA9", StrVal("\uDCC3\uDCA9\uDCA9").core)
    }

    @Test
    fun `should keep units that form no well-formed UTF-8`() {
        val text = "\uDCC0\uDCAF\uDCED\uDCA0\uDC80\uDCF5\uDC80"
        assertEquals(text, StrVal(text).core)
    }

    @Test
    fun `should keep a lone high surrogate`() {
        assertEquals("\uD800", StrVal("\uD800").core)
        assertEquals("a\uD83Db", StrVal("a\uD83Db").core)
        assertEquals("\uD83D", StrVal("😀".substring(0, 1)).core)
    }

    @Test
    fun `should keep a low surrogate outside DC80 to DCFF`() {
        assertEquals("\uDC00", StrVal("\uDC00").core)
        assertEquals("\uDE00", StrVal("😀".substring(1)).core)
    }

    @Test
    fun `should re-decode the runs of units on each side of a kept surrogate`() {
        assertEquals("\uDCC3\uDC00\uDCA9", StrVal("\uDCC3\uDC00\uDCA9").core)
        assertEquals("\uDC00é", StrVal("\uDC00\uDCC3\uDCA9").core)
        assertEquals("é\uD800", StrVal("\uDCC3\uDCA9\uD800").core)
    }

    @Test
    fun `should give its own core for a canonical core`() {
        val inputs = listOf("\uD83D", "\uDCC3\uDCA9", "\uDCC3\uDC00\uDCA9", "a\uDCFF𐃃\uDCA9", "😀\uDCFF")
        for (input in inputs) {
            val core = StrVal(input).core
            assertEquals(core, StrVal(core).core, input)
        }
    }

    @Test
    fun `should join the two halves of a split pair into the character`() {
        val emoji = "😀"
        val halves = StrVal(emoji.substring(0, 1)).core + StrVal(emoji.substring(1)).core
        assertEquals(StrVal(emoji), StrVal(halves))
    }

    @Test
    fun `should keep well-formed surrogate pair`() {
        assertEquals("😀\uDCFF", StrVal("😀\uDCFF").core)
    }

    @Test
    fun `should equal and hash by canonical core`() {
        val joined = StrVal("\uDCC3\uDCA9")
        assertEquals(StrVal("é"), joined)
        assertEquals(StrVal("é").hashCode(), joined.hashCode())
        assertNotEquals(StrVal("\uDCFE"), StrVal("\uDCFF"))
        assertNotEquals(StrVal("?"), StrVal("\uDCFF"))
    }

    @Test
    fun `should render core in toString`() {
        assertEquals("StrVal{ab}", StrVal("ab").toString())
    }

    @Test
    fun `should canonicalize concatenated cores of single units`() {
        assertEquals("é".strVal, (StrVal("\uDCC3").core + StrVal("\uDCA9").core).strVal)
    }

    // --- toByteArray ---

    @Test
    fun `should encode characters as UTF-8 and units as their bytes`() {
        val expected = bytesOf(0x61, 0xC3, 0xA9, 0xFF, 0xF0, 0x9F, 0x98, 0x80)
        assertContentEquals(expected, StrVal("aé\uDCFF😀").toByteArray())
        assertContentEquals("�".toByteArray(Charsets.UTF_8), StrVal("�").toByteArray())
    }

    @Test
    fun `should encode lone high surrogate as ED A0 80`() {
        assertContentEquals(bytesOf(0xED, 0xA0, 0x80), StrVal("\uD800").toByteArray())
    }

    @Test
    fun `should share the bytes of a kept surrogate with the value of those bytes`() {
        val kept = StrVal("\uD83D")
        val units = StrVal("\uDCED\uDCA0\uDCBD")
        assertContentEquals(bytesOf(0xED, 0xA0, 0xBD), kept.toByteArray())
        assertContentEquals(kept.toByteArray(), units.toByteArray())
        assertNotEquals(kept, units)
        assertEquals(units, kept.toByteArray().strVal)
    }

    @Test
    fun `should encode concatenation as concatenated bytes`() {
        val parts = listOf("a", "é", "\uDCC3", "\uDCA9", "\uDCFF", "😀", "\uDCF0\uDC9F", "\uDC98\uDC80", "")
        for (left in parts) {
            for (right in parts) {
                val joined = StrVal(StrVal(left).core + StrVal(right).core)
                val expected = StrVal(left).toByteArray() + StrVal(right).toByteArray()
                assertContentEquals(expected, joined.toByteArray(), "$left + $right")
            }
        }
    }

    @Test
    fun `should encode a pair whose low unit is in DC80 to DCFF as its four bytes`() {
        assertContentEquals(bytesOf(0xF0, 0x9F, 0x92, 0x80), StrVal("💀").toByteArray())
        assertContentEquals(bytesOf(0xF0, 0x9F, 0x92, 0x80, 0x80), StrVal("💀\uDC80").toByteArray())
    }

    private fun bytesOf(vararg values: Int): ByteArray = ByteArray(values.size) { values[it].toByte() }

    // --- indexOfByteOutsideUtf8 ---

    @Test
    fun `should find no byte outside UTF-8 in text and pairs`() {
        assertEquals(-1, "".indexOfByteOutsideUtf8())
        assertEquals(-1, "aé😀".indexOfByteOutsideUtf8())
        assertEquals(-1, "💀".indexOfByteOutsideUtf8())
        assertEquals(-1, "􏳿".indexOfByteOutsideUtf8())
    }

    @Test
    fun `should find unpaired units in DC80 to DCFF from the start index`() {
        assertEquals(2, "💀\uDC80".indexOfByteOutsideUtf8())
        assertEquals(1, "a\uDCFFb\uDC80".indexOfByteOutsideUtf8())
        assertEquals(3, "a\uDCFFb\uDC80".indexOfByteOutsideUtf8(startIndex = 2))
        assertEquals(-1, "a\uDCFFb".indexOfByteOutsideUtf8(startIndex = 2))
        assertEquals(1, "\uDCFF\uDCFE".indexOfByteOutsideUtf8(startIndex = 1))
    }

    @Test
    fun `should not report surrogates outside DC80 to DCFF`() {
        assertEquals(-1, "\uD800".indexOfByteOutsideUtf8())
        assertEquals(-1, "\uDC00\uDD00".indexOfByteOutsideUtf8())
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
        assertTrue(StrVal("42").equals(IntVal(42L), ignoreCase = false))
    }

    @Test
    fun `should return false from equals when IPrimitiveVal core differs as string`() {
        assertFalse(StrVal("hello").equals(IntVal(42L), ignoreCase = false))
    }

    @Test
    fun `should return false from equals when IPrimitiveVal is NullVal`() {
        assertFalse(StrVal("null").equals(NullVal, ignoreCase = false))
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

    // --- get(IntVal) ---

    @Test
    fun `should return char at IntVal positive index`() {
        assertEquals('H', StrVal("Hello")[IntVal(0L)])
    }

    @Test
    fun `should return char at IntVal negative index`() {
        assertEquals('o', StrVal("Hello")[IntVal(-1L)])
    }

    @Test
    fun `should throw IndexOutOfBoundsException for out-of-range IntVal index`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("Hello")[IntVal(10L)]
        }
    }

    @Test
    fun `should throw IndexOutOfBoundsException for IntVal index beyond Int range`() {
        assertFailsWith<IndexOutOfBoundsException> {
            StrVal("Hello")[IntVal(1L shl 32)]
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
