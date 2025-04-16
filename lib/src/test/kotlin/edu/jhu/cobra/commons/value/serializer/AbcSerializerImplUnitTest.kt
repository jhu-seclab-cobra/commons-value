package edu.jhu.cobra.commons.value.serializer

import cobra.commons.value.*
import java.nio.ByteBuffer
import java.nio.CharBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbcSerializerImplUnitTest<M : Any> {

    abstract val testTarget: IValSerializer<M>

    inline fun <reified T : IValue> testSerialization(value: T) {
        val serialized = testTarget.serialize(value)
        val deserialized = testTarget.deserialize(serialized)
        assertEquals(value, deserialized, "Serialization and deserialization of ${T::class} failed.")
    }

    companion object {
        val nullVal = NullVal
        val strValEmpty = StrVal("")
        val strValShort = StrVal("test")
        val strValLong = StrVal("a".repeat(10000))
        val boolValTrue = BoolVal(true)
        val boolValFalse = BoolVal(false)
        val numValByte = NumVal(Byte.MAX_VALUE)
        val numValShort = NumVal(Short.MAX_VALUE)
        val numValInt = NumVal(31231)
        val numValFloatMax = NumVal(Float.MAX_VALUE)
        val numValDoubleMin = NumVal(Double.MIN_VALUE)
        val numValFloatNaN = NumVal(Float.NaN)
        val numValFloatPosInf = NumVal(Float.POSITIVE_INFINITY)
        val numValFloatNegInf = NumVal(Float.NEGATIVE_INFINITY)
        val unsureNum = Unsure.NUM
        val unsureStr = Unsure.STR
        val unsureAny = Unsure.ANY
        val listValEmpty = ListVal(emptyList())
        val listValMixed = ListVal(listOf(StrVal("test"), BoolVal(true), NumVal(1)))
        val listValNested = ListVal(List(1_000) { ListVal(List(100) { NumVal(it) }) })
        val setValEmpty = SetVal(emptySet())
        val setValMixed = SetVal(setOf(StrVal("test"), BoolVal(true), NumVal(1)))
        val setValNested = SetVal(List(1_000) { SetVal(List(100) { NumVal(it) }) })
        val mapValEmpty = MapVal(emptyMap())
        val mapValMixed = MapVal(mapOf("key1" to StrVal("value1"), "key2" to NumVal(2)))
        val mapValLarge = MapVal((1..1_000).associate { "key$it" to NumVal(it) })
        val rangeValSmall = RangeVal(1, 10)
        val rangeValLarge = RangeVal(-31231, 31231)
    }

    @Test
    fun testNullValSerialization() {
        testSerialization(nullVal)
    }

    @Test
    fun testStrValSerialization() {
        testSerialization(strValEmpty)
        testSerialization(strValShort)
        testSerialization(strValLong)
    }

    @Test
    fun testBoolValSerialization() {
        testSerialization(boolValTrue)
        testSerialization(boolValFalse)
    }

    @Test
    fun testNumValSerialization() {
        testSerialization(numValByte)
        testSerialization(numValShort)
        testSerialization(numValInt)
        testSerialization(numValFloatMax)
        testSerialization(numValDoubleMin)
        testSerialization(numValFloatNaN)
        testSerialization(numValFloatPosInf)
        testSerialization(numValFloatNegInf)
    }

    @Test
    fun testUnsureSerialization() {
        testSerialization(unsureNum)
        testSerialization(unsureStr)
        testSerialization(unsureAny)
    }

    @Test
    fun testListValSerialization() {
        testSerialization(listValEmpty)
        testSerialization(listValMixed)
        testSerialization(listValNested)
    }

    @Test
    fun testSetValSerialization() {
        testSerialization(setValEmpty)
        testSerialization(setValMixed)
        testSerialization(setValNested)
    }

    @Test
    fun testMapValSerialization() {
        testSerialization(mapValEmpty)
        testSerialization(mapValMixed)
        testSerialization(mapValLarge)
    }

    @Test
    fun testRangeValSerialization() {
        testSerialization(rangeValSmall)
        testSerialization(rangeValLarge)
    }

    @Test
    fun testLargeRandomDataSet() {
        val randomDataSet = List(10_000) { randomIValue() }
        randomDataSet.forEach { testSerialization(it) }
    }
}

class DftByteBufferSerializerImplTest : AbcSerializerImplUnitTest<ByteBuffer>() {

    override val testTarget get() = DftByteBufferSerializerImpl
}

class DftByteArraySerializerImplTest : AbcSerializerImplUnitTest<ByteArray>() {

    override val testTarget get() = DftByteArraySerializerImpl
}


class DftCharBufferSerializerImplTest : AbcSerializerImplUnitTest<CharBuffer>() {

    override val testTarget get() = DftCharBufferSerializerImpl
}