package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Black-box specification tests for [IPrimitiveVal] sealed interface.
 *
 * Subtype membership:
 * - `should be implemented by StrVal` -- StrVal is IPrimitiveVal
 * - `should be implemented by IntVal` -- IntVal is IPrimitiveVal
 * - `should be implemented by FloatVal` -- FloatVal is IPrimitiveVal
 * - `should be implemented by BoolVal` -- BoolVal is IPrimitiveVal
 * - `should be implemented by NullVal` -- NullVal is IPrimitiveVal
 * - `should be implemented by Unsure` -- Unsure is IPrimitiveVal
 *
 * Exhaustive when:
 * - `should support exhaustive when over StrVal` -- sealed branch coverage
 * - `should support exhaustive when over IntVal` -- sealed branch coverage
 * - `should support exhaustive when over FloatVal` -- sealed branch coverage
 * - `should support exhaustive when over BoolVal` -- sealed branch coverage
 * - `should support exhaustive when over NullVal` -- sealed branch coverage
 * - `should support exhaustive when over Unsure` -- sealed branch coverage
 *
 * Total order (compareTo), same kind:
 * - `should compare StrVal lexicographically` -- StrVal < and ==
 * - `should compare BoolVal false below true` -- BoolVal order and ==
 * - `should compare NullVal equal to NullVal` -- NullVal ==
 * - `should compare IntVal by long value` -- IntVal <, >, ==
 * - `should compare FloatVal by double value` -- FloatVal <
 * - `should compare IntVal against FloatVal within double precision` -- mixed numeric fast path
 *
 * Total order (compareTo), cross kind:
 * - `should rank kinds null below bool below numeric below string below unsure` -- kind ranking
 * - `should compare IntVal beyond double precision exactly against FloatVal` -- no Double rounding
 * - `should compare huge IntVal exactly against fractional FloatVal` -- BigDecimal path
 * - `should order NaN above all numbers` -- NaN placement
 * - `should order negative zero equivalent to positive zero` -- -0.0 == 0.0 in order
 * - `should order Unsure entries by declaration order` -- Unsure ordinal ranking
 * - `should sort mixed primitives deterministically` -- sortedWith over full kind mix
 *
 * deepCopy:
 * - `should return same instance from deepCopy for every primitive kind` -- immutable identity copy
 */
internal class IPrimitiveValTest {
    @Test
    fun `should be implemented by StrVal`() {
        val value: IPrimitiveVal = StrVal("test")
        assertTrue(value is StrVal)
    }

    @Test
    fun `should be implemented by IntVal`() {
        val value: IPrimitiveVal = IntVal(42L)
        assertTrue(value is IntVal)
    }

    @Test
    fun `should be implemented by FloatVal`() {
        val value: IPrimitiveVal = FloatVal(3.14)
        assertTrue(value is FloatVal)
    }

    @Test
    fun `should be implemented by BoolVal`() {
        val value: IPrimitiveVal = BoolVal.T
        assertTrue(value is BoolVal)
    }

    @Test
    fun `should be implemented by NullVal`() {
        val value: IPrimitiveVal = NullVal
        assertTrue(value is NullVal)
    }

    @Test
    fun `should be implemented by Unsure`() {
        val value: IPrimitiveVal = Unsure.ANY
        assertTrue(value is Unsure)
    }

    @Test
    fun `should support exhaustive when over StrVal`() {
        val result = exhaustiveWhen(StrVal("test"))
        assertEquals("str", result)
    }

    @Test
    fun `should support exhaustive when over IntVal`() {
        val result = exhaustiveWhen(IntVal(42L))
        assertEquals("int", result)
    }

    @Test
    fun `should support exhaustive when over FloatVal`() {
        val result = exhaustiveWhen(FloatVal(3.14))
        assertEquals("float", result)
    }

    @Test
    fun `should support exhaustive when over BoolVal`() {
        val result = exhaustiveWhen(BoolVal.T)
        assertEquals("bool", result)
    }

    @Test
    fun `should support exhaustive when over NullVal`() {
        val result = exhaustiveWhen(NullVal)
        assertEquals("null", result)
    }

    @Test
    fun `should support exhaustive when over Unsure`() {
        val result = exhaustiveWhen(Unsure.ANY)
        assertEquals("unsure", result)
    }

    private fun exhaustiveWhen(value: IPrimitiveVal): String =
        when (value) {
            is StrVal -> "str"
            is IntVal -> "int"
            is FloatVal -> "float"
            is BoolVal -> "bool"
            is NullVal -> "null"
            is Unsure -> "unsure"
        }

    // --- total order: same kind ---

    @Test
    fun `should compare StrVal lexicographically`() {
        assertTrue(StrVal("a") < StrVal("b"))
        assertEquals(0, StrVal("a").compareTo(StrVal("a")))
    }

    @Test
    fun `should compare BoolVal false below true`() {
        assertTrue(BoolVal.F < BoolVal.T)
        assertEquals(0, BoolVal.T.compareTo(BoolVal.T))
    }

    @Test
    fun `should compare NullVal equal to NullVal`() {
        assertEquals(0, NullVal.compareTo(NullVal))
    }

    @Test
    fun `should compare IntVal by long value`() {
        assertTrue(IntVal(1L) < IntVal(2L))
        assertTrue(IntVal(2L) > IntVal(1L))
        assertEquals(0, IntVal(1L).compareTo(IntVal(1L)))
    }

    @Test
    fun `should compare FloatVal by double value`() {
        assertTrue(FloatVal(1.0) < FloatVal(2.0))
    }

    @Test
    fun `should compare IntVal against FloatVal within double precision`() {
        assertTrue(IntVal(1L) < FloatVal(1.5))
        assertTrue(FloatVal(1.5) > IntVal(1L))
    }

    // --- total order: cross kind ---

    @Test
    fun `should rank kinds null below bool below numeric below string below unsure`() {
        assertTrue(NullVal < BoolVal.T)
        assertTrue(BoolVal.T < IntVal(Long.MIN_VALUE))
        assertTrue(FloatVal(Double.POSITIVE_INFINITY) < StrVal(""))
        assertTrue(StrVal("z") < Unsure.ANY)
    }

    @Test
    fun `should compare IntVal beyond double precision exactly against FloatVal`() {
        val large = (1L shl 53) + 1
        assertTrue(IntVal(large) > FloatVal((1L shl 53).toDouble()))
        assertTrue(IntVal(-large) < FloatVal(-(1L shl 53).toDouble()))
    }

    @Test
    fun `should compare huge IntVal exactly against fractional FloatVal`() {
        assertTrue(IntVal(Long.MAX_VALUE) > FloatVal(9.223372036854775E18))
        assertTrue(IntVal(Long.MAX_VALUE) < FloatVal(9.3E18))
    }

    @Test
    fun `should order NaN above all numbers`() {
        assertTrue(FloatVal(Double.NaN) > IntVal(Long.MAX_VALUE))
        assertTrue(FloatVal(Double.NaN) > FloatVal(Double.POSITIVE_INFINITY))
        assertEquals(0, FloatVal(Double.NaN).compareTo(FloatVal(Double.NaN)))
    }

    @Test
    fun `should order negative zero equivalent to positive zero`() {
        assertEquals(0, FloatVal(-0.0).compareTo(FloatVal(0.0)))
        assertEquals(0, IntVal(0L).compareTo(FloatVal(-0.0)))
    }

    @Test
    fun `should order Unsure entries by declaration order`() {
        assertTrue(Unsure.ANY < Unsure.STR)
        assertTrue(Unsure.STR < Unsure.NUM)
        assertTrue(Unsure.NUM < Unsure.BOOL)
    }

    @Test
    fun `should sort mixed primitives deterministically`() {
        val sorted =
            listOf<IPrimitiveVal>(
                Unsure.ANY,
                StrVal("a"),
                FloatVal(0.5),
                IntVal(1L),
                BoolVal.T,
                NullVal,
            ).sorted()
        assertEquals(
            listOf<IPrimitiveVal>(NullVal, BoolVal.T, FloatVal(0.5), IntVal(1L), StrVal("a"), Unsure.ANY),
            sorted,
        )
    }

    // --- deepCopy ---

    @Test
    fun `should return same instance from deepCopy for every primitive kind`() {
        val primitives =
            listOf<IPrimitiveVal>(StrVal("a"), IntVal(1L), FloatVal(0.5), BoolVal.T, NullVal, Unsure.ANY)
        for (primitive in primitives) {
            assertSame(primitive, primitive.deepCopy())
        }
    }
}
