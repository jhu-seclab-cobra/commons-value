package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IPrimitiveVal
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Black-box specification tests for [IPrimitiveVal] sealed interface derived from design-primitive.md.
 *
 * Subtype membership:
 * - `should be implemented by StrVal` -- StrVal is IPrimitiveVal
 * - `should be implemented by IntVal` -- IntVal is IPrimitiveVal
 * - `should be implemented by FloatVal` -- FloatVal is IPrimitiveVal
 * - `should be implemented by NumVal` -- NumVal is IPrimitiveVal (deprecated)
 * - `should be implemented by BoolVal` -- BoolVal is IPrimitiveVal
 * - `should be implemented by NullVal` -- NullVal is IPrimitiveVal
 * - `should be implemented by Unsure` -- Unsure is IPrimitiveVal
 *
 * Exhaustive when:
 * - `should support exhaustive when over StrVal` -- sealed branch coverage
 * - `should support exhaustive when over IntVal` -- sealed branch coverage
 * - `should support exhaustive when over FloatVal` -- sealed branch coverage
 * - `should support exhaustive when over NumVal` -- sealed branch coverage (deprecated)
 * - `should support exhaustive when over BoolVal` -- sealed branch coverage
 * - `should support exhaustive when over NullVal` -- sealed branch coverage
 * - `should support exhaustive when over Unsure` -- sealed branch coverage
 */
@Suppress("DEPRECATION")
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
    fun `should be implemented by NumVal`() {
        val value: IPrimitiveVal = NumVal(42)
        assertTrue(value is NumVal)
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
    fun `should support exhaustive when over NumVal`() {
        val result = exhaustiveWhen(NumVal(42))
        assertEquals("num", result)
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

    private fun exhaustiveWhen(value: IPrimitiveVal): String = when (value) {
        is StrVal -> "str"
        is IntVal -> "int"
        is FloatVal -> "float"
        is NumVal -> "num"
        is BoolVal -> "bool"
        is NullVal -> "null"
        is Unsure -> "unsure"
    }
}
