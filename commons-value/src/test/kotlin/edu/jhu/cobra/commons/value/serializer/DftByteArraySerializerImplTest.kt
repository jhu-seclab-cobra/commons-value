package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.*
import java.math.BigInteger
import kotlin.test.*

/**
 * Test class for [DftByteArraySerializerImpl] that verifies the serialization and deserialization
 * functionality for all supported value types.
 */
class DftByteArraySerializerImplTest {

    @Test
    fun `test null value serialization and deserialization`() {
        val nullVal = NullVal
        val bytes = DftByteArraySerializerImpl.serialize(nullVal)
        assertNotNull(bytes)
        assertEquals(1, bytes.size)
        assertEquals(Type.NULL.byte, bytes[0])

        val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
        assertEquals(nullVal, deserialized)
    }

    @Test
    fun `test string value serialization and deserialization`() {
        val testCases = listOf(
            "",
            "Hello, World!",
            "Special chars: !@#$%^&*()",
            "Unicode: 你好世界",
            "Mixed: ABC123你好!@#"
        )

        testCases.forEach { str ->
            val strVal = StrVal(str)
            val bytes = DftByteArraySerializerImpl.serialize(strVal)
            assertNotNull(bytes)
            assertTrue(bytes.size >= 1)
            assertEquals(Type.STR.byte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(strVal, deserialized)
        }
    }

    @Test
    fun `test boolean value serialization and deserialization`() {
        listOf(true, false).forEach { bool ->
            val boolVal = BoolVal(bool)
            val bytes = DftByteArraySerializerImpl.serialize(boolVal)
            assertNotNull(bytes)
            assertEquals(2, bytes.size)
            assertEquals(Type.BOOL.byte, bytes[0])
            assertEquals(if (bool) 1.toByte() else 0.toByte(), bytes[1])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(boolVal, deserialized)
        }
    }

    @Test
    fun `test unsure value serialization and deserialization`() {
        val testCases = mapOf(
            Unsure.ANY to Type.UNSURE_ANY.byte,
            Unsure.NUM to Type.UNSURE_NUM.byte,
            Unsure.STR to Type.UNSURE_STR.byte,
            Unsure.BOOL to Type.UNSURE_BOOL.byte
        )

        testCases.forEach { (unsure, expectedByte) ->
            val bytes = DftByteArraySerializerImpl.serialize(unsure)
            assertNotNull(bytes)
            assertEquals(1, bytes.size)
            assertEquals(expectedByte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(unsure, deserialized)
        }
    }

    @Test
    fun `test numeric value serialization and deserialization`() {
        // Test byte
        val byteVal = NumVal(42.toByte())
        val byteBytes = DftByteArraySerializerImpl.serialize(byteVal)
        assertEquals(Type.NUM_BYTE.byte, byteBytes[0])
        assertEquals(byteVal, DftByteArraySerializerImpl.deserialize(byteBytes))

        // Test short
        val shortVal = NumVal(12345.toShort())
        val shortBytes = DftByteArraySerializerImpl.serialize(shortVal)
        assertEquals(Type.NUM_SHORT.byte, shortBytes[0])
        assertEquals(shortVal, DftByteArraySerializerImpl.deserialize(shortBytes))

        // Test int
        val intVal = NumVal(1234567890)
        val intBytes = DftByteArraySerializerImpl.serialize(intVal)
        assertEquals(Type.NUM_INT.byte, intBytes[0])
        assertEquals(intVal, DftByteArraySerializerImpl.deserialize(intBytes))

        // Test long
        val longVal = NumVal(1234567890123456789L)
        val longBytes = DftByteArraySerializerImpl.serialize(longVal)
        assertEquals(Type.NUM_LONG.byte, longBytes[0])
        assertEquals(longVal, DftByteArraySerializerImpl.deserialize(longBytes))

        // Test float
        val floatVal = NumVal(3.14159f)
        val floatBytes = DftByteArraySerializerImpl.serialize(floatVal)
        assertEquals(Type.NUM_FLOAT.byte, floatBytes[0])
        assertEquals(floatVal, DftByteArraySerializerImpl.deserialize(floatBytes))

        // Test double
        val doubleVal = NumVal(3.14159265359)
        val doubleBytes = DftByteArraySerializerImpl.serialize(doubleVal)
        assertEquals(Type.NUM_DOUBLE.byte, doubleBytes[0])
        assertEquals(doubleVal, DftByteArraySerializerImpl.deserialize(doubleBytes))

        // Test BigInteger
        val bigIntVal = NumVal(BigInteger("123456789012345678901234567890"))
        val bigIntBytes = DftByteArraySerializerImpl.serialize(bigIntVal)
        assertEquals(Type.NUM_OTHERS.byte, bigIntBytes[0])
        assertEquals(bigIntVal, DftByteArraySerializerImpl.deserialize(bigIntBytes))
    }

    @Test
    fun `test range value serialization and deserialization`() {
        val ranges = listOf(
            RangeVal(NumVal(1), NumVal(10)),
            RangeVal(NumVal(-5), NumVal(5)),
            RangeVal(NumVal(0.5), NumVal(1.5))
        )

        ranges.forEach { range ->
            val bytes = DftByteArraySerializerImpl.serialize(range)
            assertNotNull(bytes)
            assertTrue(bytes.size > 1)
            assertEquals(Type.RANGE.byte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(range, deserialized)
        }
    }

    @Test
    fun `test list value serialization and deserialization`() {
        val emptyList = ListVal()
        val simpleList = ListVal(NumVal(1), NumVal(2), NumVal(3))
        val mixedList = ListVal(
            NumVal(1),
            StrVal("hello"),
            BoolVal(true),
            NullVal,
            ListVal(NumVal(4), NumVal(5))
        )

        listOf(emptyList, simpleList, mixedList).forEach { list ->
            val bytes = DftByteArraySerializerImpl.serialize(list)
            assertNotNull(bytes)
            assertTrue(bytes.isNotEmpty())
            assertEquals(Type.LIST.byte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(list, deserialized)
        }
    }

    @Test
    fun `test set value serialization and deserialization`() {
        val emptySet = SetVal()
        val simpleSet = SetVal(NumVal(1), NumVal(2), NumVal(3))
        val mixedSet = SetVal(
            NumVal(1),
            StrVal("hello"),
            BoolVal(true),
            NullVal,
            SetVal(NumVal(4), NumVal(5))
        )

        listOf(emptySet, simpleSet, mixedSet).forEach { set ->
            val bytes = DftByteArraySerializerImpl.serialize(set)
            assertNotNull(bytes)
            assertTrue(bytes.isNotEmpty())
            assertEquals(Type.SET.byte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(set, deserialized)
        }
    }

    @Test
    fun `test map value serialization and deserialization`() {
        val emptyMap = MapVal()
        val simpleMap = MapVal().apply {
            this["one"] = NumVal(1)
            this["two"] = NumVal(2)
        }
        val complexMap = MapVal().apply {
            this["number"] = NumVal(42)
            this["string"] = StrVal("hello")
            this["boolean"] = BoolVal(true)
            this["null"] = NullVal
            this["nested"] = MapVal().apply {
                this["x"] = NumVal(1)
                this["y"] = NumVal(2)
            }
        }

        listOf(emptyMap, simpleMap, complexMap).forEach { map ->
            val bytes = DftByteArraySerializerImpl.serialize(map)
            assertNotNull(bytes)
            assertTrue(bytes.isNotEmpty())
            assertEquals(Type.MAP.byte, bytes[0])

            val deserialized = DftByteArraySerializerImpl.deserialize(bytes)
            assertEquals(map, deserialized)
        }
    }

    @Test
    fun `test invalid type deserialization`() {
        val invalidBytes = byteArrayOf(99) // 使用未定义的类型字节

        assertFailsWith<IllegalArgumentException> {
            DftByteArraySerializerImpl.deserialize(invalidBytes)
        }
    }

    @Test
    fun `test empty array deserialization`() {
        val emptyBytes = byteArrayOf()

        assertFailsWith<IllegalArgumentException> {
            DftByteArraySerializerImpl.deserialize(emptyBytes)
        }
    }
} 