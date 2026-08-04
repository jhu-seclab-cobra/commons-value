package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * Black-box tests for [NullVal] derived from the design doc.
 *
 * - `should be a singleton` — same identity across references
 * - `should have null core` — core is always null
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
}
