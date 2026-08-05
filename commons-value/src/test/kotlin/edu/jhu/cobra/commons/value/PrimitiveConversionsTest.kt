package edu.jhu.cobra.commons.value

import java.io.File
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Black-box tests for the primitive conversion extension functions derived from the design doc.
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
 * - `should convert Int to IntVal via primitiveVal` — primitiveVal Int dispatches to intVal
 * - `should convert Long to IntVal via primitiveVal` — primitiveVal Long dispatches to intVal
 * - `should convert Double to FloatVal via primitiveVal` — primitiveVal Double dispatches to floatVal
 * - `should convert Float to FloatVal via primitiveVal` — primitiveVal Float dispatches to floatVal
 * - `should compare StrVal lexicographically` — compareTo StrVal
 * - `should compare StrVal equal` — compareTo StrVal ==
 * - `should compare BoolVal F less than T` — compareTo BoolVal
 * - `should compare BoolVal T greater than F` — compareTo BoolVal
 * - `should compare BoolVal equal` — compareTo BoolVal ==
 * - `should compare NullVal equal to NullVal` — compareTo NullVal ==
 * - `should throw IllegalArgumentException for cross-type compareTo` — compareTo error
 * - `should compare IntVal less than IntVal` — compareTo IntVal <
 * - `should compare IntVal greater than IntVal` — compareTo IntVal >
 * - `should compare IntVal equal to IntVal` — compareTo IntVal ==
 * - `should compare FloatVal less than FloatVal` — compareTo FloatVal <
 * - `should compare IntVal less than FloatVal cross-type` — compareTo IntVal vs FloatVal
 * - `should compare FloatVal greater than IntVal cross-type` — compareTo FloatVal vs IntVal
 * - `should return true when String starts with StrVal content` — String.startsWith match
 * - `should return false when String does not start with StrVal content` — String.startsWith mismatch
 * - `should return true when String starts with empty StrVal` — String.startsWith boundary
 * - `should return false when empty String checked against non-empty StrVal` — String.startsWith boundary
 */
internal class PrimitiveConversionsTest {
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
}
