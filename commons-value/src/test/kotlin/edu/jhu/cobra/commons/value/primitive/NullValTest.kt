package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Black-box tests for [NullVal] derived from the design doc.
 *
 * - `should be a singleton` — same identity across references
 * - `should have null core` — core is always null
 * - `should return true from isNull when value is NullVal` — infix isNull self
 * - `should return false from isNull when value is StrVal` — infix isNull non-null
 * - `should return false from isNull when value is IntVal` — infix isNull non-null
 * - `should return false from isNull when value is BoolVal` — infix isNull non-null
 * - `should return false from isNull when value is Unsure` — infix isNull non-null
 * - `should return false from isNotNull when value is NullVal` — infix isNotNull self
 * - `should return true from isNotNull when value is StrVal` — infix isNotNull non-null
 * - `should return true from isNotNull when value is IntVal` — infix isNotNull non-null
 * - `should return true from isNotNull when value is BoolVal` — infix isNotNull non-null
 * - `should return true from isNotNull when value is Unsure` — infix isNotNull non-null
 */
internal class NullValTest {

    @Test
    fun `should be a singleton`() {
        assertSame(NullVal, NullVal)
    }

    @Test
    fun `should have null core`() {
        assertNull(NullVal.core)
    }

    // --- isNull ---

    @Test
    fun `should return true from isNull when value is NullVal`() {
        assertTrue(NullVal isNull NullVal)
    }

    @Test
    fun `should return false from isNull when value is StrVal`() {
        assertFalse(NullVal isNull StrVal("hello"))
    }

    @Test
    fun `should return false from isNull when value is IntVal`() {
        assertFalse(NullVal isNull IntVal(42L))
    }

    @Test
    fun `should return false from isNull when value is BoolVal`() {
        assertFalse(NullVal isNull BoolVal.T)
    }

    @Test
    fun `should return false from isNull when value is Unsure`() {
        assertFalse(NullVal isNull Unsure.ANY)
    }

    // --- isNotNull ---

    @Test
    fun `should return false from isNotNull when value is NullVal`() {
        assertFalse(NullVal isNotNull NullVal)
    }

    @Test
    fun `should return true from isNotNull when value is StrVal`() {
        assertTrue(NullVal isNotNull StrVal("hello"))
    }

    @Test
    fun `should return true from isNotNull when value is IntVal`() {
        assertTrue(NullVal isNotNull IntVal(42L))
    }

    @Test
    fun `should return true from isNotNull when value is BoolVal`() {
        assertTrue(NullVal isNotNull BoolVal.F)
    }

    @Test
    fun `should return true from isNotNull when value is Unsure`() {
        assertTrue(NullVal isNotNull Unsure.STR)
    }
}
