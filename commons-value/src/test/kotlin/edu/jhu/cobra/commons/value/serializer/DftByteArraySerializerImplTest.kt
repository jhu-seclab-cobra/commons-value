package edu.jhu.cobra.commons.value.serializer

import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Tests for [DftByteArraySerializerImpl]. Extends [AbcSerializerImplUnitTest] for round-trip
 * contract coverage and adds ByteArray-specific error cases.
 *
 * - `should throw IllegalArgumentException when deserializing unknown type tag` — Unknown byte tag rejected.
 * - `should throw IllegalArgumentException when deserializing empty ByteArray` — Empty input rejected.
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
}
