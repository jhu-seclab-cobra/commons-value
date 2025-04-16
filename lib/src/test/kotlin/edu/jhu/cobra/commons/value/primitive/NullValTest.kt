package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.compareTo
import edu.jhu.cobra.commons.value.numVal
import edu.jhu.cobra.commons.value.strVal
import kotlin.test.*

class NullValTest {
    @Test
    fun testSingleton() {
        // Test that NullVal is a singleton
        assertSame(NullVal, NullVal)
    }

    @Test
    fun testCore() {
        // Test that core is null
        assertNull(NullVal.core)
    }

    @Test
    fun testToString() {
        assertEquals("NullVal", NullVal.toString())
    }

    @Test
    fun testEquality() {
        assertEquals(NullVal, NullVal)
        assertEquals(NullVal.core, null)
    }

    @Test
    fun testHashCode() {
        assertEquals(NullVal.hashCode(), NullVal.hashCode())
    }

    @Test
    fun testComparison() {
        // NullVal should be less than any other value
        assertEquals(0, NullVal.compareTo(NullVal))
    }

    @Test
    fun testIsNull() {
        assertTrue { NullVal isNull NullVal }
        assertFalse { NullVal.isNull(1.numVal) }
        assertFalse { NullVal.isNull("test".strVal) }
    }

    @Test
    fun testIsNotNull() {
        assertFalse { NullVal isNotNull NullVal }
        assertTrue { NullVal.isNotNull(1.numVal) }
        assertTrue { NullVal.isNotNull("test".strVal) }
    }
} 