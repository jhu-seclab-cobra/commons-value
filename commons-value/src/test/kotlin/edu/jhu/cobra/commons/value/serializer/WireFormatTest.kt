package edu.jhu.cobra.commons.value.serializer

import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the wire-format extension functions specified in design-utils.md.
 *
 * - `should parse valid hex string via asHexInt` — "FF" parses to 255.
 * - `should parse lowercase hex string via asHexInt` — "ff" parses to 255.
 * - `should parse zero hex string via asHexInt` — "0" parses to 0.
 * - `should throw NumberFormatException for invalid asHexInt input` — "GG" rejected.
 * - `should convert int to hex string via asHexString` — 255 converts to "ff".
 * - `should convert zero to hex string via asHexString` — 0 converts to "0".
 * - `should return remainder from getBuffer when delimiter absent after advanced position` — Delimiter-absent read
 *   returns the remaining characters instead of over-allocating from the buffer limit.
 * - `should return all remaining from getBuffer when delimiter absent at position zero` — Delimiter-absent read at
 *   position zero returns the whole content.
 */
internal class WireFormatTest {
    // --- String.asHexInt ---

    @Test
    fun `should parse valid hex string via asHexInt`() {
        assertEquals(255, "FF".asHexInt())
    }

    @Test
    fun `should parse lowercase hex string via asHexInt`() {
        assertEquals(255, "ff".asHexInt())
    }

    @Test
    fun `should parse zero hex string via asHexInt`() {
        assertEquals(0, "0".asHexInt())
    }

    @Test
    fun `should throw NumberFormatException for invalid asHexInt input`() {
        assertFailsWith<NumberFormatException> {
            "GG".asHexInt()
        }
    }

    // --- Int.asHexString ---

    @Test
    fun `should convert int to hex string via asHexString`() {
        assertEquals("ff", 255.asHexString())
    }

    @Test
    fun `should convert zero to hex string via asHexString`() {
        assertEquals("0", 0.asHexString())
    }

    // --- CharBuffer.getBuffer(until) ---

    @Test
    fun `should return remainder from getBuffer when delimiter absent after advanced position`() {
        val buffer = CharBuffer.wrap("abc:defgh")
        buffer.getBuffer(':')
        val rest = buffer.getBuffer('|')
        assertEquals("defgh", rest.toString())
    }

    @Test
    fun `should return all remaining from getBuffer when delimiter absent at position zero`() {
        val buffer = CharBuffer.wrap("defgh")
        val rest = buffer.getBuffer('|')
        assertEquals("defgh", rest.toString())
    }
}
