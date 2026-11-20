package edu.jhu.cobra.commons.value

// The codec between a byte sequence and the canonical text of a StrVal.

// A unit U+DC80..U+DCFF stands for the byte 80..FF of its low eight bits.
private const val BYTE_UNIT_BASE = 0xDC00
private const val FIRST_BYTE_UNIT = 0xDC80
private const val LAST_BYTE_UNIT = 0xDCFF

// UTF-16 units D800..DFFF are exactly the units with these five high bits.
private const val SURROGATE_MASK = 0xF800
private const val SURROGATE_BITS = 0xD800

private const val ASCII_END = 0x80
private const val TWO_BYTE_END = 0x800
private const val SUPPLEMENTARY_START = 0x10000

// Lead-byte marker bits indexed by the number of continuation bytes that follow.
private val LEAD_MARKERS = intArrayOf(0x00, 0xC0, 0xE0, 0xF0)
private const val CONTINUATION_MIN = 0x80
private const val CONTINUATION_MAX = 0xBF
private const val CONTINUATION_PAYLOAD = 0x3F
private const val BYTE_MASK = 0xFF
private const val PAYLOAD_BITS = 6

/** Whether this unit is U+DC80..U+DCFF, the unit of one byte outside UTF-8. */
internal fun Char.isByteOutsideUtf8(): Boolean = code in FIRST_BYTE_UNIT..LAST_BYTE_UNIT

/**
 * Decodes [bytes] greedily: each well-formed UTF-8 sequence (Unicode Table 3-7) becomes its character,
 * and each other byte `XX` becomes the unit U+DCXX. The result is canonical.
 */
internal fun decodeBytes(bytes: ByteArray): String {
    if (bytes.isWellFormedUtf8()) return String(bytes, Charsets.UTF_8)
    val text = StringBuilder(bytes.size)
    var index = 0
    while (index < bytes.size) {
        val length = wellFormedLength(index, bytes.size) { bytes[it].toInt() and BYTE_MASK }
        if (length == 0) {
            text.append((BYTE_UNIT_BASE + (bytes[index].toInt() and BYTE_MASK)).toChar())
            index++
        } else {
            text.appendCodePoint(codePointAt(index, length) { bytes[it].toInt() and BYTE_MASK })
            index += length
        }
    }
    return text.toString()
}

/**
 * The canonical text of [text]: each maximal run of unpaired units U+DC80..U+DCFF is replaced by the
 * decoding of the bytes it stands for, and every other unit is kept. Every `StrVal` constructor argument
 * passes here; text without a unit in D800..DFFF is returned unchanged.
 */
internal fun canonical(text: String): String {
    var index = 0
    while (index < text.length) {
        if (text[index].code and SURROGATE_MASK == SURROGATE_BITS) {
            return if (text.isCanonicalFrom(index)) text else text.redecodeUnitRuns(index)
        }
        index++
    }
    return text
}

// Canonical text holds runs of unpaired units U+DC80..U+DCFF from which no well-formed UTF-8 sequence
// starts; a sequence never extends past its run, because the next character's first byte is ASCII or a
// lead byte, never a continuation byte, and a kept surrogate is not re-decoded.
private fun String.isCanonicalFrom(from: Int): Boolean {
    var index = from
    while (index < length) {
        val end = unitRunEnd(index)
        if (end > index) {
            if (unitRunStartsSequence(index, end)) return false
            index = end
        } else {
            index += pairOrUnitLength(index)
        }
    }
    return true
}

private fun String.redecodeUnitRuns(from: Int): String {
    val text = StringBuilder(length).append(this, 0, from)
    var index = from
    while (index < length) {
        val end = unitRunEnd(index)
        if (end > index) {
            text.append(decodeBytes(ByteArray(end - index) { (this[index + it].code - BYTE_UNIT_BASE).toByte() }))
            index = end
        } else {
            val next = index + pairOrUnitLength(index)
            text.append(this, index, next)
            index = next
        }
    }
    return text.toString()
}

// End of the run of units U+DC80..U+DCFF that starts at [start], or [start] when none does. A unit
// that a high surrogate before it pairs with is consumed by pairOrUnitLength and never starts a run.
private fun String.unitRunEnd(start: Int): Int {
    var end = start
    while (end < length && this[end].isByteOutsideUtf8()) end++
    return end
}

// 2 for a high surrogate followed by a low surrogate, 1 for any other unit.
private fun String.pairOrUnitLength(index: Int): Int =
    if (this[index].isHighSurrogate() && index + 1 < length && this[index + 1].isLowSurrogate()) 2 else 1

// Whether a well-formed UTF-8 sequence starts in the run of units [start, end); runs only on text
// that holds units.
private fun String.unitRunStartsSequence(
    start: Int,
    end: Int,
): Boolean = (start until end).any { from -> wellFormedLength(from, end) { this[it].code - BYTE_UNIT_BASE } != 0 }

/**
 * Encodes each character as UTF-8, each unpaired unit U+DC80..U+DCFF as its byte, and each other
 * unpaired surrogate as the three bytes of its generalized UTF-8 form.
 */
internal fun String.encodeBytes(): ByteArray {
    if (none { it.isSurrogate() }) return toByteArray(Charsets.UTF_8)
    var size = 0
    forEachByte { size++ }
    val bytes = ByteArray(size)
    var offset = 0
    forEachByte { bytes[offset++] = it.toByte() }
    return bytes
}

private inline fun String.forEachByte(emit: (Int) -> Unit) {
    var index = 0
    while (index < length) {
        val char = this[index]
        when {
            char.isByteOutsideUtf8() -> {
                emit(char.code - BYTE_UNIT_BASE)
            }

            char.isHighSurrogate() && index + 1 < length && this[index + 1].isLowSurrogate() -> {
                emitUtf8(Character.toCodePoint(char, this[index + 1]), emit)
                index++
            }

            else -> {
                emitUtf8(char.code, emit)
            }
        }
        index++
    }
}

// Emits the UTF-8 bytes of [point]; a surrogate code point takes the three-byte form.
private inline fun emitUtf8(
    point: Int,
    emit: (Int) -> Unit,
) {
    val continuations =
        when {
            point < ASCII_END -> 0
            point < TWO_BYTE_END -> 1
            point < SUPPLEMENTARY_START -> 2
            else -> 3
        }
    emit(LEAD_MARKERS[continuations] or (point shr continuations * PAYLOAD_BITS))
    for (shift in continuations - 1 downTo 0) {
        emit(CONTINUATION_MIN or ((point shr shift * PAYLOAD_BITS) and CONTINUATION_PAYLOAD))
    }
}

private fun ByteArray.isWellFormedUtf8(): Boolean {
    var index = 0
    while (index < size) {
        val length = wellFormedLength(index, size) { this[it].toInt() and BYTE_MASK }
        if (length == 0) return false
        index += length
    }
    return true
}

// Length of the well-formed UTF-8 sequence (Unicode Table 3-7) that starts at [start] and ends by
// [end], or 0 when none does; [byteAt] reads the byte 00..FF at a position.
private inline fun wellFormedLength(
    start: Int,
    end: Int,
    byteAt: (Int) -> Int,
): Int {
    val lead = byteAt(start)
    val length = sequenceLength(lead)
    if (length == 1) return 1
    if (length == 0 || start + length > end) return 0
    if (byteAt(start + 1) !in secondByteMin(lead)..secondByteMax(lead)) return 0
    for (offset in 2 until length) {
        if (byteAt(start + offset) !in CONTINUATION_MIN..CONTINUATION_MAX) return 0
    }
    return length
}

// Sequence length a lead byte announces; 0 for a continuation byte, C0, C1 and F5..FF.
private fun sequenceLength(lead: Int): Int =
    when {
        lead < ASCII_END -> 1
        lead < 0xC2 -> 0
        lead < 0xE0 -> 2
        lead < 0xF0 -> 3
        lead < 0xF5 -> 4
        else -> 0
    }

// The second-byte bounds of Table 3-7 exclude overlong forms (E0, F0), encoded surrogates (ED)
// and code points above U+10FFFF (F4).
private fun secondByteMin(lead: Int): Int =
    when (lead) {
        0xE0 -> 0xA0
        0xF0 -> 0x90
        else -> CONTINUATION_MIN
    }

private fun secondByteMax(lead: Int): Int =
    when (lead) {
        0xED -> 0x9F
        0xF4 -> 0x8F
        else -> CONTINUATION_MAX
    }

private inline fun codePointAt(
    start: Int,
    length: Int,
    byteAt: (Int) -> Int,
): Int {
    val leadPayload = byteAt(start) and (0x7F shr length)
    var point = if (length == 1) byteAt(start) else leadPayload
    for (offset in 1 until length) {
        point = (point shl PAYLOAD_BITS) or (byteAt(start + offset) and CONTINUATION_PAYLOAD)
    }
    return point
}
