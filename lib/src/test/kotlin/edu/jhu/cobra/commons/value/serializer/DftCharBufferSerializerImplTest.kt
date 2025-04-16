package edu.jhu.cobra.commons.value.serializer

import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DftCharBufferSerializerImplTest {
    @Test
    fun `test invalid type deserialization`() {
        val invalidBuffer = "Unknown:value:".asCharBuffer()

        assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(invalidBuffer)
        }
    }

    @Test
    fun `test empty buffer deserialization`() {
        val emptyBuffer = CharBuffer.allocate(0)

        assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(emptyBuffer)
        }
    }

    @Test
    fun `test malformed buffer deserialization`() {
        val malformedBuffer = "Str:invalid_hex:value:".asCharBuffer()

        assertFailsWith<NumberFormatException> {
            DftCharBufferSerializerImpl.deserialize(malformedBuffer)
        }
    }
} 