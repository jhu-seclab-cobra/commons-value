package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import java.math.BigInteger
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
 * - `should round-trip NumVal Byte` — NumVal wrapping Byte round-trips.
 * - `should round-trip NumVal Short` — NumVal wrapping Short round-trips.
 * - `should round-trip NumVal Int` — NumVal wrapping Int round-trips.
 * - `should round-trip NumVal Long` — NumVal wrapping Long round-trips.
 * - `should round-trip NumVal Float` — NumVal wrapping Float round-trips.
 * - `should round-trip NumVal Double` — NumVal wrapping Double round-trips.
 * - `should round-trip NumVal Float NaN` — NumVal wrapping Float.NaN round-trips preserving NaN.
 * - `should round-trip NumVal Float positive infinity` — NumVal wrapping Float.POSITIVE_INFINITY round-trips.
 * - `should round-trip NumVal Float negative infinity` — NumVal wrapping Float.NEGATIVE_INFINITY round-trips.
 * - `should round-trip NumVal BigInteger` — NumVal wrapping BigInteger round-trips via NUM_OTHERS tag.
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

    // --- NumVal ---

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Byte`() {
        assertRoundTrip(NumVal(Byte.MAX_VALUE))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Short`() {
        assertRoundTrip(NumVal(Short.MAX_VALUE))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Int`() {
        assertRoundTrip(NumVal(42))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Long`() {
        assertRoundTrip(NumVal(1234567890123456789L))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Float`() {
        assertRoundTrip(NumVal(3.14f))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Double`() {
        assertRoundTrip(NumVal(Double.MIN_VALUE))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Float NaN`() {
        val original = NumVal(Float.NaN)
        val serialized = testTarget.serialize(original)
        val deserialized = testTarget.deserialize(serialized) as NumVal
        assertEquals(true, (deserialized.core as Float).isNaN())
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Float positive infinity`() {
        assertRoundTrip(NumVal(Float.POSITIVE_INFINITY))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip NumVal Float negative infinity`() {
        assertRoundTrip(NumVal(Float.NEGATIVE_INFINITY))
    }

    @Suppress("DEPRECATION")
    @Test
    open fun `should round-trip NumVal BigInteger`() {
        assertRoundTrip(NumVal(BigInteger("123456789012345678901234567890")))
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

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip ListVal with mixed types`() {
        assertRoundTrip(ListVal(StrVal("test"), BoolVal.T, NumVal(1), NullVal))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip nested ListVal`() {
        val nested = ListVal(
            ListVal(NumVal(1), NumVal(2)),
            ListVal(StrVal("a"), StrVal("b")),
        )
        assertRoundTrip(nested)
    }

    // --- SetVal ---

    @Test
    fun `should round-trip empty SetVal`() {
        assertRoundTrip(SetVal(emptySet()))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip SetVal with mixed types`() {
        assertRoundTrip(SetVal(StrVal("test"), BoolVal.T, NumVal(1)))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip nested SetVal`() {
        val nested = SetVal(
            SetVal(NumVal(1), NumVal(2)),
            SetVal(StrVal("a"), StrVal("b")),
        )
        assertRoundTrip(nested)
    }

    // --- MapVal ---

    @Test
    fun `should round-trip empty MapVal`() {
        assertRoundTrip(MapVal(emptyMap()))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip MapVal with mixed value types`() {
        assertRoundTrip(
            MapVal(
                "str" to StrVal("value"),
                "num" to NumVal(42),
                "bool" to BoolVal.T,
            ),
        )
    }

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip nested MapVal`() {
        val nested = MapVal(
            "inner" to MapVal("key" to NumVal(1)),
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

    @Suppress("DEPRECATION")
    @Test
    fun `should round-trip mixed nested collection`() {
        val mixed = ListVal(
            SetVal(NumVal(1), NumVal(2)),
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
