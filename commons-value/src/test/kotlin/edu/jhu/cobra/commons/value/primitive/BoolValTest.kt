package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import kotlin.test.*

class BoolValTest {
    @Test
    fun testConstants() {
        assertTrue(BoolVal.T.core)
        assertFalse(BoolVal.F.core)
    }

    @Test
    fun testDefaultConstructor() {
        val defaultBool = BoolVal()
        assertFalse(defaultBool.core)
    }

    @Test
    fun testParameterizedConstructor() {
        val trueVal = BoolVal(true)
        assertTrue(trueVal.core)

        val falseVal = BoolVal(false)
        assertFalse(falseVal.core)
    }

    @Test
    fun testIsTrue() {
        assertTrue(BoolVal.T.isTrue())
        assertFalse(BoolVal.F.isTrue())
    }

    @Test
    fun testIsFalse() {
        assertFalse(BoolVal.T.isFalse())
        assertTrue(BoolVal.F.isFalse())
    }

    @Test
    fun testToString() {
        assertEquals("BoolVal{true}", BoolVal.T.toString())
        assertEquals("BoolVal{false}", BoolVal.F.toString())
    }

    @Test
    fun testEquality() {
        assertEquals(BoolVal(true), BoolVal.T)
        assertEquals(BoolVal(false), BoolVal.F)
        assertNotEquals(BoolVal.T, BoolVal.F)
    }

    @Test
    fun testHashCode() {
        assertEquals(BoolVal(true).hashCode(), BoolVal.T.hashCode())
        assertEquals(BoolVal(false).hashCode(), BoolVal.F.hashCode())
        assertNotEquals(BoolVal.T.hashCode(), BoolVal.F.hashCode())
    }
} 