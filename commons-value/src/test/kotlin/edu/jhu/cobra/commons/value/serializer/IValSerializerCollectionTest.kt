package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MAX_NESTING_DEPTH
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Parameterized round-trip tests for collection and structural values across all [IValSerializer]
 * implementations: [DftByteArraySerializerImpl], [DftByteBufferSerializerImpl],
 * [DftCharBufferSerializerImpl]. Each test runs once per serializer via @MethodSource.
 *
 * - `should round-trip empty ListVal` — Empty ListVal round-trips.
 * - `should round-trip ListVal with mixed types` — ListVal containing mixed value types round-trips.
 * - `should round-trip nested ListVal` — ListVal containing nested ListVal round-trips.
 * - `should round-trip empty SetVal` — Empty SetVal round-trips.
 * - `should round-trip SetVal with mixed types` — SetVal containing mixed value types round-trips.
 * - `should round-trip nested SetVal` — SetVal containing nested SetVal round-trips.
 * - `should round-trip empty MapVal` — Empty MapVal round-trips.
 * - `should round-trip MapVal with mixed value types` — MapVal with mixed value types round-trips.
 * - `should round-trip nested MapVal` — MapVal containing nested MapVal round-trips.
 * - `should round-trip RangeVal with positive bounds` — RangeVal(1, 10) round-trips.
 * - `should round-trip RangeVal with negative bounds` — RangeVal(-100, 100) round-trips.
 * - `should round-trip mixed nested collection` — ListVal containing SetVal, MapVal, RangeVal round-trips.
 * - `should round-trip large random data set` — 100 random IValues all round-trip.
 * - `should round-trip list at max nesting depth` — List nested MAX_NESTING_DEPTH levels round-trips.
 * - `should throw IllegalArgumentException when serialized nesting exceeds limit` — List nested one past
 *   MAX_NESTING_DEPTH rejected at serialize.
 * - `should throw IllegalArgumentException when serializing cyclic value graph` — Self-referencing list
 *   rejected at serialize instead of overflowing the stack.
 * - `should round-trip StrVal with surrogate pair` — Well-formed surrogate pair round-trips.
 * - `should throw IllegalArgumentException when serializing unpaired surrogate in string` — Lone high
 *   surrogate in StrVal rejected at serialize instead of corrupting silently.
 * - `should throw IllegalArgumentException when serializing unpaired surrogate in map key` — Lone high
 *   surrogate in a map key rejected at serialize instead of corrupting silently.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IValSerializerCollectionTest {
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

    // --- ListVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip empty ListVal`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, ListVal(emptyList()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip ListVal with mixed types`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, ListVal(StrVal("test"), BoolVal.T, IntVal(1L), NullVal))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip nested ListVal`(serializer: IValSerializer<*>) {
        val nested =
            ListVal(
                ListVal(IntVal(1L), IntVal(2L)),
                ListVal(StrVal("a"), StrVal("b")),
            )
        assertRoundTrip(serializer, nested)
    }

    // --- SetVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip empty SetVal`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, SetVal(emptySet()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip SetVal with mixed types`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, SetVal(StrVal("test"), BoolVal.T, IntVal(1L)))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip nested SetVal`(serializer: IValSerializer<*>) {
        val nested =
            SetVal(
                SetVal(IntVal(1L), IntVal(2L)),
                SetVal(StrVal("a"), StrVal("b")),
            )
        assertRoundTrip(serializer, nested)
    }

    // --- MapVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip empty MapVal`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, MapVal(emptyMap()))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip MapVal with mixed value types`(serializer: IValSerializer<*>) {
        assertRoundTrip(
            serializer,
            MapVal(
                "str" to StrVal("value"),
                "num" to IntVal(42L),
                "bool" to BoolVal.T,
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip nested MapVal`(serializer: IValSerializer<*>) {
        val nested =
            MapVal(
                "inner" to MapVal("key" to IntVal(1L)),
                "list" to ListVal(StrVal("a")),
            )
        assertRoundTrip(serializer, nested)
    }

    // --- RangeVal ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip RangeVal with positive bounds`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, RangeVal(1, 10))
    }

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip RangeVal with negative bounds`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, RangeVal(-100, 100))
    }

    // --- Mixed nested ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip mixed nested collection`(serializer: IValSerializer<*>) {
        val mixed =
            ListVal(
                SetVal(IntVal(1L), IntVal(2L)),
                MapVal("k" to StrVal("v")),
                RangeVal(0, 99),
                BoolVal.F,
                NullVal,
                Unsure.ANY,
            )
        assertRoundTrip(serializer, mixed)
    }

    // --- Large random data set ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip large random data set`(serializer: IValSerializer<*>) {
        val dataSet = List(100) { randomIValue() }
        dataSet.forEach { assertRoundTrip(serializer, it) }
    }

    // --- Nesting depth ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip list at max nesting depth`(serializer: IValSerializer<*>) {
        var value: IValue = NullVal
        repeat(MAX_NESTING_DEPTH) { value = ListVal(value) }
        assertRoundTrip(serializer, value)
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("serializers")
    fun `should throw IllegalArgumentException when serialized nesting exceeds limit`(serializer: IValSerializer<*>) {
        val s = serializer as IValSerializer<Any>
        var value: IValue = NullVal
        repeat(MAX_NESTING_DEPTH + 1) { value = ListVal(value) }
        assertFailsWith<IllegalArgumentException> {
            s.serialize(value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("serializers")
    fun `should throw IllegalArgumentException when serializing cyclic value graph`(serializer: IValSerializer<*>) {
        val s = serializer as IValSerializer<Any>
        val cyclic = ListVal()
        cyclic.add(cyclic)
        assertFailsWith<IllegalArgumentException> {
            s.serialize(cyclic)
        }
    }

    // --- UTF-16 well-formedness ---

    @ParameterizedTest
    @MethodSource("serializers")
    fun `should round-trip StrVal with surrogate pair`(serializer: IValSerializer<*>) {
        assertRoundTrip(serializer, StrVal("emoji: 😀"))
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("serializers")
    fun `should throw IllegalArgumentException when serializing unpaired surrogate in string`(serializer: IValSerializer<*>) {
        val s = serializer as IValSerializer<Any>
        assertFailsWith<IllegalArgumentException> {
            s.serialize(StrVal("broken: \uD800"))
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("serializers")
    fun `should throw IllegalArgumentException when serializing unpaired surrogate in map key`(serializer: IValSerializer<*>) {
        val s = serializer as IValSerializer<Any>
        assertFailsWith<IllegalArgumentException> {
            s.serialize(MapVal("broken: \uD800" to NullVal))
        }
    }
}
