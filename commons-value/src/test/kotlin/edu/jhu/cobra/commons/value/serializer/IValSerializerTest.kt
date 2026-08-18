package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Parameterized round-trip tests for primitive values across all [IValSerializer] implementations:
 * [DftByteArraySerializerImpl], [DftByteBufferSerializerImpl], [DftCharBufferSerializerImpl].
 * Each test runs once per serializer via @MethodSource.
 *
 * - `should round-trip NullVal` — NullVal survives serialization round-trip.
 * - `should round-trip StrVal with empty string` — Empty StrVal round-trips.
 * - `should round-trip StrVal with short string` — Short StrVal round-trips.
 * - `should round-trip StrVal with long string` — Long StrVal (10000 chars) round-trips.
 * - `should round-trip StrVal with unicode` — Unicode StrVal round-trips.
 * - `should round-trip BoolVal true` — BoolVal.T round-trips.
 * - `should round-trip BoolVal false` — BoolVal.F round-trips.
 * - `should round-trip IntVal from Byte range` — IntVal covering Byte.MAX_VALUE round-trips.
 * - `should round-trip IntVal from Short range` — IntVal covering Short.MAX_VALUE round-trips.
 * - `should round-trip IntVal from Int range` — IntVal(42L) round-trips.
 * - `should round-trip IntVal from Long range` — IntVal wrapping large Long round-trips.
 * - `should round-trip FloatVal from Float range` — FloatVal wrapping Float-precision value round-trips.
 * - `should round-trip FloatVal from Double min` — FloatVal(Double.MIN_VALUE) round-trips.
 * - `should round-trip IntVal zero` — IntVal(0L) round-trips.
 * - `should round-trip IntVal positive` — IntVal(42L) round-trips.
 * - `should round-trip IntVal negative` — IntVal(-1L) round-trips.
 * - `should round-trip IntVal max` — IntVal(Long.MAX_VALUE) round-trips.
 * - `should round-trip IntVal min` — IntVal(Long.MIN_VALUE) round-trips.
 * - `should round-trip FloatVal zero` — FloatVal(0.0) round-trips.
 * - `should round-trip FloatVal fractional` — FloatVal(3.14) round-trips.
 * - `should round-trip FloatVal negative` — FloatVal(-1.5) round-trips.
 * - `should round-trip FloatVal max` — FloatVal(Double.MAX_VALUE) round-trips.
 * - `should round-trip FloatVal min` — FloatVal(Double.MIN_VALUE) round-trips.
 * - `should round-trip FloatVal NaN` — FloatVal(Double.NaN) round-trips preserving NaN.
 * - `should round-trip FloatVal positive infinity` — FloatVal(Double.POSITIVE_INFINITY) round-trips.
 * - `should round-trip FloatVal negative infinity` — FloatVal(Double.NEGATIVE_INFINITY) round-trips.
 * - `should round-trip ListVal with IntVal and FloatVal` — ListVal containing IntVal and FloatVal round-trips.
 * - `should round-trip MapVal with IntVal and FloatVal values` — MapVal with IntVal and FloatVal values round-trips.
 * - `should round-trip Unsure ANY` — Unsure.ANY round-trips.
 * - `should round-trip Unsure STR` — Unsure.STR round-trips.
 * - `should round-trip Unsure NUM` — Unsure.NUM round-trips.
 * - `should round-trip Unsure BOOL` — Unsure.BOOL round-trips.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IValSerializerTest {
    companion object {
        @JvmStatic
        fun serializers(): Stream<Arguments> =
            Stream.of(
                Arguments.of(DftByteArraySerializerImpl),
                Arguments.of(DftByteBufferSerializerImpl),
                Arguments.of(DftCharBufferSerializerImpl),
            )
    }

    @Suppress("UNCHECKED_CAST")
    private fun assertRoundTrip(
        serializer: IValSerializer<*>,
        value: IValue,
    ) {
        val s = serializer as IValSerializer<Any>
        val serialized = s.serialize(value)
        val deserialized = s.deserialize(serialized)
        assertEquals(value, deserialized)
    }

    // --- NullVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip NullVal`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, NullVal)
    }

    // --- StrVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip StrVal with empty string`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, StrVal(""))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip StrVal with short string`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, StrVal("hello"))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip StrVal with long string`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, StrVal("a".repeat(10000)))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip StrVal with unicode`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, StrVal("Unicode: 你好世界"))
    }

    // --- BoolVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip BoolVal true`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, BoolVal.T)
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip BoolVal false`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, BoolVal.F)
    }

    // --- IntVal (formerly NumVal integer subtypes) ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal from Byte range`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(Byte.MAX_VALUE.toLong()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal from Short range`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(Short.MAX_VALUE.toLong()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal from Int range`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(42L))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal from Long range`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(1234567890123456789L))
    }

    // --- FloatVal (formerly NumVal float subtypes) ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal from Float range`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(3.14f.toDouble()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal from Double min`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(Double.MIN_VALUE))
    }

    // --- IntVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal zero`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(0L))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal positive`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(42L))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal negative`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(-1L))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal max`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(Long.MAX_VALUE))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip IntVal min`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, IntVal(Long.MIN_VALUE))
    }

    // --- FloatVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal zero`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(0.0))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal fractional`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(3.14))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal negative`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(-1.5))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal max`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(Double.MAX_VALUE))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal min`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(Double.MIN_VALUE))
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal NaN`(serializer: IValSerializer<*>) {
        val s = serializer as IValSerializer<Any>
        val original = FloatVal(Double.NaN)
        val serialized = s.serialize(original)
        val deserialized = s.deserialize(serialized) as FloatVal
        assertTrue(deserialized.core.isNaN())
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal positive infinity`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(Double.POSITIVE_INFINITY))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip FloatVal negative infinity`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, FloatVal(Double.NEGATIVE_INFINITY))
    }

    // --- Nested collections with IntVal/FloatVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip ListVal with IntVal and FloatVal`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, ListVal(IntVal(42L), FloatVal(3.14), IntVal(-1L), FloatVal(0.0)))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip MapVal with IntVal and FloatVal values`(serializer: IValSerializer<*>) {
        assertRoundTrip(
            serializer,
            MapVal(
                "int" to IntVal(42L),
                "float" to FloatVal(3.14),
                "intMax" to IntVal(Long.MAX_VALUE),
                "floatNeg" to FloatVal(-1.5),
            ),
        )
    }

    // --- Unsure ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip Unsure ANY`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, Unsure.ANY)
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip Unsure STR`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, Unsure.STR)
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip Unsure NUM`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, Unsure.NUM)
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip Unsure BOOL`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, Unsure.BOOL)
    }
}
