package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.*
import kotlin.test.*

class UnsureTest {
    @Test
    fun testCoreValues() {
        assertEquals("__IPrimitiveVal__", Unsure.ANY.core)
        assertEquals("__StrVal__", Unsure.STR.core)
        assertEquals("__NumVal__", Unsure.NUM.core)
        assertEquals("__BoolVal__", Unsure.BOOL.core)
    }

    @Test
    fun testValueOf() {
        assertEquals(Unsure.STR, Unsure.new("__StrVal__"))
        assertEquals(Unsure.NUM, Unsure.new("__NumVal__"))
        assertEquals(Unsure.BOOL, Unsure.new("__BoolVal__"))
        assertEquals(Unsure.ANY, Unsure.new("__IPrimitiveVal__"))
        assertNull(Unsure.new("invalid"))
    }

    @Test
    fun testNewWithExample() {
        assertEquals(Unsure.STR, Unsure.new(StrVal("test")))
        assertEquals(Unsure.NUM, Unsure.new(NumVal(42)))
        assertEquals(Unsure.BOOL, Unsure.new(BoolVal.T))
        assertEquals(Unsure.ANY, Unsure.new(NullVal))

        // Test with Unsure itself
        assertEquals(Unsure.STR, Unsure.new(Unsure.STR))
    }

    @Test
    fun testNewWithType() {
        assertEquals(Unsure.STR, Unsure.new<StrVal>())
        assertEquals(Unsure.NUM, Unsure.new<NumVal>())
        assertEquals(Unsure.BOOL, Unsure.new<BoolVal>())
        assertEquals(Unsure.ANY, Unsure.new<NullVal>())
    }

    @Test
    fun testContains() {
        assertTrue("__StrVal__" in Unsure)
        assertTrue("__NumVal__" in Unsure)
        assertTrue("__BoolVal__" in Unsure)
        assertTrue("__IPrimitiveVal__" in Unsure)
        assertFalse("invalid" in Unsure)
    }

    @Test
    fun testToString() {
        assertEquals("Unsure{__StrVal__}", Unsure.STR.toString())
        assertEquals("Unsure{__NumVal__}", Unsure.NUM.toString())
        assertEquals("Unsure{__BoolVal__}", Unsure.BOOL.toString())
        assertEquals("Unsure{__IPrimitiveVal__}", Unsure.ANY.toString())
    }
} 