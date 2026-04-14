package edu.jhu.cobra.commons.value

/**
 * Targeted tests to verify suspected bugs exist before fixing.
 * Each test isolates one suspected defect with a minimal reproduction case.
 *
 * - `data class ListVal copy shares mutable backing store` — shallow copy hazard
 * - `data class SetVal copy shares mutable backing store` — shallow copy hazard
 * - `data class MapVal copy shares mutable backing store` — shallow copy hazard
 * - `ByteBuffer RangeVal round-trip preserves Long bounds` — Int truncation
 * - `ByteBuffer RangeVal round-trip preserves Double bounds` — Int truncation
 * - `NumVal compareTo with Long MAX_VALUE` — Int truncation in compareTo
 * - `ByteBuffer NUM_OTHERS round-trip preserves BigInteger` — missing length prefix
 */
import edu.jhu.cobra.commons.value.serializer.DftByteBufferSerializerImpl
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class BugVerificationTest {

    // --- Fixed: data class shallow copy removed by converting to regular class ---
    // ListVal, SetVal, MapVal no longer have copy() — the hazard is eliminated.

    // --- Suspected: ByteBuffer RangeVal Int truncation ---

    @Test
    fun `ByteBuffer RangeVal round-trip preserves Long bounds`() {
        val range = RangeVal(NumVal(Long.MAX_VALUE), NumVal(Long.MAX_VALUE))
        val serialized = DftByteBufferSerializerImpl.serialize(range)
        val restored = DftByteBufferSerializerImpl.deserialize(serialized) as RangeVal
        assertEquals(Long.MAX_VALUE, restored.first.toLong(),
            "RangeVal Long bounds should survive ByteBuffer round-trip without truncation")
    }

    @Test
    fun `ByteBuffer RangeVal round-trip preserves Double bounds`() {
        val range = RangeVal(NumVal(3.14), NumVal(99.9))
        val serialized = DftByteBufferSerializerImpl.serialize(range)
        val restored = DftByteBufferSerializerImpl.deserialize(serialized) as RangeVal
        assertEquals(3.14, (restored.start.core as Number).toDouble(), 0.001,
            "RangeVal Double bounds should survive ByteBuffer round-trip without truncation")
    }

    // --- Suspected: NumVal.compareTo Int truncation ---

    @Test
    fun `NumVal compareTo with Long MAX_VALUE`() {
        val big = NumVal(Long.MAX_VALUE)
        // Long.MAX_VALUE > 0, so compareTo(0) should be positive
        assertTrue(big.compareTo(0) > 0,
            "NumVal(Long.MAX_VALUE).compareTo(0) should be positive, not truncated to Int")
    }

    @Test
    fun `NumVal compareTo with Double value`() {
        val d = NumVal(0.5)
        // 0.5 > 0, so compareTo(0) should be positive
        assertTrue(d.compareTo(0) > 0,
            "NumVal(0.5).compareTo(0) should be positive, not truncated to Int 0")
    }

    // --- Suspected: ByteBuffer NUM_OTHERS BigInteger ---

    @Test
    fun `ByteBuffer NUM_OTHERS round-trip preserves BigInteger`() {
        val value = NumVal(BigInteger("123456789012345678901234567890"))
        val serialized = DftByteBufferSerializerImpl.serialize(value)
        val restored = DftByteBufferSerializerImpl.deserialize(serialized)
        assertEquals(value, restored,
            "NumVal(BigInteger) should survive ByteBuffer round-trip")
    }
}
