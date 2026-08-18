package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.MAX_NESTING_DEPTH
import edu.jhu.cobra.commons.value.RangeVal
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * ByteBuffer-specific error tests for [DftByteBufferSerializerImpl].
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
 * - `should throw ValFormatException when deserialized nesting exceeds limit` — Material nested one past
 *   MAX_NESTING_DEPTH rejected at deserialize.
 * - `should throw ValFormatException when STR payload contains invalid UTF-8` — Malformed byte sequence
 *   rejected instead of silently decoding to U+FFFD.
 * - `should round-trip RangeVal with Long MAX_VALUE bounds` — Long bounds survive serialization without truncation.
 */
internal class DftByteBufferSerializerImplTest {

    @Test
    fun `should throw IllegalArgumentException when deserializing unknown type tag`() {
        val invalidBuffer = ByteBuffer.allocate(1).put(99.toByte()).flip()
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
                .flip()
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
                .flip()
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
                .flip()
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
                .flip()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                DftByteBufferSerializerImpl.deserialize(corruptBuffer)
            }
        assertTrue("range start" in exception.message.orEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException when buffer is fully consumed`() {
        val consumedBuffer = ByteBuffer.allocate(1).put(Type.NULL.byte).flip()
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
                .flip()
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
                .flip()
        assertFailsWith<ValFormatException> {
            DftByteBufferSerializerImpl.deserialize(trailingBuffer)
        }
    }

    @Test
    fun `should throw ValFormatException when deserialized nesting exceeds limit`() {
        val levels = MAX_NESTING_DEPTH + 1
        val material = ByteBuffer.allocate(levels * (TYPE_TAG_BYTES + SIZE_PREFIX_BYTES) + TYPE_TAG_BYTES)
        repeat(levels) { material.put(Type.LIST.byte).putInt(1) }
        material.put(Type.NULL.byte).flip()
        assertFailsWith<ValFormatException> {
            DftByteBufferSerializerImpl.deserialize(material)
        }
    }

    @Test
    fun `should throw ValFormatException when STR payload contains invalid UTF-8`() {
        // 0xFF is never valid in UTF-8.
        val corruptBuffer =
            ByteBuffer
                .allocate(6)
                .put(Type.STR.byte)
                .putInt(1)
                .put(0xFF.toByte())
                .flip()
        assertFailsWith<ValFormatException> {
            DftByteBufferSerializerImpl.deserialize(corruptBuffer)
        }
    }

    @Test
    fun `should round-trip RangeVal with Long MAX_VALUE bounds`() {
        val range = RangeVal(IntVal(Long.MAX_VALUE), IntVal(Long.MAX_VALUE))
        val serialized = DftByteBufferSerializerImpl.serialize(range)
        val restored = DftByteBufferSerializerImpl.deserialize(serialized) as RangeVal
        assertEquals(
            Long.MAX_VALUE,
            restored.first,
            "RangeVal Long bounds should survive ByteBuffer round-trip without truncation",
        )
    }
}
