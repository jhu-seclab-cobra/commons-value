package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.MAX_NESTING_DEPTH
import edu.jhu.cobra.commons.value.StrVal
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * CharBuffer-specific error tests for [DftCharBufferSerializerImpl].
 *
 * - `should throw IllegalArgumentException when deserializing unknown type label` — Unknown type string rejected.
 * - `should throw IllegalArgumentException when deserializing empty CharBuffer` — Empty buffer rejected.
 * - `should throw IllegalArgumentException when string length exceeds remaining chars` — Corrupt length prefix rejected.
 * - `should throw ValFormatException when string length prefix is negative` — "-1" is not unsigned
 *   hexadecimal; rejected at the prefix parse.
 * - `should throw IllegalArgumentException when map key is not a StrVal` — Non-Str key inside MAP rejected.
 * - `should throw ValFormatException when int payload is not numeric` — Unparsable IntV payload raises
 *   ValFormatException instead of leaking NumberFormatException.
 * - `should throw ValFormatException when container count is not hexadecimal` — Unparsable List count raises
 *   ValFormatException instead of leaking NumberFormatException.
 * - `should throw ValFormatException when trailing chars follow value` — Material continuing past the
 *   decoded top-level value rejected.
 * - `should throw ValFormatException when deserialized nesting exceeds limit` — Material nested one past
 *   MAX_NESTING_DEPTH rejected at deserialize.
 * - `should throw ValFormatException when map key delimiter is not equals sign` — Wrong char between
 *   key and value rejected instead of being consumed silently.
 * - `should throw ValFormatException when list element delimiter is invalid` — Wrong char between
 *   elements rejected instead of being consumed silently.
 * - `should decode StrB payload to its units` — `StrB:2:ff61` decodes to the unit U+DCFF and `a`.
 * - `should throw ValFormatException when string material is not the one material of its value` — A `StrB`
 *   payload that is well-formed UTF-8 or empty, holds uppercase or non-hex digits, or is shorter than its
 *   byte count; a `StrB` byte count spelled other than `Int.asHexString()` emits it (`01`, `+1`, `A`); a `Str`
 *   payload holding an unpaired surrogate; a map key in the `StrB` frame; a map key in the `Str` frame holding a
 *   unit.
 */
internal class DftCharBufferSerializerImplTest {
    @Test
    fun `should throw IllegalArgumentException when deserializing unknown type label`() {
        val invalidBuffer = "Unknown:value:".asCharBuffer()
        assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(invalidBuffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when deserializing empty CharBuffer`() {
        val emptyBuffer = CharBuffer.allocate(0)
        assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(emptyBuffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when string length exceeds remaining chars`() {
        val corruptBuffer = "Str:64:ab".asCharBuffer()
        assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when string length prefix is negative`() {
        val corruptBuffer = "Str:-1:".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when map key is not a StrVal`() {
        val corruptBuffer = "Map:1:IntV:5:=Null::".asCharBuffer()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftCharBufferSerializerImpl.deserialize(corruptBuffer)
            }
        assertTrue("map key" in exception.message.orEmpty())
    }

    @Test
    fun `should throw ValFormatException when int payload is not numeric`() {
        val corruptBuffer = "IntV:abc:".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when container count is not hexadecimal`() {
        val corruptBuffer = "List:zz:".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when trailing chars follow value`() {
        val trailingBuffer = "Null:Null:".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(trailingBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when deserialized nesting exceeds limit`() {
        val levels = MAX_NESTING_DEPTH + 1
        val material = ("List:1:".repeat(levels) + "Null:" + ":".repeat(levels)).asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(material)
        }
    }

    @Test
    fun `should throw ValFormatException when map key delimiter is not equals sign`() {
        // Valid form: Map:1:Str:1:a=Null::
        val corruptBuffer = "Map:1:Str:1:a#Null::".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when list element delimiter is invalid`() {
        // Valid form: List:2:Null:,Null::
        val corruptBuffer = "List:2:Null:;Null::".asCharBuffer()
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should decode StrB payload to its units`() {
        assertEquals(StrVal("\uDCFFa"), DftCharBufferSerializerImpl.deserialize("StrB:2:ff61".asCharBuffer()))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "StrB:2:c3a9",
            "StrB:0:",
            "StrB:2:FF61",
            "StrB:1:zz",
            "StrB:1:+f",
            "StrB:3:ff61",
            "StrB:01:ff",
            "StrB:+1:ff",
            "StrB:A:ffffffffffffffffffff",
            "Str:1:\uDCFF",
            "Str:1:\uD800",
            "Map:1:StrB:1:ff=Null::",
            "Map:1:Str:1:\uDCFF=Null::",
        ],
    )
    fun `should throw ValFormatException when string material is not the one material of its value`(material: String) {
        assertFailsWith<ValFormatException> {
            DftCharBufferSerializerImpl.deserialize(material.asCharBuffer())
        }
    }
}
