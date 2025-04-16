package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.StrVal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IPrimitiveTest {
    @Test
    fun testIValueCore() {
        assertNull(NullVal.core)
        val numVal = NumVal(42)
        assertEquals(42, numVal.core)
        val strVal = StrVal("test")
        assertEquals("test", strVal.core)
        assertEquals(true, BoolVal.T.core)
        assertEquals(false, BoolVal.F.core)
    }
} 