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

/*
 * Parameterized round-trip tests for non-numeric primitive values across all [IValSerializer]
 * implementations, per the round-trip contract in design-serializer.md. Numeric boundaries live in
 * IValSerializerNumericTest; collections in IValSerializerCollectionTest.
 *
 * - `should round-trip NullVal` — NullVal survives serialization round-trip.
 * - `should round-trip StrVal with empty string` — Empty StrVal round-trips.
 * - `should round-trip StrVal with short string` — Short StrVal round-trips.
 * - `should round-trip StrVal with long string` — Long StrVal (10000 chars) round-trips.
 * - `should round-trip StrVal with unicode` — Unicode StrVal round-trips.
 * - `should round-trip BoolVal true` — BoolVal.T round-trips.
 * - `should round-trip BoolVal false` — BoolVal.F round-trips.
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
