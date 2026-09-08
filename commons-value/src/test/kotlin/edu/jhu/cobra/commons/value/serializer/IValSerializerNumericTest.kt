package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/*
 * Parameterized round-trip tests for IntVal and FloatVal boundaries across all [IValSerializer]
 * implementations, per the 8-byte Long and Double payload contract in design-serializer.md.
 * Every value is crossed with every serializer via @MethodSource.
 *
 * - `should round-trip IntVal boundary` — Zero, sign boundaries, Byte/Short/Int/Long extremes round-trip.
 * - `should round-trip FloatVal boundary` — Zero, fractional, negative, Float-precision, Double extremes,
 *   and both infinities round-trip.
 * - `should round-trip FloatVal NaN` — FloatVal(Double.NaN) round-trips preserving NaN.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IValSerializerNumericTest {
    companion object {
        private val serializers = listOf(DftByteArraySerializerImpl, DftByteBufferSerializerImpl, DftCharBufferSerializerImpl)

        private val intBoundaries =
            listOf(
                0L,
                1L,
                -1L,
                42L,
                Byte.MAX_VALUE.toLong(),
                Byte.MIN_VALUE.toLong(),
                Short.MAX_VALUE.toLong(),
                Short.MIN_VALUE.toLong(),
                Int.MAX_VALUE.toLong(),
                Int.MIN_VALUE.toLong(),
                1234567890123456789L,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
            )

        private val floatBoundaries =
            listOf(
                0.0,
                -0.0,
                3.14,
                -1.5,
                3.14f.toDouble(),
                Double.MIN_VALUE,
                Double.MAX_VALUE,
                -Double.MAX_VALUE,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
            )

        private fun cross(values: List<IValue>): Stream<Arguments> =
            serializers.stream().flatMap { serializer -> values.stream().map { Arguments.of(serializer, it) } }

        @JvmStatic
        fun serializers(): Stream<Arguments> = serializers.stream().map { Arguments.of(it) }

        @JvmStatic
        fun intCases(): Stream<Arguments> = cross(intBoundaries.map(::IntVal))

        @JvmStatic
        fun floatCases(): Stream<Arguments> = cross(floatBoundaries.map(::FloatVal))
    }

    @Suppress("UNCHECKED_CAST")
    private fun roundTrip(
        serializer: IValSerializer<*>,
        value: IValue,
    ): IValue {
        val s = serializer as IValSerializer<Any>
        return s.deserialize(s.serialize(value))
    }

    @ParameterizedTest
    @MethodSource("intCases")
    fun `should round-trip IntVal boundary`(
        serializer: IValSerializer<*>,
        value: IntVal,
    ) {
        assertEquals(value, roundTrip(serializer, value))
    }

    @ParameterizedTest
    @MethodSource("floatCases")
    fun `should round-trip FloatVal boundary`(
        serializer: IValSerializer<*>,
        value: FloatVal,
    ) {
        assertEquals(value, roundTrip(serializer, value))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal NaN`(serializer: IValSerializer<*>) {
        val deserialized = roundTrip(serializer, FloatVal(Double.NaN)) as FloatVal
        assertTrue(deserialized.core.isNaN())
    }
}
