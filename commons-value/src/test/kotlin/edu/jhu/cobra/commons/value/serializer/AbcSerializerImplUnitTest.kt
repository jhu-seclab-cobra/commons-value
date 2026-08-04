package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Abstract base test for [IValSerializer] round-trip contract: deserialize(serialize(value)) == value.
 *
 * Subclasses provide a concrete [testTarget] for each serializer implementation.
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
 * - `should round-trip Unsure ANY` — Unsure.ANY round-trips.
 * - `should round-trip Unsure STR` — Unsure.STR round-trips.
 * - `should round-trip Unsure NUM` — Unsure.NUM round-trips.
 * - `should round-trip Unsure BOOL` — Unsure.BOOL round-trips.
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
 * - `should round-trip large random data set` — 10000 random IValues all round-trip.
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
 */
internal abstract class AbcSerializerImplUnitTest<M : Any> {
    abstract val testTarget: IValSerializer<M>

    private fun assertRoundTrip(value: IValue) {
        val serialized = testTarget.serialize(value)
        val deserialized = testTarget.deserialize(serialized)
        assertEquals(value, deserialized)
    }

    // --- NullVal ---

    @Test
    fun `should round-trip NullVal`() {
        assertRoundTrip(NullVal)
    }

    // --- StrVal ---

    @Test
    fun `should round-trip StrVal with empty string`() {
        assertRoundTrip(StrVal(""))
    }

    @Test
    fun `should round-trip StrVal with short string`() {
        assertRoundTrip(StrVal("hello"))
    }

    @Test
    fun `should round-trip StrVal with long string`() {
        assertRoundTrip(StrVal("a".repeat(10000)))
    }

    @Test
    fun `should round-trip StrVal with unicode`() {
        assertRoundTrip(StrVal("Unicode: \u4f60\u597d\u4e16\u754c"))
    }

    // --- BoolVal ---

    @Test
    fun `should round-trip BoolVal true`() {
        assertRoundTrip(BoolVal.T)
    }

    @Test
    fun `should round-trip BoolVal false`() {
        assertRoundTrip(BoolVal.F)
    }

    // --- IntVal (formerly NumVal integer subtypes) ---

    @Test
    fun `should round-trip IntVal from Byte range`() {
        assertRoundTrip(IntVal(Byte.MAX_VALUE.toLong()))
    }

    @Test
    fun `should round-trip IntVal from Short range`() {
        assertRoundTrip(IntVal(Short.MAX_VALUE.toLong()))
    }

    @Test
    fun `should round-trip IntVal from Int range`() {
        assertRoundTrip(IntVal(42L))
    }

    @Test
    fun `should round-trip IntVal from Long range`() {
        assertRoundTrip(IntVal(1234567890123456789L))
    }

    // --- FloatVal (formerly NumVal float subtypes) ---

    @Test
    fun `should round-trip FloatVal from Float range`() {
        assertRoundTrip(FloatVal(3.14f.toDouble()))
    }

    @Test
    fun `should round-trip FloatVal from Double min`() {
        assertRoundTrip(FloatVal(Double.MIN_VALUE))
    }

    // --- IntVal ---

    @Test
    fun `should round-trip IntVal zero`() {
        assertRoundTrip(IntVal(0L))
    }

    @Test
    fun `should round-trip IntVal positive`() {
        assertRoundTrip(IntVal(42L))
    }

    @Test
    fun `should round-trip IntVal negative`() {
        assertRoundTrip(IntVal(-1L))
    }

    @Test
    fun `should round-trip IntVal max`() {
        assertRoundTrip(IntVal(Long.MAX_VALUE))
    }

    @Test
    fun `should round-trip IntVal min`() {
        assertRoundTrip(IntVal(Long.MIN_VALUE))
    }

    // --- FloatVal ---

    @Test
    fun `should round-trip FloatVal zero`() {
        assertRoundTrip(FloatVal(0.0))
    }

    @Test
    fun `should round-trip FloatVal fractional`() {
        assertRoundTrip(FloatVal(3.14))
    }

    @Test
    fun `should round-trip FloatVal negative`() {
        assertRoundTrip(FloatVal(-1.5))
    }

    @Test
    fun `should round-trip FloatVal max`() {
        assertRoundTrip(FloatVal(Double.MAX_VALUE))
    }

    @Test
    fun `should round-trip FloatVal min`() {
        assertRoundTrip(FloatVal(Double.MIN_VALUE))
    }

    @Test
    fun `should round-trip FloatVal NaN`() {
        val original = FloatVal(Double.NaN)
        val serialized = testTarget.serialize(original)
        val deserialized = testTarget.deserialize(serialized) as FloatVal
        assertTrue(deserialized.core.isNaN())
    }

    @Test
    fun `should round-trip FloatVal positive infinity`() {
        assertRoundTrip(FloatVal(Double.POSITIVE_INFINITY))
    }

    @Test
    fun `should round-trip FloatVal negative infinity`() {
        assertRoundTrip(FloatVal(Double.NEGATIVE_INFINITY))
    }

    // --- Nested collections with IntVal/FloatVal ---

    @Test
    fun `should round-trip ListVal with IntVal and FloatVal`() {
        assertRoundTrip(ListVal(IntVal(42L), FloatVal(3.14), IntVal(-1L), FloatVal(0.0)))
    }

    @Test
    fun `should round-trip MapVal with IntVal and FloatVal values`() {
        assertRoundTrip(
            MapVal(
                "int" to IntVal(42L),
                "float" to FloatVal(3.14),
                "intMax" to IntVal(Long.MAX_VALUE),
                "floatNeg" to FloatVal(-1.5),
            ),
        )
    }

    // --- Unsure ---

    @Test
    fun `should round-trip Unsure ANY`() {
        assertRoundTrip(Unsure.ANY)
    }

    @Test
    fun `should round-trip Unsure STR`() {
        assertRoundTrip(Unsure.STR)
    }

    @Test
    fun `should round-trip Unsure NUM`() {
        assertRoundTrip(Unsure.NUM)
    }

    @Test
    fun `should round-trip Unsure BOOL`() {
        assertRoundTrip(Unsure.BOOL)
    }

    // --- ListVal ---

    @Test
    fun `should round-trip empty ListVal`() {
        assertRoundTrip(ListVal(emptyList()))
    }

    @Test
    fun `should round-trip ListVal with mixed types`() {
        assertRoundTrip(ListVal(StrVal("test"), BoolVal.T, IntVal(1L), NullVal))
    }

    @Test
    fun `should round-trip nested ListVal`() {
        val nested =
            ListVal(
                ListVal(IntVal(1L), IntVal(2L)),
                ListVal(StrVal("a"), StrVal("b")),
            )
        assertRoundTrip(nested)
    }

    // --- SetVal ---

    @Test
    fun `should round-trip empty SetVal`() {
        assertRoundTrip(SetVal(emptySet()))
    }

    @Test
    fun `should round-trip SetVal with mixed types`() {
        assertRoundTrip(SetVal(StrVal("test"), BoolVal.T, IntVal(1L)))
    }

    @Test
    fun `should round-trip nested SetVal`() {
        val nested =
            SetVal(
                SetVal(IntVal(1L), IntVal(2L)),
                SetVal(StrVal("a"), StrVal("b")),
            )
        assertRoundTrip(nested)
    }

    // --- MapVal ---

    @Test
    fun `should round-trip empty MapVal`() {
        assertRoundTrip(MapVal(emptyMap()))
    }

    @Test
    fun `should round-trip MapVal with mixed value types`() {
        assertRoundTrip(
            MapVal(
                "str" to StrVal("value"),
                "num" to IntVal(42L),
                "bool" to BoolVal.T,
            ),
        )
    }

    @Test
    fun `should round-trip nested MapVal`() {
        val nested =
            MapVal(
                "inner" to MapVal("key" to IntVal(1L)),
                "list" to ListVal(StrVal("a")),
            )
        assertRoundTrip(nested)
    }

    // --- RangeVal ---

    @Test
    fun `should round-trip RangeVal with positive bounds`() {
        assertRoundTrip(RangeVal(1, 10))
    }

    @Test
    fun `should round-trip RangeVal with negative bounds`() {
        assertRoundTrip(RangeVal(-100, 100))
    }

    // --- Mixed nested ---

    @Test
    fun `should round-trip mixed nested collection`() {
        val mixed =
            ListVal(
                SetVal(IntVal(1L), IntVal(2L)),
                MapVal("k" to StrVal("v")),
                RangeVal(0, 99),
                BoolVal.F,
                NullVal,
                Unsure.ANY,
            )
        assertRoundTrip(mixed)
    }

    // --- Large random data set ---

    @Test
    fun `should round-trip large random data set`() {
        val dataSet = List(100) { randomIValue() }
        dataSet.forEach { assertRoundTrip(it) }
    }
}
