package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DftByteBufferSerializerImplTest {
    @Test
    fun `test invalid type deserialization`() {
        val invalidBuffer = ByteBuffer.allocate(1).put(99.toByte()).typedFlip()

        assertFailsWith<IllegalArgumentException> {
            DftByteBufferSerializerImpl.deserialize(invalidBuffer)
        }
    }

    @Test
    fun `test empty buffer deserialization`() {
        val emptyBuffer = ByteBuffer.allocate(0)

        assertFailsWith<IllegalArgumentException>("Empty byte buffer") {
            DftByteBufferSerializerImpl.deserialize(emptyBuffer)
        }
    }
} 