package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Black-box tests for [Unsure] derived from the design doc.
 *
 * - `should have four entries` — ANY, STR, NUM, BOOL exist
 * - `should have correct core for ANY` — core is __IPrimitiveVal__
 * - `should have correct core for STR` — core is __StrVal__
 * - `should have correct core for NUM` — core is __NumVal__
 * - `should have correct core for BOOL` — core is __BoolVal__
 * - `should return ANY from new with valid string` — new(String) for ANY
 * - `should return STR from new with valid string` — new(String) for STR
 * - `should return NUM from new with valid string` — new(String) for NUM
 * - `should return BOOL from new with valid string` — new(String) for BOOL
 * - `should return null from new with invalid string` — new(String) invalid
 * - `should return null from new with empty string` — new(String) empty
 * - `should infer STR from StrVal example` — new(IPrimitiveVal) with StrVal
 * - `should infer BOOL from BoolVal example` — new(IPrimitiveVal) with BoolVal
 * - `should infer ANY from NullVal example` — new(IPrimitiveVal) with NullVal
 * - `should return self from new with Unsure example` — new(IPrimitiveVal) identity
 * - `should return STR from reified new StrVal` — new<StrVal>()
 * - `should return BOOL from reified new BoolVal` — new<BoolVal>()
 * - `should return ANY from reified new NullVal` — new<NullVal>()
 * - `should return true from contains for valid identifiers` — contains valid
 * - `should return false from contains for invalid string` — contains invalid
 * - `should return false from contains for empty string` — contains boundary
 * - `should implement IPrimitiveVal` — type hierarchy
 * - `should infer NUM from IntVal example` — new(IPrimitiveVal) with IntVal
 * - `should infer NUM from FloatVal example` — new(IPrimitiveVal) with FloatVal
 * - `should return NUM from reified new IntVal` — new<IntVal>()
 * - `should return NUM from reified new FloatVal` — new<FloatVal>()
 */
internal class UnsureTest {
    // --- Entries and core identifiers ---

    @Test
    fun `should have four entries`() {
        assertEquals(4, Unsure.entries.size)
    }

    @Test
    fun `should have correct core for ANY`() {
        assertEquals("__IPrimitiveVal__", Unsure.ANY.core)
    }

    @Test
    fun `should have correct core for STR`() {
        assertEquals("__StrVal__", Unsure.STR.core)
    }

    @Test
    fun `should have correct core for NUM`() {
        assertEquals("__NumVal__", Unsure.NUM.core)
    }

    @Test
    fun `should have correct core for BOOL`() {
        assertEquals("__BoolVal__", Unsure.BOOL.core)
    }

    // --- new(String) ---

    @Test
    fun `should return ANY from new with valid string`() {
        assertEquals(Unsure.ANY, Unsure.new("__IPrimitiveVal__"))
    }

    @Test
    fun `should return STR from new with valid string`() {
        assertEquals(Unsure.STR, Unsure.new("__StrVal__"))
    }

    @Test
    fun `should return NUM from new with valid string`() {
        assertEquals(Unsure.NUM, Unsure.new("__NumVal__"))
    }

    @Test
    fun `should return BOOL from new with valid string`() {
        assertEquals(Unsure.BOOL, Unsure.new("__BoolVal__"))
    }

    @Test
    fun `should return null from new with invalid string`() {
        assertNull(Unsure.new("invalid"))
    }

    @Test
    fun `should return null from new with empty string`() {
        assertNull(Unsure.new(""))
    }

    // --- new(IPrimitiveVal) ---

    @Test
    fun `should infer STR from StrVal example`() {
        assertEquals(Unsure.STR, Unsure.new(StrVal("test")))
    }

    @Test
    fun `should infer BOOL from BoolVal example`() {
        assertEquals(Unsure.BOOL, Unsure.new(BoolVal.T))
    }

    @Test
    fun `should infer ANY from NullVal example`() {
        assertEquals(Unsure.ANY, Unsure.new(NullVal))
    }

    @Test
    fun `should return self from new with Unsure example`() {
        assertEquals(Unsure.STR, Unsure.new(Unsure.STR))
        assertEquals(Unsure.NUM, Unsure.new(Unsure.NUM))
        assertEquals(Unsure.BOOL, Unsure.new(Unsure.BOOL))
        assertEquals(Unsure.ANY, Unsure.new(Unsure.ANY))
    }

    // --- new<T>() reified ---

    @Test
    fun `should return STR from reified new StrVal`() {
        assertEquals(Unsure.STR, Unsure.new<StrVal>())
    }

    @Test
    fun `should return BOOL from reified new BoolVal`() {
        assertEquals(Unsure.BOOL, Unsure.new<BoolVal>())
    }

    @Test
    fun `should return ANY from reified new NullVal`() {
        assertEquals(Unsure.ANY, Unsure.new<NullVal>())
    }

    // --- operator contains ---

    @Test
    fun `should return true from contains for IPrimitiveVal identifier`() {
        assertTrue("__IPrimitiveVal__" in Unsure)
    }

    @Test
    fun `should return true from contains for StrVal identifier`() {
        assertTrue("__StrVal__" in Unsure)
    }

    @Test
    fun `should return true from contains for NumVal identifier`() {
        assertTrue("__NumVal__" in Unsure)
    }

    @Test
    fun `should return true from contains for BoolVal identifier`() {
        assertTrue("__BoolVal__" in Unsure)
    }

    @Test
    fun `should return false from contains for invalid string`() {
        assertFalse("invalid" in Unsure)
    }

    @Test
    fun `should return false from contains for empty string`() {
        assertFalse("" in Unsure)
    }

    // --- Type hierarchy ---

    @Test
    fun `should implement IPrimitiveVal`() {
        val value: IPrimitiveVal = Unsure.STR
        assertTrue(value is Unsure)
    }

    // --- new(IPrimitiveVal) with IntVal/FloatVal ---

    @Test
    fun `should infer NUM from IntVal example`() {
        assertEquals(Unsure.NUM, Unsure.new(IntVal(42L)))
    }

    @Test
    fun `should infer NUM from FloatVal example`() {
        assertEquals(Unsure.NUM, Unsure.new(FloatVal(3.14)))
    }

    // --- new<T>() reified with IntVal/FloatVal ---

    @Test
    fun `should return NUM from reified new IntVal`() {
        assertEquals(Unsure.NUM, Unsure.new<IntVal>())
    }

    @Test
    fun `should return NUM from reified new FloatVal`() {
        assertEquals(Unsure.NUM, Unsure.new<FloatVal>())
    }
}
