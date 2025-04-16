package edu.jhu.cobra.commons.value.serializer

import org.junit.Test
import java.io.DataInput
import java.io.EOFException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.CharBuffer
import kotlin.test.*

class SerializerUtilsTest {
    @Test
    fun testAsNumber() {
        assertEquals(42, "42".asNumber().toInt())
        assertEquals(3.14f, "3.14".asNumber())
        assertEquals(-123L, "-123".asNumber().toLong())
        assertEquals(0xFF, "0xFF".asNumber())
        assertFailsWith<NumberFormatException> { "not a number".asNumber() }
    }

    @Test
    fun testHexConversion() {
        assertEquals(255, "FF".asHexInt())
        assertEquals("ff", 255.asHexString())
        assertFailsWith<NumberFormatException> { "GG".asHexInt() }
    }

    @Test
    fun testByteBufferGetArray() {
        val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        assertContentEquals(byteArrayOf(1, 2, 3), buffer.getArray(3))
        assertContentEquals(byteArrayOf(4, 5), buffer.getArray(2))
        assertFailsWith<BufferUnderflowException> { buffer.getArray(1) }
    }

    @Test
    fun testByteBufferGetString() {
        // Test with explicit size
        val buffer1 = ByteBuffer.wrap("Hello".encodeToByteArray())
        assertEquals("Hello", buffer1.getString(5))

        // Test with size prefix
        val str = "Hello"
        val bytes = str.encodeToByteArray()
        val sizePrefix = ByteBuffer.allocate(4 + bytes.size)  // 4 bytes for size + string bytes
        sizePrefix.putInt(bytes.size)
        sizePrefix.put(bytes)
        sizePrefix.typedFlip()
        assertEquals(str, sizePrefix.getString())
    }

    @Test
    fun testByteBufferOf() {
        val buffer = byteBufferOf(1, 2, 3)
        assertEquals(3, buffer.limit())
        assertEquals(1, buffer.get())
        assertEquals(2, buffer.get())
        assertEquals(3, buffer.get())
    }

    @Test
    fun testPutType() {
        val buffer = ByteBuffer.allocate(2)
        buffer.put(Type.NULL)
        buffer.put(Type.STR)
        buffer.flip()
        assertEquals(Type.NULL.byte, buffer.get())
        assertEquals(Type.STR.byte, buffer.get())
    }

    @Test
    fun testAsCharBuffer() {
        val str = "Hello"
        val buffer = str.asCharBuffer()
        assertEquals(str.length, buffer.length)
        assertEquals(str, buffer.toString())
    }

    @Test
    fun testTypedFlip() {
        val byteBuffer = ByteBuffer.allocate(3)
        byteBuffer.put(1).put(2).put(3)
        val flipped = byteBuffer.typedFlip()
        assertEquals(0, flipped.position())
        assertEquals(3, flipped.limit())

        val charBuffer = CharBuffer.allocate(3)
        charBuffer.put('a').put('b').put('c')
        val flippedChar = charBuffer.typedFlip()
        assertEquals(0, flippedChar.position())
        assertEquals(3, flippedChar.limit())
    }

    @Test
    fun testTypedPosition() {
        val buffer = CharBuffer.allocate(3)
        buffer.put('a').put('b').put('c')
        buffer.typedPosition(1)
        assertEquals(1, buffer.position())
        assertFailsWith<IllegalArgumentException> { buffer.typedPosition(-1) }
        assertFailsWith<IllegalArgumentException> { buffer.typedPosition(4) }
    }

    @Test
    fun testRemoveUntil() {
        val buffer = "Hello,World".asCharBuffer()
        assertTrue(buffer.remove(','))
        assertEquals("World", buffer.toString())
        assertFalse(buffer.remove('x'))
    }

    @Test
    fun testGetBuffer() {
        // Test getBuffer(until: Char)
        val buffer1 = "Hello,World".asCharBuffer()
        assertEquals("Hello", buffer1.getBuffer(',').toString())
        assertEquals("World", buffer1.toString())

        // Test getBuffer(size: Int)
        val buffer2 = "Hello,World".asCharBuffer()
        assertEquals("Hello", buffer2.getBuffer(5).toString())
        assertEquals(",World", buffer2.toString())

        assertFailsWith<BufferUnderflowException> {
            "Hi".asCharBuffer().getBuffer(3)
        }
    }

    @Test
    fun testGetString() {
        val buffer = "Hello,World".asCharBuffer()
        assertEquals("Hello", buffer.getString(','))
        assertEquals("World", buffer.toString())
    }

    @Test
    fun testTypeEnumValues() {
        // Test all Type enum values have unique bytes and strings
        val bytes = Type.entries.map { it.byte }
        val strings = Type.entries.map { it.str }
        assertEquals(bytes.distinct().size, bytes.size)
        assertEquals(strings.distinct().size, strings.size)
    }

    @Test
    fun testDataInputAsByteArray() {
        val testData = byteArrayOf(1, 2, 3, 4, 5)
        val dataInput = object : DataInput {
            private var position = 0

            override fun readByte(): Byte {
                if (position >= testData.size) throw EOFException()
                return testData[position++]
            }

            // Other DataInput methods are not used in this test
            override fun readFully(b: ByteArray) = throw UnsupportedOperationException()
            override fun readFully(b: ByteArray, off: Int, len: Int) = throw UnsupportedOperationException()
            override fun skipBytes(n: Int) = throw UnsupportedOperationException()
            override fun readUnsignedByte() = throw UnsupportedOperationException()
            override fun readUnsignedShort() = throw UnsupportedOperationException()
            override fun readShort() = throw UnsupportedOperationException()
            override fun readChar() = throw UnsupportedOperationException()
            override fun readInt() = throw UnsupportedOperationException()
            override fun readLong() = throw UnsupportedOperationException()
            override fun readFloat() = throw UnsupportedOperationException()
            override fun readDouble() = throw UnsupportedOperationException()
            override fun readLine() = throw UnsupportedOperationException()
            override fun readUTF() = throw UnsupportedOperationException()
            override fun readBoolean() = throw UnsupportedOperationException()
        }

        // Test reading specific size
        assertContentEquals(byteArrayOf(1, 2, 3), dataInput.asByteArray(3))

        // Test reading zero bytes
        assertContentEquals(byteArrayOf(), dataInput.asByteArray(0))

        // Test reading until EOF
        assertContentEquals(byteArrayOf(4, 5), dataInput.asByteArray(-1))

        // Test EOF exception
        assertFailsWith<EOFException> { dataInput.asByteArray(1) }
    }

    @Test
    fun testDataInputAsByteSequence() {
        val testData = byteArrayOf(1, 2, 3, 4, 5)
        val dataInput = object : DataInput {
            private var position = 0

            override fun readByte(): Byte {
                if (position >= testData.size) throw EOFException()
                return testData[position++]
            }

            // Other DataInput methods are not used in this test
            override fun readFully(b: ByteArray) = throw UnsupportedOperationException()
            override fun readFully(b: ByteArray, off: Int, len: Int) = throw UnsupportedOperationException()
            override fun skipBytes(n: Int) = throw UnsupportedOperationException()
            override fun readUnsignedByte() = throw UnsupportedOperationException()
            override fun readUnsignedShort() = throw UnsupportedOperationException()
            override fun readShort() = throw UnsupportedOperationException()
            override fun readChar() = throw UnsupportedOperationException()
            override fun readInt() = throw UnsupportedOperationException()
            override fun readLong() = throw UnsupportedOperationException()
            override fun readFloat() = throw UnsupportedOperationException()
            override fun readDouble() = throw UnsupportedOperationException()
            override fun readLine() = throw UnsupportedOperationException()
            override fun readUTF() = throw UnsupportedOperationException()
            override fun readBoolean() = throw UnsupportedOperationException()
        }

        // Test reading specific size
        assertContentEquals(byteArrayOf(1, 2, 3), dataInput.asByteSequence(3).toList().toByteArray())

        // Test reading zero bytes
        assertContentEquals(byteArrayOf(), dataInput.asByteSequence(0).toList().toByteArray())

        // Test reading with EOF
        assertFailsWith<EOFException> {
            dataInput.asByteSequence(-1).toList()
        }

        // Test reading more than available
        assertFailsWith<EOFException> {
            dataInput.asByteSequence(10).toList()
        }
    }
} 