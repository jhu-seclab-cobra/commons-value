package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests for [DftByteArraySerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds ByteArray-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type tag` — Unknown byte tag rejected.
 * - `should throw IllegalArgumentException when deserializing empty ByteArray` — Empty input rejected.
 * - `should throw IllegalArgumentException when map key size is negative` — Negative length prefix rejected.
 * - `should throw IllegalArgumentException with context when list element size exceeds remaining` —
 *   Corrupt length prefix reported with context.
 * - `should throw IllegalArgumentException when BOOL payload byte is corrupt` — BOOL payload outside 0..1 rejected
 *   instead of decoding to false.
 * - `should throw IllegalArgumentException when range bound is not an IntVal` — Non-INT nested value inside RANGE rejected.
 * - `should throw ValFormatException when int payload is truncated` — Truncated INT payload raises
 *   ValFormatException instead of leaking BufferUnderflowException.
 * - `should throw ValFormatException when trailing bytes follow value` — Material continuing past the
 *   decoded top-level value rejected.
 * - `should throw ValFormatException when nested element does not consume its declared window` — Element
 *   size prefix larger than the element's encoding rejected instead of silently shifting the parse.
 * - `should throw ValFormatException when deserialized nesting exceeds limit` — Material nested one past
 *   MAX_NESTING_DEPTH rejected at deserialize.
 * - `should throw ValFormatException when STR payload contains invalid UTF-8` — Malformed byte sequence
 *   rejected instead of silently decoding to U+FFFD.
 * - `should throw ValFormatException when map key contains invalid UTF-8` — Malformed key bytes rejected.
 */
internal class DftByteArraySerializerImplTest : AbcSerializerImplUnitTest<ByteArray>() {
    override val testTarget: IValSerializer<ByteArray> get() = DftByteArraySerializerImpl

    @Test
    fun `should throw IllegalArgumentException when deserializing unknown type tag`() {
        val invalidBytes = byteArrayOf(99)
        assertFailsWith<IllegalArgumentException> {
            DftByteArraySerializerImpl.deserialize(invalidBytes)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when deserializing empty ByteArray`() {
        val emptyBytes = byteArrayOf()
        assertFailsWith<IllegalArgumentException> {
            DftByteArraySerializerImpl.deserialize(emptyBytes)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when map key size is negative`() {
        val corruptBytes = byteArrayOf(Type.MAP.byte, -1, -1, -1, -1)
        assertFailsWith<IllegalArgumentException> {
            DftByteArraySerializerImpl.deserialize(corruptBytes)
        }
    }

    @Test
    fun `should throw IllegalArgumentException with context when list element size exceeds remaining`() {
        val corruptBytes = byteArrayOf(Type.LIST.byte, 0, 0, 0, 100)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteArraySerializerImpl.deserialize(corruptBytes)
            }
        assertTrue("size" in exception.message.orEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException when BOOL payload byte is corrupt`() {
        val corruptBytes = byteArrayOf(Type.BOOL.byte, 7)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteArraySerializerImpl.deserialize(corruptBytes)
            }
        assertTrue("7" in exception.message.orEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException when range bound is not an IntVal`() {
        val corruptBytes = byteArrayOf(Type.RANGE.byte, 0, 0, 0, 1, Type.NULL.byte)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteArraySerializerImpl.deserialize(corruptBytes)
            }
        assertTrue("range start" in exception.message.orEmpty())
    }

    @Test
    fun `should throw ValFormatException when int payload is truncated`() {
        val truncatedBytes = byteArrayOf(Type.INT.byte, 0, 0)
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(truncatedBytes)
        }
    }

    @Test
    fun `should throw ValFormatException when trailing bytes follow value`() {
        val trailingBytes = byteArrayOf(Type.NULL.byte, Type.NULL.byte)
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(trailingBytes)
        }
    }

    @Test
    fun `should throw ValFormatException when nested element does not consume its declared window`() {
        // First element declares 2 bytes but its NULL encoding consumes 1; the leftover byte
        // shifts the parse yet still yields a well-formed second element.
        val corruptBytes = byteArrayOf(Type.LIST.byte, 0, 0, 0, 2, Type.NULL.byte, 0, 0, 0, 1, Type.NULL.byte)
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(corruptBytes)
        }
    }

    @Test
    fun `should throw ValFormatException when deserialized nesting exceeds limit`() {
        var material = byteArrayOf(Type.NULL.byte)
        repeat(MAX_NESTING_DEPTH + 1) {
            val sizePrefix = ByteBuffer.allocate(SIZE_PREFIX_BYTES).putInt(material.size).array()
            material = byteArrayOf(Type.LIST.byte) + sizePrefix + material
        }
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(material)
        }
    }

    @Test
    fun `should throw ValFormatException when STR payload contains invalid UTF-8`() {
        // 0xFF is never valid in UTF-8.
        val corruptBytes = byteArrayOf(Type.STR.byte, 0xFF.toByte())
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(corruptBytes)
        }
    }

    @Test
    fun `should throw ValFormatException when map key contains invalid UTF-8`() {
        val corruptBytes = byteArrayOf(Type.MAP.byte, 0, 0, 0, 1, 0xFF.toByte(), 0, 0, 0, 1, Type.NULL.byte)
        assertFailsWith<ValFormatException> {
            DftByteArraySerializerImpl.deserialize(corruptBytes)
        }
    }
}
