package edu.jhu.cobra.commons.value.serializer

import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests for [DftCharBufferSerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds CharBuffer-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type label` — Unknown type string rejected.
 * - `should throw IllegalArgumentException when deserializing empty CharBuffer` — Empty buffer rejected.
 * - `should throw IllegalArgumentException when string length exceeds remaining chars` — Corrupt length prefix rejected.
 * - `should throw IllegalArgumentException with context when string length is negative` — Negative length prefix reported with context.
 */
internal class DftCharBufferSerializerImplTest : AbcSerializerImplUnitTest<CharBuffer>() {

    override val testTarget: IValSerializer<CharBuffer> get() = DftCharBufferSerializerImpl

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
    fun `should throw IllegalArgumentException with context when string length is negative`() {
        val corruptBuffer = "Str:-1:".asCharBuffer()
        val exception = assertFailsWith<IllegalArgumentException> {
            DftCharBufferSerializerImpl.deserialize(corruptBuffer)
        }
        assertTrue("size" in exception.message.orEmpty())
    }
}
