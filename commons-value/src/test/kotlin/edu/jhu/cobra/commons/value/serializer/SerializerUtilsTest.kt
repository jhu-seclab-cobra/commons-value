package edu.jhu.cobra.commons.value.serializer

import java.io.DataInput
import java.io.EOFException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for SerializerUtils extension functions specified in design-utils.md.
 *
 * - `should parse valid integer string via asNumber` — "42" parses to 42.
 * - `should parse valid decimal string via asNumber` — "3.14" parses to a number.
 * - `should parse valid negative string via asNumber` — "-123" parses to -123.
 * - `should parse hex-prefixed string via asNumber` — "0xFF" parses to 255.
 * - `should throw NumberFormatException for invalid asNumber input` — Non-numeric string rejected.
 * - `should parse valid hex string via asHexInt` — "FF" parses to 255.
 * - `should parse lowercase hex string via asHexInt` — "ff" parses to 255.
 * - `should parse zero hex string via asHexInt` — "0" parses to 0.
 * - `should throw NumberFormatException for invalid asHexInt input` — "GG" rejected.
 * - `should convert int to hex string via asHexString` — 255 converts to "ff".
 * - `should convert zero to hex string via asHexString` — 0 converts to "0".
 * - `should return empty ByteArray when size is zero` — DataInput.asByteArray(0) returns empty.
 * - `should read exact bytes when size is positive` — DataInput.asByteArray(3) reads 3 bytes.
 * - `should read until EOF when size is negative` — DataInput.asByteArray(-1) reads all remaining.
 * - `should return empty sequence when available is zero` — DataInput.asByteSequence(0) yields nothing.
 * - `should yield exact bytes when available is positive` — DataInput.asByteSequence(3) yields 3 bytes.
 * - `should yield until EOF when available is negative` — DataInput.asByteSequence(-1) yields all remaining.
 */
internal class SerializerUtilsTest {

    // --- String.asNumber ---

    @Test
    fun `should parse valid integer string via asNumber`() {
        assertEquals(42, "42".asNumber().toInt())
    }

    @Test
    fun `should parse valid decimal string via asNumber`() {
        assertEquals(3.14f, "3.14".asNumber())
    }

    @Test
    fun `should parse valid negative string via asNumber`() {
        assertEquals(-123L, "-123".asNumber().toLong())
    }

    @Test
    fun `should parse hex-prefixed string via asNumber`() {
        assertEquals(0xFF, "0xFF".asNumber())
    }

    @Test
    fun `should throw NumberFormatException for invalid asNumber input`() {
        assertFailsWith<NumberFormatException> {
            "not a number".asNumber()
        }
    }

    // --- String.asHexInt ---

    @Test
    fun `should parse valid hex string via asHexInt`() {
        assertEquals(255, "FF".asHexInt())
    }

    @Test
    fun `should parse lowercase hex string via asHexInt`() {
        assertEquals(255, "ff".asHexInt())
    }

    @Test
    fun `should parse zero hex string via asHexInt`() {
        assertEquals(0, "0".asHexInt())
    }

    @Test
    fun `should throw NumberFormatException for invalid asHexInt input`() {
        assertFailsWith<NumberFormatException> {
            "GG".asHexInt()
        }
    }

    // --- Int.asHexString ---

    @Test
    fun `should convert int to hex string via asHexString`() {
        assertEquals("ff", 255.asHexString())
    }

    @Test
    fun `should convert zero to hex string via asHexString`() {
        assertEquals("0", 0.asHexString())
    }

    // --- DataInput.asByteArray ---

    @Test
    fun `should return empty ByteArray when size is zero`() {
        val dataInput = createDataInput(byteArrayOf(1, 2, 3))
        assertContentEquals(byteArrayOf(), dataInput.asByteArray(0))
    }

    @Test
    fun `should read exact bytes when size is positive`() {
        val dataInput = createDataInput(byteArrayOf(1, 2, 3, 4, 5))
        assertContentEquals(byteArrayOf(1, 2, 3), dataInput.asByteArray(3))
    }

    @Test
    fun `should read until EOF when size is negative`() {
        val dataInput = createDataInput(byteArrayOf(10, 20, 30))
        assertContentEquals(byteArrayOf(10, 20, 30), dataInput.asByteArray(-1))
    }

    // --- DataInput.asByteSequence ---

    @Test
    fun `should return empty sequence when available is zero`() {
        val dataInput = createDataInput(byteArrayOf(1, 2, 3))
        assertContentEquals(byteArrayOf(), dataInput.asByteSequence(0).toList().toByteArray())
    }

    @Test
    fun `should yield exact bytes when available is positive`() {
        val dataInput = createDataInput(byteArrayOf(1, 2, 3, 4, 5))
        assertContentEquals(byteArrayOf(1, 2, 3), dataInput.asByteSequence(3).toList().toByteArray())
    }

    @Test
    fun `should yield until EOF when available is negative`() {
        val dataInput = createDataInput(byteArrayOf(10, 20, 30))
        assertContentEquals(byteArrayOf(10, 20, 30), dataInput.asByteSequence(-1).toList().toByteArray())
    }

    // --- Helper ---

    private fun createDataInput(data: ByteArray): DataInput = object : DataInput {
        private var position = 0

        override fun readByte(): Byte {
            if (position >= data.size) throw EOFException()
            return data[position++]
        }

        override fun readFully(b: ByteArray) {
            for (i in b.indices) b[i] = readByte()
        }

        override fun readFully(b: ByteArray, off: Int, len: Int) {
            for (i in off until off + len) b[i] = readByte()
        }

        override fun skipBytes(n: Int): Int = throw UnsupportedOperationException()
        override fun readUnsignedByte(): Int = throw UnsupportedOperationException()
        override fun readUnsignedShort(): Int = throw UnsupportedOperationException()
        override fun readShort(): Short = throw UnsupportedOperationException()
        override fun readChar(): Char = throw UnsupportedOperationException()
        override fun readInt(): Int = throw UnsupportedOperationException()
        override fun readLong(): Long = throw UnsupportedOperationException()
        override fun readFloat(): Float = throw UnsupportedOperationException()
        override fun readDouble(): Double = throw UnsupportedOperationException()
        override fun readLine(): String = throw UnsupportedOperationException()
        override fun readUTF(): String = throw UnsupportedOperationException()
        override fun readBoolean(): Boolean = throw UnsupportedOperationException()
    }
}
