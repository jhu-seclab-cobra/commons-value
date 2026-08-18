package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Golden wire-format tests pinning the exact serialized encoding of one nested sample value
 * per serializer implementation.
 *
 * The expected constants were captured from the serializer output before the container-codec
 * refactor; any change to them is a wire-format break, not a test to update.
 *
 * Test list:
 * - `should emit pinned bytes for nested sample via byte array serializer`
 * - `should emit pinned bytes for nested sample via byte buffer serializer`
 * - `should emit pinned text for nested sample via char buffer serializer`
 */
internal class WireFormatGoldenTest {
    // One value exercising every container tag (LIST, SET, MAP, RANGE) plus each scalar tag.
    private val sample: IValue =
        ListVal(
            IntVal(7),
            StrVal("ab"),
            BoolVal.T,
            BoolVal.F,
            NullVal,
            Unsure.NUM,
            FloatVal(1.5),
            RangeVal(1L, 3L),
            SetVal(IntVal(1), StrVal("x")),
            MapVal("k" to StrVal("v")),
        )

    private val goldenByteArrayHex =
        "460000000939000000000000000700000003146162000000021e01000000021e00000000010a000000012a00000009" +
            "3a3ff8000000000000000000173c00000009390000000000000001390000000000000003000000144700000009" +
            "3900000000000000010000000214780000000c50000000016b000000021476"

    private val goldenByteBufferHex =
        "460000000a390000000000000007140000000261621f200a2a3a3ff80000000000003c3900000000000000013900" +
            "0000000000000347000000023900000000000000011400000001785000000001000000016b140000000176"

    private val goldenCharBufferText =
        "List:a:IntV:7:,Str:2:ab,True:,False:,Null:,UnNUM:,FloatV:1.5:,Range:1,3:," +
            "Set:2:IntV:1:,Str:1:x:,Map:1:Str:1:k=Str:1:v::"

    private fun ByteArray.toHex(): String = joinToString("") { byte -> "%02x".format(byte) }

    @Test
    fun `should emit pinned bytes for nested sample via byte array serializer`() {
        assertEquals(goldenByteArrayHex, DftByteArraySerializerImpl.serialize(sample).toHex())
    }

    @Test
    fun `should emit pinned bytes for nested sample via byte buffer serializer`() {
        val buffer = DftByteBufferSerializerImpl.serialize(sample)
        val bytes = ByteArray(buffer.limit()).also { buffer.get(it) }
        assertEquals(goldenByteBufferHex, bytes.toHex())
    }

    @Test
    fun `should emit pinned text for nested sample via char buffer serializer`() {
        assertEquals(goldenCharBufferText, DftCharBufferSerializerImpl.serialize(sample).toString())
    }
}
