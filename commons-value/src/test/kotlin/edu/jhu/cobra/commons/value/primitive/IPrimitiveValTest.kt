package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IPrimitiveVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Black-box tests for [IPrimitiveVal] sealed interface derived from the design doc.
 *
 * - `should be implemented by StrVal` — StrVal is IPrimitiveVal
 * - `should be implemented by NumVal` — NumVal is IPrimitiveVal
 * - `should be implemented by BoolVal` — BoolVal is IPrimitiveVal
 * - `should be implemented by NullVal` — NullVal is IPrimitiveVal
 * - `should be implemented by Unsure` — Unsure is IPrimitiveVal
 * - `should support exhaustive when over all subtypes` — sealed hierarchy completeness
 */
internal class IPrimitiveValTest {

    @Test
    fun `should be implemented by StrVal`() {
        val value: IPrimitiveVal = StrVal("test")
        assertTrue(value is StrVal)
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
        val value: IPrimitiveVal = StrVal("test")
        val result = exhaustiveWhen(value)
        assertNotNull(result)
    }

    @Test
    fun `should support exhaustive when over NumVal`() {
        val value: IPrimitiveVal = NumVal(42)
        val result = exhaustiveWhen(value)
        assertNotNull(result)
    }

    @Test
    fun `should support exhaustive when over BoolVal`() {
        val value: IPrimitiveVal = BoolVal.T
        val result = exhaustiveWhen(value)
        assertNotNull(result)
    }

    @Test
    fun `should support exhaustive when over NullVal`() {
        val value: IPrimitiveVal = NullVal
        val result = exhaustiveWhen(value)
        assertNotNull(result)
    }

    @Test
    fun `should support exhaustive when over Unsure`() {
        val value: IPrimitiveVal = Unsure.ANY
        val result = exhaustiveWhen(value)
        assertNotNull(result)
    }

    private fun exhaustiveWhen(value: IPrimitiveVal): String = when (value) {
        is StrVal -> "str"
        is NumVal -> "num"
        is BoolVal -> "bool"
        is NullVal -> "null"
        is Unsure -> "unsure"
    }
}
