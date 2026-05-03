package edu.jhu.cobra.commons.value.serializer

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Tests for [DftByteBufferSerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds ByteBuffer-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type tag` — Unknown byte tag rejected.
 * - `should throw IllegalArgumentException when deserializing empty ByteBuffer` — Empty buffer rejected.
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
}
