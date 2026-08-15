package edu.jhu.cobra.commons.value.serializer

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
}
