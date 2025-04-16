package cobra.commons.value

/**
 * Represents a string value in the storage system.
 *
 * Provides utility methods for string manipulation and comparison.
 *
 * Example usage:
 * ```kotlin
 * val strVal = StrVal("Hello World")
 * println(strVal.uppercase()) // Outputs: StrVal{HELLO WORLD}
 * println(strVal.length) // Outputs: 11
 * ```
 * @property core The actual string value.
 *
 */
data class StrVal(override val core: String) : IPrimitiveVal {

    /**
     * Creates a `StrVal` initialized to an empty string.
     */
    constructor() : this("")

    /**
     * Determines whether this string starts with the specified [other] string.
     *
     * @param other The prefix to check for.
     * @return `true` if this string starts with [other], `false` otherwise.
     */
    fun startsWith(other: String): Boolean = core.startsWith(other)

    /**
     * Returns the substring after the first occurrence of [delimiter].
     *
     * @param delimiter The delimiter to split on.
     * @return The substring after [delimiter].
     */
    fun substringAfter(delimiter: String): String = core.substringAfter(delimiter)

    /**
     * Returns the substring before the first occurrence of [delimiter].
     *
     * @param delimiter The delimiter to split on.
     * @return The substring before [delimiter].
     */
    fun substringBefore(delimiter: String): String = core.substringBefore(delimiter)

    /**
     * Compares this string with another string [string], optionally ignoring case.
     *
     * @param string The string to compare to.
     * @param ignoreCase Whether to ignore case during comparison.
     * @return `true` if the strings are equal, `false` otherwise.
     */
    fun equals(string: String, ignoreCase: Boolean): Boolean = core.equals(string, ignoreCase)

    /**
     * Compares this [StrVal] with another [IPrimitiveVal], optionally ignoring case.
     *
     * @param value The [IPrimitiveVal] to compare to.
     * @param ignoreCase Whether to ignore case during comparison.
     * @return `true` if the values are equal, `false` otherwise.
     */
    fun equals(value: IPrimitiveVal, ignoreCase: Boolean): Boolean = core.equals(value.core.toString(), ignoreCase)

    /**
     * Converts the string to uppercase.
     *
     * @return A new [StrVal] with the string in uppercase.
     */
    fun uppercase(): StrVal = StrVal(core.uppercase())

    /**
     * Converts the string to lowercase.
     *
     * @return A new [StrVal] with the string in lowercase.
     */
    fun lowercase(): StrVal = StrVal(core.lowercase())

    /**
     * Trims leading and trailing whitespace from the string.
     *
     * @return A new [StrVal] with trimmed whitespace.
     */
    fun trim(): StrVal = StrVal(core.trim())

    /**
     * Determines whether this string contains the specified [substring].
     *
     * @param substring The substring to check for.
     * @return `true` if the string contains [substring], `false` otherwise.
     */
    fun contains(substring: String): Boolean = core.contains(substring)

    /**
     * Returns the length of the string.
     *
     * @return The number of characters in the string.
     */
    val length: Int get() = core.length

    /**
     * Retrieves the character at the specified [index].
     *
     * Negative indices access characters from the end of the string.
     *
     * @param index The position of the character to retrieve.
     * @return The character at the specified position.
     */
    operator fun get(index: Int) = if (index < 0) core[core.length + index] else core[index]

    /**
     * Retrieves the character at the specified [index], represented as a [NumVal].
     *
     * @param index The [NumVal] representing the position of the character to retrieve.
     * @return The character at the specified position.
     */
    operator fun get(index: NumVal) = get(index.core.toInt())

    override fun toString(): String = "StrVal{$core}"
}