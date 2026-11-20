package edu.jhu.cobra.commons.value

/**
 * Represents a string value that stands for a byte sequence.
 *
 * Provides utility methods for string manipulation and comparison; they are text operations on [core],
 * and a byte operation reads [toByteArray].
 */
public class StrVal : IPrimitiveVal {
    /**
     * The canonical text of the string's bytes: each well-formed UTF-8 sequence is its character, and
     * each other byte `XX` in 80..FF is the unpaired unit U+DCXX.
     */
    override val core: String

    /**
     * Creates a `StrVal` of the bytes [core] stands for.
     *
     * @param core The string content; the constructor stores its canonical form.
     */
    public constructor(core: String) {
        this.core = canonical(core)
    }

    /**
     * Creates a `StrVal` initialized to an empty string.
     */
    public constructor() : this("")

    // The decoding of bytes is canonical already, so this constructor skips the canonical scan.
    private constructor(bytes: ByteArray) {
        core = decodeBytes(bytes)
    }

    internal companion object {
        /** The `StrVal` whose bytes are [bytes], decoded once. */
        internal fun ofBytes(bytes: ByteArray): StrVal = StrVal(bytes)
    }

    /**
     * Determines whether this string starts with the specified [other] string.
     *
     * @param other The prefix to check for.
     * @return `true` if this string starts with [other], `false` otherwise.
     */
    public fun startsWith(other: String): Boolean = core.startsWith(other)

    /**
     * Returns the substring after the first occurrence of [delimiter].
     *
     * @param delimiter The delimiter to split on.
     * @return The substring after [delimiter].
     */
    public fun substringAfter(delimiter: String): String = core.substringAfter(delimiter)

    /**
     * Returns the substring before the first occurrence of [delimiter].
     *
     * @param delimiter The delimiter to split on.
     * @return The substring before [delimiter].
     */
    public fun substringBefore(delimiter: String): String = core.substringBefore(delimiter)

    /**
     * Compares this string with another string [string], optionally ignoring case.
     *
     * @param string The string to compare to.
     * @param ignoreCase Whether to ignore case during comparison.
     * @return `true` if the strings are equal, `false` otherwise.
     */
    public fun equals(
        string: String,
        ignoreCase: Boolean,
    ): Boolean = core.equals(string, ignoreCase)

    /**
     * Compares this [StrVal] with another [IPrimitiveVal], optionally ignoring case.
     *
     * A [NullVal] is never equal: the absence of a value has no textual rendering.
     *
     * @param value The [IPrimitiveVal] to compare to.
     * @param ignoreCase Whether to ignore case during comparison.
     * @return `true` if the values are equal, `false` otherwise.
     */
    public fun equals(
        value: IPrimitiveVal,
        ignoreCase: Boolean,
    ): Boolean = value !is NullVal && core.equals(value.core.toString(), ignoreCase)

    /**
     * Converts the string to uppercase.
     *
     * @return A new [StrVal] with the string in uppercase.
     */
    public fun uppercase(): StrVal = StrVal(core.uppercase())

    /**
     * Converts the string to lowercase.
     *
     * @return A new [StrVal] with the string in lowercase.
     */
    public fun lowercase(): StrVal = StrVal(core.lowercase())

    /**
     * Trims leading and trailing whitespace from the string.
     *
     * @return A new [StrVal] with trimmed whitespace.
     */
    public fun trim(): StrVal = StrVal(core.trim())

    /**
     * Determines whether this string contains the specified [substring].
     *
     * @param substring The substring to check for.
     * @return `true` if the string contains [substring], `false` otherwise.
     */
    public fun contains(substring: String): Boolean = core.contains(substring)

    /**
     * Returns the length of the string.
     *
     * @return The number of characters in the string.
     */
    public val length: Int get() = core.length

    /**
     * Retrieves the character at the specified [index].
     *
     * Negative indices access characters from the end of the string.
     *
     * @param index The position of the character to retrieve.
     * @return The character at the specified position.
     */
    public operator fun get(index: Int): Char = if (index < 0) core[core.length + index] else core[index]

    /**
     * Retrieves the character at the specified [index], represented as an [IntVal].
     *
     * An index outside the [Int] range is out of bounds; it is never truncated.
     *
     * @param index The [IntVal] representing the position of the character to retrieve.
     * @return The character at the specified position.
     * @throws IndexOutOfBoundsException if the index does not address a character.
     */
    public operator fun get(index: IntVal): Char {
        val position = index.core
        if (position < Int.MIN_VALUE || position > Int.MAX_VALUE) {
            throw IndexOutOfBoundsException("Index $position out of bounds for length ${core.length}")
        }
        return get(position.toInt())
    }

    override fun equals(other: Any?): Boolean = this === other || (other is StrVal && core == other.core)

    override fun hashCode(): Int = core.hashCode()

    override fun toString(): String = "StrVal{$core}"
}

/**
 * Returns the byte sequence this string stands for: the UTF-8 bytes of each character, and byte `XX`
 * for each unpaired unit U+DCXX. [ByteArray.strVal] is the inverse.
 *
 * @return The bytes of this string
 */
public fun StrVal.toByteArray(): ByteArray = core.encodeBytes()

/**
 * Returns the index of the first unpaired unit U+DC80..U+DCFF at or after [startIndex], or -1 when
 * there is none.
 *
 * A high surrogate followed by a low surrogate is one pair, so the low unit DC80 of U+1F480
 * (D83D DC80) is not returned when [startIndex] is a character's first unit. Applied to a
 * [StrVal.core] from such an index, the result is the next unit that stands for a byte outside UTF-8.
 *
 * @param startIndex The index of a character's first unit; a negative index reads as 0
 * @return The index of the unit, or -1
 */
public fun String.indexOfByteOutsideUtf8(startIndex: Int = 0): Int {
    var index = startIndex.coerceAtLeast(0)
    while (index < length) {
        val char = this[index]
        if (char.isByteOutsideUtf8()) return index
        index += if (char.isHighSurrogate() && index + 1 < length && this[index + 1].isLowSurrogate()) 2 else 1
    }
    return -1
}
