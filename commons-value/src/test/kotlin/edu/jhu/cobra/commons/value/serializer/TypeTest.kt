package edu.jhu.cobra.commons.value.serializer

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the [Type] enum tag uniqueness invariant.
 *
 * - `should have unique byte values across all entries` — no two tags share a binary encoding.
 * - `should have unique str values across all entries` — no two tags share a text encoding.
 */
internal class TypeTest {
    @Test
    fun `should have unique byte values across all entries`() {
        val entries = Type.entries
        assertEquals(entries.size, entries.map { it.byte }.toSet().size)
    }

    @Test
    fun `should have unique str values across all entries`() {
        val entries = Type.entries
        assertEquals(entries.size, entries.map { it.str }.toSet().size)
    }
}
