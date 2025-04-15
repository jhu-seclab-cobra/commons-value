package cobra.commons.value.serializer

import kotlin.test.Test
import kotlin.test.assertEquals

class SerializerPerformanceTest {

    private val randomDataSet = List(10_0000) { randomIValue() }

    @Test
    fun testDftByteBufferSerializerImpl() = randomDataSet.forEach { value ->
        val serialized = DftByteBufferSerializerImpl.serialize(value)
        val deserialized = DftByteBufferSerializerImpl.deserialize(serialized)
        assertEquals(value, deserialized, "Serialization and deserialization of ${value::class} failed.")
    }

    @Test
    fun testDftByteArraySerializerImpl() = randomDataSet.forEach { value ->
        val serialized = DftByteArraySerializerImpl.serialize(value)
        val deserialized = DftByteArraySerializerImpl.deserialize(serialized)
        assertEquals(value, deserialized, "Serialization and deserialization of ${value::class} failed.")
    }

//    @Test
//    fun testDftCharBufferSerializerImpl() = randomDataSet.forEach { value ->
//        val serialized = DftCharBufferSerializerImpl.serialize(value)
//        val deserialized = DftCharBufferSerializerImpl.deserialize(serialized)
//        assertEquals(value, deserialized, "Serialization and deserialization of ${value::class} failed.")
//    }

}