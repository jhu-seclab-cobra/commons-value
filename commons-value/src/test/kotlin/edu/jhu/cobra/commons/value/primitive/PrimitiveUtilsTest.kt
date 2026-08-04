package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import edu.jhu.cobra.commons.value.boolVal
import edu.jhu.cobra.commons.value.compareTo
import edu.jhu.cobra.commons.value.floatVal
import edu.jhu.cobra.commons.value.intVal
import edu.jhu.cobra.commons.value.isInByteRange
import edu.jhu.cobra.commons.value.isInIntRange
import edu.jhu.cobra.commons.value.isInLongRange
import edu.jhu.cobra.commons.value.isInShortRange
import edu.jhu.cobra.commons.value.primitiveVal
import edu.jhu.cobra.commons.value.startsWith
import edu.jhu.cobra.commons.value.strVal
import edu.jhu.cobra.commons.value.toRegex
import java.io.File
import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Black-box tests for PrimitiveUtils extension functions derived from the design doc.
 *
 * - `should wrap String as StrVal` — String.strVal
 * - `should wrap empty String as StrVal` — String.strVal boundary
 * - `should wrap Char as StrVal` — Char.strVal
 * - `should wrap Path as StrVal` — Path.strVal
 * - `should wrap File as StrVal` — File.strVal
 * - `should wrap true as BoolVal T` — Boolean.boolVal true
 * - `should wrap false as BoolVal F` — Boolean.boolVal false
 * - `should convert null to NullVal via primitiveVal` — Any?.primitiveVal null
 * - `should convert Number to IntVal via primitiveVal` — Any?.primitiveVal Number (Int dispatches to IntVal)
 * - `should convert String to StrVal via primitiveVal` — Any?.primitiveVal String
 * - `should convert Boolean to BoolVal via primitiveVal` — Any?.primitiveVal Boolean
 * - `should return IPrimitiveVal unchanged via primitiveVal` — Any?.primitiveVal identity
 * - `should throw IllegalArgumentException for unsupported primitiveVal type` — primitiveVal error
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
 * - `should wrap Long 0 as IntVal` — Long.intVal zero
 * - `should wrap Long 42 as IntVal` — Long.intVal typical
 * - `should wrap Long MAX_VALUE as IntVal` — Long.intVal boundary max
 * - `should wrap Long MIN_VALUE as IntVal` — Long.intVal boundary min
 * - `should wrap Int as IntVal with Long core` — Int.intVal widens to Long
 * - `should wrap Int MAX_VALUE as IntVal` — Int.intVal boundary max
 * - `should wrap Int MIN_VALUE as IntVal` — Int.intVal boundary min
 * - `should wrap Short as IntVal with Long core` — Short.intVal widens to Long
 * - `should wrap Byte as IntVal with Long core` — Byte.intVal widens to Long
 * - `should parse integer string to IntVal` — String.intVal typical
 * - `should parse negative string to IntVal` — String.intVal negative
 * - `should parse zero string to IntVal` — String.intVal zero
 * - `should parse Long MAX_VALUE string to IntVal` — String.intVal boundary
 * - `should throw NumberFormatException for non-integer string intVal` — String.intVal error
 * - `should throw NumberFormatException for decimal string intVal` — String.intVal error decimal
 * - `should throw NumberFormatException for empty string intVal` — String.intVal error empty
 * - `should throw NumberFormatException for blank string intVal` — String.intVal error blank
 * - `should include offending input in intVal NumberFormatException message` — String.intVal error message
 * - `should wrap Double 0 as FloatVal` — Double.floatVal zero
 * - `should wrap Double 3_14 as FloatVal` — Double.floatVal typical
 * - `should wrap Double MAX_VALUE as FloatVal` — Double.floatVal boundary max
 * - `should wrap Float as FloatVal with Double core` — Float.floatVal widens to Double
 * - `should parse decimal string to FloatVal` — String.floatVal typical
 * - `should parse negative string to FloatVal` — String.floatVal negative
 * - `should parse zero string to FloatVal` — String.floatVal zero
 * - `should throw NumberFormatException for non-numeric string floatVal` — String.floatVal error
 * - `should throw NumberFormatException for empty string floatVal` — String.floatVal error empty
 * - `should throw NumberFormatException for blank string floatVal` — String.floatVal error blank
 * - `should include offending input in floatVal NumberFormatException message` — String.floatVal error message
 * - `should convert Int to IntVal via primitiveVal` — primitiveVal Int dispatches to intVal
 * - `should convert Long to IntVal via primitiveVal` — primitiveVal Long dispatches to intVal
 * - `should convert Double to FloatVal via primitiveVal` — primitiveVal Double dispatches to floatVal
 * - `should convert Float to FloatVal via primitiveVal` — primitiveVal Float dispatches to floatVal
 * - `should compare IntVal less than IntVal` — compareTo IntVal <
 * - `should compare IntVal greater than IntVal` — compareTo IntVal >
 * - `should compare IntVal equal to IntVal` — compareTo IntVal ==
 * - `should compare FloatVal less than FloatVal` — compareTo FloatVal <
 * - `should compare IntVal less than FloatVal cross-type` — compareTo IntVal vs FloatVal
 * - `should compare FloatVal greater than IntVal cross-type` — compareTo FloatVal vs IntVal
 */
internal class PrimitiveUtilsTest {
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
    fun `should convert Number to IntVal via primitiveVal`() {
        assertEquals(IntVal(42L), 42.primitiveVal)
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
        val original = IntVal(42L)
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
            IntVal(1L).compareTo(StrVal("1"))
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

    // --- Long.intVal ---

    @Test
    fun `should wrap Long 0 as IntVal`() {
        assertEquals(IntVal(0L), 0L.intVal)
    }

    @Test
    fun `should wrap Long 42 as IntVal`() {
        assertEquals(IntVal(42L), 42L.intVal)
    }

    @Test
    fun `should wrap Long MAX_VALUE as IntVal`() {
        assertEquals(IntVal(Long.MAX_VALUE), Long.MAX_VALUE.intVal)
    }

    @Test
    fun `should wrap Long MIN_VALUE as IntVal`() {
        assertEquals(IntVal(Long.MIN_VALUE), Long.MIN_VALUE.intVal)
    }

    // --- Int.intVal ---

    @Test
    fun `should wrap Int as IntVal with Long core`() {
        val result = 42.intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    @Test
    fun `should wrap Int MAX_VALUE as IntVal`() {
        val result = Int.MAX_VALUE.intVal
        assertEquals(Int.MAX_VALUE.toLong(), result.core)
    }

    @Test
    fun `should wrap Int MIN_VALUE as IntVal`() {
        val result = Int.MIN_VALUE.intVal
        assertEquals(Int.MIN_VALUE.toLong(), result.core)
    }

    // --- Short.intVal ---

    @Test
    fun `should wrap Short as IntVal with Long core`() {
        val result = 42.toShort().intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    // --- Byte.intVal ---

    @Test
    fun `should wrap Byte as IntVal with Long core`() {
        val result = 42.toByte().intVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    // --- String.intVal ---

    @Test
    fun `should parse integer string to IntVal`() {
        assertEquals(IntVal(42L), "42".intVal)
    }

    @Test
    fun `should parse negative string to IntVal`() {
        assertEquals(IntVal(-1L), "-1".intVal)
    }

    @Test
    fun `should parse zero string to IntVal`() {
        assertEquals(IntVal(0L), "0".intVal)
    }

    @Test
    fun `should parse Long MAX_VALUE string to IntVal`() {
        assertEquals(IntVal(Long.MAX_VALUE), Long.MAX_VALUE.toString().intVal)
    }

    @Test
    fun `should throw NumberFormatException for non-integer string intVal`() {
        assertFailsWith<NumberFormatException> { "abc".intVal }
    }

    @Test
    fun `should throw NumberFormatException for decimal string intVal`() {
        assertFailsWith<NumberFormatException> { "3.14".intVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string intVal`() {
        assertFailsWith<NumberFormatException> { "".intVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string intVal`() {
        assertFailsWith<NumberFormatException> { " ".intVal }
    }

    @Test
    fun `should include offending input in intVal NumberFormatException message`() {
        val exception = assertFailsWith<NumberFormatException> { "abc".intVal }
        assertTrue(exception.message.orEmpty().contains("abc"))
    }

    // --- Double.floatVal ---

    @Test
    fun `should wrap Double 0 as FloatVal`() {
        assertEquals(FloatVal(0.0), 0.0.floatVal)
    }

    @Test
    fun `should wrap Double 3_14 as FloatVal`() {
        assertEquals(FloatVal(3.14), 3.14.floatVal)
    }

    @Test
    fun `should wrap Double MAX_VALUE as FloatVal`() {
        assertEquals(FloatVal(Double.MAX_VALUE), Double.MAX_VALUE.floatVal)
    }

    // --- Float.floatVal ---

    @Test
    fun `should wrap Float as FloatVal with Double core`() {
        val result = 3.14f.floatVal
        assertIs<FloatVal>(result)
        assertEquals(3.14f.toDouble(), result.core)
    }

    // --- String.floatVal ---

    @Test
    fun `should parse decimal string to FloatVal`() {
        assertEquals(FloatVal(3.14), "3.14".floatVal)
    }

    @Test
    fun `should parse negative string to FloatVal`() {
        assertEquals(FloatVal(-0.5), "-0.5".floatVal)
    }

    @Test
    fun `should parse zero string to FloatVal`() {
        assertEquals(FloatVal(0.0), "0.0".floatVal)
    }

    @Test
    fun `should throw NumberFormatException for non-numeric string floatVal`() {
        assertFailsWith<NumberFormatException> { "abc".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for empty string floatVal`() {
        assertFailsWith<NumberFormatException> { "".floatVal }
    }

    @Test
    fun `should throw NumberFormatException for blank string floatVal`() {
        assertFailsWith<NumberFormatException> { " ".floatVal }
    }

    @Test
    fun `should include offending input in floatVal NumberFormatException message`() {
        val exception = assertFailsWith<NumberFormatException> { "not-a-float".floatVal }
        assertTrue(exception.message.orEmpty().contains("not-a-float"))
    }

    // --- primitiveVal with IntVal/FloatVal ---

    @Test
    fun `should convert Int to IntVal via primitiveVal`() {
        val result = 42.primitiveVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    @Test
    fun `should convert Long to IntVal via primitiveVal`() {
        val result = 42L.primitiveVal
        assertIs<IntVal>(result)
        assertEquals(42L, result.core)
    }

    @Test
    fun `should convert Double to FloatVal via primitiveVal`() {
        val result = 3.14.primitiveVal
        assertIs<FloatVal>(result)
        assertEquals(3.14, result.core)
    }

    @Test
    fun `should convert Float to FloatVal via primitiveVal`() {
        val result = 3.14f.primitiveVal
        assertIs<FloatVal>(result)
        assertEquals(3.14f.toDouble(), result.core)
    }

    // --- compareTo with IntVal/FloatVal ---

    @Test
    fun `should compare IntVal less than IntVal`() {
        assertTrue(IntVal(1L).compareTo(IntVal(2L)) < 0)
    }

    @Test
    fun `should compare IntVal greater than IntVal`() {
        assertTrue(IntVal(2L).compareTo(IntVal(1L)) > 0)
    }

    @Test
    fun `should compare IntVal equal to IntVal`() {
        assertEquals(0, IntVal(1L).compareTo(IntVal(1L)))
    }

    @Test
    fun `should compare FloatVal less than FloatVal`() {
        assertTrue(FloatVal(1.0).compareTo(FloatVal(2.0)) < 0)
    }

    @Test
    fun `should compare IntVal less than FloatVal cross-type`() {
        assertTrue(IntVal(1L).compareTo(FloatVal(1.5)) < 0)
    }

    @Test
    fun `should compare FloatVal greater than IntVal cross-type`() {
        assertTrue(FloatVal(1.5).compareTo(IntVal(1L)) > 0)
    }
}
