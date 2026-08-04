package edu.jhu.cobra.commons.value

/**
 * Targeted tests to verify suspected bugs exist before fixing.
 * Each test isolates one suspected defect with a minimal reproduction case.
 *
 * - `ByteBuffer RangeVal round-trip preserves Long bounds` — Int truncation
 * - `IntVal compareTo with Long MAX_VALUE` — Int truncation in compareTo
 */
import edu.jhu.cobra.commons.value.serializer.DftByteBufferSerializerImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class BugVerificationTest {
    // --- Suspected: ByteBuffer RangeVal Int truncation ---

    @Test
    fun `ByteBuffer RangeVal round-trip preserves Long bounds`() {
        val range = RangeVal(IntVal(Long.MAX_VALUE), IntVal(Long.MAX_VALUE))
        val serialized = DftByteBufferSerializerImpl.serialize(range)
        val restored = DftByteBufferSerializerImpl.deserialize(serialized) as RangeVal
        assertEquals(
            Long.MAX_VALUE,
            restored.first,
            "RangeVal Long bounds should survive ByteBuffer round-trip without truncation",
        )
    }

    // --- Suspected: IntVal.compareTo Int truncation ---

    @Test
    fun `IntVal compareTo with Long MAX_VALUE`() {
        val big = IntVal(Long.MAX_VALUE)
        // Long.MAX_VALUE > 0, so compareTo(0) should be positive
        assertTrue(
            big.compareTo(0) > 0,
            "IntVal(Long.MAX_VALUE).compareTo(0) should be positive, not truncated to Int",
        )
    }
}
