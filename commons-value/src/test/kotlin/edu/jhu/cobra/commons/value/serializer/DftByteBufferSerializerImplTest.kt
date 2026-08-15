package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests for [DftByteBufferSerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds ByteBuffer-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type tag` — Unknown byte tag rejected.
 * - `should throw IllegalArgumentException when deserializing empty ByteBuffer` — Empty buffer rejected.
 * - `should throw IllegalArgumentException when string length exceeds remaining bytes` — Corrupt length prefix rejected.
 * - `should throw IllegalArgumentException when string length is negative` — Negative length prefix rejected.
 * - `should throw IllegalArgumentException with context when list count is negative` — Negative element count reported with context.
 * - `should throw IllegalArgumentException when range bound is not an IntVal` — Non-INT nested value inside RANGE rejected.
 * - `should throw IllegalArgumentException when buffer is fully consumed` — Re-reading an exhausted buffer rejected.
 * - `should throw ValFormatException when long payload is truncated` — Truncated INT payload raises
 *   ValFormatException instead of leaking BufferUnderflowException.
 * - `should throw ValFormatException when trailing bytes follow value` — Material continuing past the
 *   decoded top-level value rejected.
 */
internal class DftByteBufferSerializerImplTest : AbcSerializerImplUnitTest<ByteBuffer>() {
    override val testTarget: IValSerializer<ByteBuffer> get() = DftByteBufferSerializerImpl

    @Test
    fun `should throw IllegalArgumentException when deserializing unknown type tag`() {
        val invalidBuffer = ByteBuffer.allocate(1).put(99.toByte()).typedFlip()
        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(invalidBuffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when deserializing empty ByteBuffer`() {
        val emptyBuffer = ByteBuffer.allocate(0)
        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(emptyBuffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when string length exceeds remaining bytes`() {
        val buffer =
            ByteBuffer
                .allocate(6)
                .put(Type.STR.byte)
                .putInt(100)
                .put('a'.code.toByte())
                .typedFlip()
        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(buffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when string length is negative`() {
        val buffer =
            ByteBuffer
                .allocate(5)
                .put(Type.STR.byte)
                .putInt(-1)
                .typedFlip()
        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(buffer)
        }
    }

    @Test
    fun `should throw IllegalArgumentException with context when list count is negative`() {
        val buffer =
            ByteBuffer
                .allocate(5)
                .put(Type.LIST.byte)
                .putInt(-1)
                .typedFlip()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteBufferSerializerImpl.deserialize(buffer)
            }
        assertTrue("count" in exception.message.orEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException when range bound is not an IntVal`() {
        val corruptBuffer =
            ByteBuffer
                .allocate(2)
                .put(Type.RANGE.byte)
                .put(Type.NULL.byte)
                .typedFlip()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteBufferSerializerImpl.deserialize(corruptBuffer)
            }
        assertTrue("range start" in exception.message.orEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException when buffer is fully consumed`() {
        val consumedBuffer = ByteBuffer.allocate(1).put(Type.NULL.byte).typedFlip()
        DftByteBufferSerializerImpl.deserialize(consumedBuffer)
        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(consumedBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when long payload is truncated`() {
        val truncatedBuffer =
            ByteBuffer
                .allocate(3)
                .put(Type.INT.byte)
                .putShort(0)
                .typedFlip()
        assertFailsWith<ValFormatException> {
            DftByteBufferSerializerImpl.deserialize(truncatedBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when trailing bytes follow value`() {
        val trailingBuffer =
            ByteBuffer
                .allocate(2)
                .put(Type.NULL.byte)
                .put(Type.NULL.byte)
                .typedFlip()
        assertFailsWith<ValFormatException> {
            DftByteBufferSerializerImpl.deserialize(trailingBuffer)
        }
    }
}
