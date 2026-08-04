package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.IPrimitiveVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Black-box tests for [BoolVal] derived from the design doc.
 *
 * - `should have true core for T` — T singleton core is true
 * - `should have false core for F` — F singleton core is false
 * - `should return T singleton when invoke with true` — factory identity
 * - `should return F singleton when invoke with false` — factory identity
 * - `should return F when invoke with no arguments` — default factory
 * - `should return true from isTrue when core is true` — isTrue on T
 * - `should return false from isTrue when core is false` — isTrue on F
 * - `should return true from isFalse when core is false` — isFalse on F
 * - `should return false from isFalse when core is true` — isFalse on T
 * - `should be equal when same boolean value` — structural equality
 * - `should not be equal when different boolean value` — T != F
 * - `should have consistent hashCode for equal instances` — hashCode contract
 * - `should implement IPrimitiveVal` — type hierarchy
 */
internal class BoolValTest {
    @Test
    fun `should have true core for T`() {
        assertTrue(BoolVal.T.core)
    }

    @Test
    fun `should have false core for F`() {
        assertFalse(BoolVal.F.core)
    }

    @Test
    fun `should return T singleton when invoke with true`() {
        assertSame(BoolVal.T, BoolVal(true))
    }

    @Test
    fun `should return F singleton when invoke with false`() {
        assertSame(BoolVal.F, BoolVal(false))
    }

    @Test
    fun `should return F when invoke with no arguments`() {
        assertSame(BoolVal.F, BoolVal())
    }

    @Test
    fun `should return correct core when invoke with true`() {
        assertEquals(true, BoolVal(true).core)
    }

    @Test
    fun `should return correct core when invoke with false`() {
        assertEquals(false, BoolVal(false).core)
    }

    @Test
    fun `should return true from isTrue when core is true`() {
        assertTrue(BoolVal.T.isTrue())
    }

    @Test
    fun `should return false from isTrue when core is false`() {
        assertFalse(BoolVal.F.isTrue())
    }

    @Test
    fun `should return true from isFalse when core is false`() {
        assertTrue(BoolVal.F.isFalse())
    }

    @Test
    fun `should return false from isFalse when core is true`() {
        assertFalse(BoolVal.T.isFalse())
    }

    @Test
    fun `should be equal when same boolean value`() {
        assertEquals(BoolVal(true), BoolVal.T)
        assertEquals(BoolVal(false), BoolVal.F)
    }

    @Test
    fun `should not be equal when different boolean value`() {
        assertFalse(BoolVal.T == BoolVal.F)
    }

    @Test
    fun `should have consistent hashCode for equal instances`() {
        assertEquals(BoolVal(true).hashCode(), BoolVal.T.hashCode())
        assertEquals(BoolVal(false).hashCode(), BoolVal.F.hashCode())
    }

    @Test
    fun `should implement IPrimitiveVal`() {
        val value: IPrimitiveVal = BoolVal.T
        assertTrue(value is BoolVal)
    }
}
