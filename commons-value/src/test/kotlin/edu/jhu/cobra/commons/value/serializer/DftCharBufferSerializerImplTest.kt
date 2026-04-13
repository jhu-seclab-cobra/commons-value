package edu.jhu.cobra.commons.value.serializer

import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Tests for [DftCharBufferSerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds CharBuffer-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type label` — Unknown type string rejected.
 * - `should throw IllegalArgumentException when deserializing empty CharBuffer` — Empty buffer rejected.
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
}
