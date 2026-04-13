package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import edu.jhu.cobra.commons.value.boolVal
import edu.jhu.cobra.commons.value.compareTo
import edu.jhu.cobra.commons.value.isInByteRange
import edu.jhu.cobra.commons.value.isInIntRange
import edu.jhu.cobra.commons.value.isInLongRange
import edu.jhu.cobra.commons.value.isInShortRange
import edu.jhu.cobra.commons.value.numVal
import edu.jhu.cobra.commons.value.primitiveVal
import edu.jhu.cobra.commons.value.startsWith
import edu.jhu.cobra.commons.value.strVal
import edu.jhu.cobra.commons.value.toRegex
import java.io.File
import java.math.BigInteger
import java.text.ParseException
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for PrimitiveUtils extension functions derived from the design doc.
 *
 * - `should wrap Byte as NumVal` — Number.numVal Byte
 * - `should wrap Short as NumVal` — Number.numVal Short
 * - `should wrap Int as NumVal` — Number.numVal Int
 * - `should wrap Long as NumVal` — Number.numVal Long
 * - `should wrap Float as NumVal` — Number.numVal Float
 * - `should wrap Double as NumVal` — Number.numVal Double
 * - `should parse integer string to NumVal` — String.numVal integer
 * - `should parse negative integer string to NumVal` — String.numVal negative integer
 * - `should parse decimal string to NumVal` — String.numVal decimal
 * - `should parse negative decimal string to NumVal` — String.numVal negative decimal
 * - `should parse large integer string to Long NumVal` — String.numVal large integer
 * - `should throw ParseException for invalid string numVal` — String.numVal error
 * - `should wrap String as StrVal` — String.strVal
 * - `should wrap empty String as StrVal` — String.strVal boundary
 * - `should wrap Char as StrVal` — Char.strVal
 * - `should wrap Path as StrVal` — Path.strVal
 * - `should wrap File as StrVal` — File.strVal
 * - `should wrap true as BoolVal T` — Boolean.boolVal true
 * - `should wrap false as BoolVal F` — Boolean.boolVal false
 * - `should convert null to NullVal via primitiveVal` — Any?.primitiveVal null
 * - `should convert Number to NumVal via primitiveVal` — Any?.primitiveVal Number
 * - `should convert String to StrVal via primitiveVal` — Any?.primitiveVal String
 * - `should convert Boolean to BoolVal via primitiveVal` — Any?.primitiveVal Boolean
 * - `should return IPrimitiveVal unchanged via primitiveVal` — Any?.primitiveVal identity
 * - `should throw IllegalArgumentException for unsupported primitiveVal type` — primitiveVal error
 * - `should compare NumVal less than NumVal` — compareTo NumVal <
 * - `should compare NumVal greater than NumVal` — compareTo NumVal >
 * - `should compare NumVal equal to NumVal` — compareTo NumVal ==
 * - `should compare NumVal with different Number subtypes` — compareTo cross-subtype
 * - `should compare StrVal lexicographically` — compareTo StrVal
 * - `should compare StrVal equal` — compareTo StrVal ==
 * - `should compare BoolVal F less than T` — compareTo BoolVal
 * - `should compare BoolVal T greater than F` — compareTo BoolVal
 * - `should compare BoolVal equal` — compareTo BoolVal ==
 * - `should compare NullVal equal to NullVal` — compareTo NullVal ==
 * - `should throw IllegalArgumentException for cross-type compareTo` — compareTo error
 * - `should escape special regex chars in StrVal toRegex` — StrVal.toRegex escape
 * - `should replace Unsure STR placeholder with wildcard in StrVal toRegex` — StrVal.toRegex STR
 * - `should replace Unsure NUM placeholder with digit pattern in StrVal toRegex` — StrVal.toRegex NUM
 * - `should replace Unsure BOOL placeholder with bool pattern in StrVal toRegex` — StrVal.toRegex BOOL
 * - `should produce case-insensitive regex from StrVal toRegex` — StrVal.toRegex ignoreCase
 * - `should match any string for Unsure ANY toRegex` — Unsure.toRegex ANY
 * - `should match any string for Unsure STR toRegex` — Unsure.toRegex STR
 * - `should match digits for Unsure NUM toRegex` — Unsure.toRegex NUM
 * - `should match true or false for Unsure BOOL toRegex` — Unsure.toRegex BOOL
 * - `should produce case-insensitive regex from Unsure toRegex` — Unsure.toRegex ignoreCase
 * - `should return true when String starts with StrVal content` — String.startsWith match
 * - `should return false when String does not start with StrVal content` — String.startsWith mismatch
 * - `should return true when String starts with empty StrVal` — String.startsWith boundary
 * - `should return false when empty String checked against non-empty StrVal` — String.startsWith boundary
 * - `should return true from isInLongRange for Long MAX_VALUE` — isInLongRange boundary
 * - `should return true from isInLongRange for Long MIN_VALUE` — isInLongRange boundary
 * - `should return true from isInLongRange for zero` — isInLongRange representative
 * - `should return true from isInLongRange for Byte Short Int` — isInLongRange smaller types
 * - `should return false from isInLongRange for value exceeding Long MAX` — isInLongRange out
 * - `should return true from isInIntRange for Int MAX_VALUE` — isInIntRange boundary
 * - `should return true from isInIntRange for Int MIN_VALUE` — isInIntRange boundary
 * - `should return true from isInIntRange for zero` — isInIntRange representative
 * - `should return false from isInIntRange for Long MAX_VALUE` — isInIntRange out
 * - `should return true from isInShortRange for Short MAX_VALUE` — isInShortRange boundary
 * - `should return true from isInShortRange for Short MIN_VALUE` — isInShortRange boundary
 * - `should return true from isInShortRange for zero` — isInShortRange representative
 * - `should return false from isInShortRange for Int MAX_VALUE` — isInShortRange out
 * - `should return true from isInByteRange for Byte MAX_VALUE` — isInByteRange boundary
 * - `should return true from isInByteRange for Byte MIN_VALUE` — isInByteRange boundary
 * - `should return true from isInByteRange for zero` — isInByteRange representative
 * - `should return false from isInByteRange for Short MAX_VALUE` — isInByteRange out
 */
internal class PrimitiveUtilsTest {

    // --- Number.numVal ---

    @Test
    fun `should wrap Byte as NumVal`() {
        assertEquals(42.toByte(), 42.toByte().numVal.core)
    }

    @Test
    fun `should wrap Short as NumVal`() {
        assertEquals(42.toShort(), 42.toShort().numVal.core)
    }

    @Test
    fun `should wrap Int as NumVal`() {
        assertEquals(42, 42.numVal.core)
    }

    @Test
    fun `should wrap Long as NumVal`() {
        assertEquals(42L, 42L.numVal.core)
    }

    @Test
    fun `should wrap Float as NumVal`() {
        assertEquals(3.14f, 3.14f.numVal.core)
    }

    @Test
    fun `should wrap Double as NumVal`() {
        assertEquals(3.14, 3.14.numVal.core)
    }

    // --- String.numVal ---

    @Test
    fun `should parse integer string to NumVal`() {
        assertEquals(42, "42".numVal.core)
    }

    @Test
    fun `should parse negative integer string to NumVal`() {
        assertEquals(-42, "-42".numVal.core)
    }

    @Test
    fun `should parse decimal string to NumVal`() {
        assertEquals(3.14, "3.14".numVal.core)
    }

    @Test
    fun `should parse negative decimal string to NumVal`() {
        assertEquals(-3.14, "-3.14".numVal.core)
    }

    @Test
    fun `should parse large integer string to Long NumVal`() {
        assertEquals(9999999999L, "9999999999".numVal.core)
    }

    @Test
    fun `should throw ParseException for invalid string numVal`() {
        assertFailsWith<ParseException> {
            "not a number".numVal
        }
    }

    // --- String.strVal / Char.strVal / Path.strVal / File.strVal ---

    @Test
    fun `should wrap String as StrVal`() {
        assertEquals("hello", "hello".strVal.core)
    }

    @Test
    fun `should wrap empty String as StrVal`() {
        assertEquals("", "".strVal.core)
    }

    @Test
    fun `should wrap Char as StrVal`() {
        assertEquals("A", 'A'.strVal.core)
    }

    @Test
    fun `should wrap Path as StrVal`() {
        val path = Path("/home/user/file.txt")
        assertEquals("/home/user/file.txt", path.strVal.core)
    }

    @Test
    fun `should wrap File as StrVal`() {
        val file = File("/home/user/file.txt")
        assertEquals("/home/user/file.txt", file.strVal.core)
    }

    // --- Boolean.boolVal ---

    @Test
    fun `should wrap true as BoolVal T`() {
        assertEquals(BoolVal.T, true.boolVal)
    }

    @Test
    fun `should wrap false as BoolVal F`() {
        assertEquals(BoolVal.F, false.boolVal)
    }

    // --- Any?.primitiveVal ---

    @Test
    fun `should convert null to NullVal via primitiveVal`() {
        assertEquals(NullVal, null.primitiveVal)
    }

    @Test
    fun `should convert Number to NumVal via primitiveVal`() {
        assertEquals(NumVal(42), 42.primitiveVal)
    }

    @Test
    fun `should convert String to StrVal via primitiveVal`() {
        assertEquals(StrVal("hello"), "hello".primitiveVal)
    }

    @Test
    fun `should convert Boolean to BoolVal via primitiveVal`() {
        assertEquals(BoolVal.T, true.primitiveVal)
    }

    @Test
    fun `should return IPrimitiveVal unchanged via primitiveVal`() {
        val original = NumVal(42)
        assertEquals(original, original.primitiveVal)
    }

    @Test
    fun `should throw IllegalArgumentException for unsupported primitiveVal type`() {
        assertFailsWith<IllegalArgumentException> {
            Object().primitiveVal
        }
    }

    // --- compareTo ---

    @Test
    fun `should compare NumVal less than NumVal`() {
        assertTrue(NumVal(1).compareTo(NumVal(2)) < 0)
    }

    @Test
    fun `should compare NumVal greater than NumVal`() {
        assertTrue(NumVal(2).compareTo(NumVal(1)) > 0)
    }

    @Test
    fun `should compare NumVal equal to NumVal`() {
        assertEquals(0, NumVal(1).compareTo(NumVal(1)))
    }

    @Test
    fun `should compare NumVal with different Number subtypes`() {
        assertTrue(NumVal(1.5).compareTo(NumVal(1)) > 0)
    }

    @Test
    fun `should compare StrVal lexicographically`() {
        assertTrue(StrVal("a").compareTo(StrVal("b")) < 0)
    }

    @Test
    fun `should compare StrVal equal`() {
        assertEquals(0, StrVal("a").compareTo(StrVal("a")))
    }

    @Test
    fun `should compare BoolVal F less than T`() {
        assertTrue(BoolVal.F.compareTo(BoolVal.T) < 0)
    }

    @Test
    fun `should compare BoolVal T greater than F`() {
        assertTrue(BoolVal.T.compareTo(BoolVal.F) > 0)
    }

    @Test
    fun `should compare BoolVal equal`() {
        assertEquals(0, BoolVal.T.compareTo(BoolVal.T))
    }

    @Test
    fun `should compare NullVal equal to NullVal`() {
        assertEquals(0, NullVal.compareTo(NullVal))
    }

    @Test
    fun `should throw IllegalArgumentException for cross-type compareTo`() {
        assertFailsWith<IllegalArgumentException> {
            NumVal(1).compareTo(StrVal("1"))
        }
    }

    // --- StrVal.toRegex ---

    @Test
    fun `should escape special regex chars in StrVal toRegex`() {
        val regex = StrVal("Hello.World").toRegex()
        assertTrue(regex.matches("Hello.World"))
        assertFalse(regex.matches("HelloXWorld"))
    }

    @Test
    fun `should replace Unsure STR placeholder with wildcard in StrVal toRegex`() {
        val regex = StrVal("Hello${Unsure.STR.core}").toRegex()
        assertTrue(regex.matches("Hello World"))
        assertTrue(regex.matches("HelloAnything"))
    }

    @Test
    fun `should replace Unsure NUM placeholder with digit pattern in StrVal toRegex`() {
        val regex = StrVal("item${Unsure.NUM.core}").toRegex()
        assertTrue(regex.matches("item123"))
        assertFalse(regex.matches("itemabc"))
    }

    @Test
    fun `should replace Unsure BOOL placeholder with bool pattern in StrVal toRegex`() {
        val regex = StrVal("is${Unsure.BOOL.core}").toRegex()
        assertTrue(regex.matches("istrue"))
        assertTrue(regex.matches("isfalse"))
        assertFalse(regex.matches("ismaybe"))
    }

    @Test
    fun `should produce case-insensitive regex from StrVal toRegex`() {
        val regex = StrVal("Hello").toRegex(doCaseIgnore = true)
        assertTrue(regex.matches("hello"))
        assertTrue(regex.matches("HELLO"))
    }

    // --- Unsure.toRegex ---

    @Test
    fun `should match any string for Unsure ANY toRegex`() {
        assertTrue("anything".matches(Unsure.ANY.toRegex()))
    }

    @Test
    fun `should match any string for Unsure STR toRegex`() {
        assertTrue("anything".matches(Unsure.STR.toRegex()))
    }

    @Test
    fun `should match digits for Unsure NUM toRegex`() {
        assertTrue("123".matches(Unsure.NUM.toRegex()))
        assertFalse("abc".matches(Unsure.NUM.toRegex()))
    }

    @Test
    fun `should match true or false for Unsure BOOL toRegex`() {
        assertTrue("true".matches(Unsure.BOOL.toRegex()))
        assertTrue("false".matches(Unsure.BOOL.toRegex()))
        assertFalse("other".matches(Unsure.BOOL.toRegex()))
    }

    @Test
    fun `should produce case-insensitive regex from Unsure toRegex`() {
        val regex = Unsure.BOOL.toRegex(doCaseIgnore = true)
        assertTrue("TRUE".matches(regex))
        assertTrue("False".matches(regex))
    }

    // --- String.startsWith(StrVal) ---

    @Test
    fun `should return true when String starts with StrVal content`() {
        assertTrue("Hello World".startsWith(StrVal("Hello")))
    }

    @Test
    fun `should return false when String does not start with StrVal content`() {
        assertFalse("Hi World".startsWith(StrVal("Hello")))
    }

    @Test
    fun `should return true when String starts with empty StrVal`() {
        assertTrue("Hello".startsWith(StrVal("")))
    }

    @Test
    fun `should return false when empty String checked against non-empty StrVal`() {
        assertFalse("".startsWith(StrVal("Hello")))
    }

    // --- Number.isInLongRange ---

    @Test
    fun `should return true from isInLongRange for Long MAX_VALUE`() {
        assertTrue(Long.MAX_VALUE.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for Long MIN_VALUE`() {
        assertTrue(Long.MIN_VALUE.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for zero`() {
        assertTrue(0L.isInLongRange)
    }

    @Test
    fun `should return true from isInLongRange for Byte Short Int`() {
        assertTrue(Byte.MAX_VALUE.isInLongRange)
        assertTrue(Short.MAX_VALUE.isInLongRange)
        assertTrue(Int.MAX_VALUE.isInLongRange)
    }

    @Test
    fun `should return false from isInLongRange for value exceeding Long MAX`() {
        val bigValue = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)
        assertFalse(bigValue.isInLongRange)
    }

    // --- Number.isInIntRange ---

    @Test
    fun `should return true from isInIntRange for Int MAX_VALUE`() {
        assertTrue(Int.MAX_VALUE.isInIntRange)
    }

    @Test
    fun `should return true from isInIntRange for Int MIN_VALUE`() {
        assertTrue(Int.MIN_VALUE.isInIntRange)
    }

    @Test
    fun `should return true from isInIntRange for zero`() {
        assertTrue(0.isInIntRange)
    }

    @Test
    fun `should return false from isInIntRange for Long MAX_VALUE`() {
        assertFalse(Long.MAX_VALUE.isInIntRange)
    }

    // --- Number.isInShortRange ---

    @Test
    fun `should return true from isInShortRange for Short MAX_VALUE`() {
        assertTrue(Short.MAX_VALUE.isInShortRange)
    }

    @Test
    fun `should return true from isInShortRange for Short MIN_VALUE`() {
        assertTrue(Short.MIN_VALUE.isInShortRange)
    }

    @Test
    fun `should return true from isInShortRange for zero`() {
        assertTrue(0.isInShortRange)
    }

    @Test
    fun `should return false from isInShortRange for Int MAX_VALUE`() {
        assertFalse(Int.MAX_VALUE.isInShortRange)
    }

    // --- Number.isInByteRange ---

    @Test
    fun `should return true from isInByteRange for Byte MAX_VALUE`() {
        assertTrue(Byte.MAX_VALUE.isInByteRange)
    }

    @Test
    fun `should return true from isInByteRange for Byte MIN_VALUE`() {
        assertTrue(Byte.MIN_VALUE.isInByteRange)
    }

    @Test
    fun `should return true from isInByteRange for zero`() {
        assertTrue(0.isInByteRange)
    }

    @Test
    fun `should return false from isInByteRange for Short MAX_VALUE`() {
        assertFalse(Short.MAX_VALUE.isInByteRange)
    }
}
