package edu.jhu.cobra.commons.value

import java.io.File
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
 * - `should round-trip every single byte through ByteArray strVal` — the 256 one-byte sequences
 * - `should map each byte of a malformed sequence to one unit` — C3, E2 82, F0 9F 98, ED A0 80, C0 AF,
 *   F5 80 80 80, FF FE
 * - `should decode well-formed UTF-8 to characters` — U+FFFD, U+20AC and U+1F600 stay characters
 * - `should decode mixed bytes to characters and units` — valid sequences and units in one array
 * - `should round-trip mixed bytes through ByteArray strVal` — bytes.strVal.toByteArray() equals bytes
 * - `should give the String constructor value for bytes` — bytes.strVal is canonical: every two-byte array and seeded
 *   random arrays of three to six bytes
 * - `should wrap Char as StrVal` — Char.strVal
 * - `should wrap Path as StrVal` — Path.strVal
 * - `should wrap File as StrVal` — File.strVal
 * - `should wrap true as BoolVal T` — Boolean.boolVal true
 * - `should wrap false as BoolVal F` — Boolean.boolVal false
 * - `should convert null to NullVal via primitiveVal` — Any?.primitiveVal null
 * - `should convert Number to IntVal via primitiveVal` — Any?.primitiveVal Number (Int dispatches to IntVal)
 * - `should convert String to StrVal via primitiveVal` — Any?.primitiveVal String
 * - `should convert Boolean to BoolVal via primitiveVal` — Any?.primitiveVal Boolean
 * - `should convert Char to StrVal via primitiveVal` — Any?.primitiveVal Char
 * - `should return IPrimitiveVal unchanged via primitiveVal` — Any?.primitiveVal identity
 * - `should throw IllegalArgumentException for unsupported primitiveVal type` — primitiveVal error
 * - `should convert Int to IntVal via primitiveVal` — primitiveVal Int dispatches to intVal
 * - `should convert Long to IntVal via primitiveVal` — primitiveVal Long dispatches to intVal
 * - `should convert Double to FloatVal via primitiveVal` — primitiveVal Double dispatches to floatVal
 * - `should convert Float to FloatVal via primitiveVal` — primitiveVal Float dispatches to floatVal
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

    // --- ByteArray.strVal ---

    @Test
    fun `should round-trip every single byte through ByteArray strVal`() {
        for (byte in 0..255) {
            val bytes = byteArrayOf(byte.toByte())
            val value = bytes.strVal
            assertContentEquals(bytes, value.toByteArray(), "byte $byte")
            assertEquals(value, value.toByteArray().strVal, "byte $byte")
            val expected = if (byte < 0x80) byte.toChar() else (0xDC00 + byte).toChar()
            assertEquals(expected.toString(), value.core, "byte $byte")
        }
    }

    @Test
    fun `should map each byte of a malformed sequence to one unit`() {
        val malformed =
            listOf(
                bytesOf(0xC3),
                bytesOf(0xE2, 0x82),
                bytesOf(0xF0, 0x9F, 0x98),
                bytesOf(0xED, 0xA0, 0x80),
                bytesOf(0xC0, 0xAF),
                bytesOf(0xF5, 0x80, 0x80, 0x80),
                bytesOf(0xFF, 0xFE),
            )
        for (bytes in malformed) {
            val units = bytes.map { (0xDC00 + (it.toInt() and 0xFF)).toChar() }.joinToString("")
            assertEquals(units, bytes.strVal.core)
            assertContentEquals(bytes, bytes.strVal.toByteArray())
        }
    }

    @Test
    fun `should decode well-formed UTF-8 to characters`() {
        assertEquals("�", bytesOf(0xEF, 0xBF, 0xBD).strVal.core)
        assertEquals("€", bytesOf(0xE2, 0x82, 0xAC).strVal.core)
        assertEquals("😀", bytesOf(0xF0, 0x9F, 0x98, 0x80).strVal.core)
    }

    @Test
    fun `should decode mixed bytes to characters and units`() {
        val bytes = bytesOf(0x61, 0xC3, 0xA9, 0xFF, 0x62, 0xE2, 0x82, 0xAC, 0xE2, 0x82)
        assertEquals("aé\uDCFFb€\uDCE2\uDC82", bytes.strVal.core)
    }

    @Test
    fun `should round-trip mixed bytes through ByteArray strVal`() {
        val bytes = bytesOf(0xF4, 0x8F, 0xBF, 0xBF, 0xF4, 0x90, 0x80, 0x80, 0xE0, 0x9F, 0xBF, 0xE0, 0xA0, 0x80, 0xC2)
        assertEquals("􏿿\uDCF4\uDC90\uDC80\uDC80\uDCE0\uDC9F\uDCBFࠀ\uDCC2", bytes.strVal.core)
        assertContentEquals(bytes, bytes.strVal.toByteArray())
    }

    // ByteArray.strVal decodes once and skips the canonical scan of the String constructor; every two-byte array and
    // seeded random arrays of three to six bytes, drawn from ASCII, lead and continuation bytes, give that constructor's
    // value.
    @Test
    fun `should give the String constructor value for bytes`() {
        val pool = intArrayOf(0x61, 0x80, 0x9F, 0xA9, 0xBF, 0xC2, 0xC3, 0xE0, 0xE2, 0xED, 0xF0, 0xF4, 0xFF)
        val random = kotlin.random.Random(SEED)
        val arrays =
            (0 until 0x10000).map { bytesOf(it shr 8, it and 0xFF) } +
                List(RANDOM_ARRAYS) { ByteArray(3 + random.nextInt(4)) { pool[random.nextInt(pool.size)].toByte() } }
        for (bytes in arrays) {
            val value = bytes.strVal
            assertEquals(StrVal(value.core).core, value.core, bytes.joinToString(" ") { "%02X".format(it) })
            assertContentEquals(bytes, value.toByteArray())
        }
    }

    private fun bytesOf(vararg values: Int): ByteArray = ByteArray(values.size) { values[it].toByte() }

    private companion object {
        const val SEED = 7
        const val RANDOM_ARRAYS = 20_000
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
    fun `should convert Char to StrVal via primitiveVal`() {
        assertEquals(StrVal("x"), 'x'.primitiveVal)
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
