package cobra.commons.value

/**
 * Provides utility extensions and functions for handling `Number`, `String`, `Boolean`, and other
 * primitive types within the custom value system.
 *
 * These utilities include:
 * - Converting between primitive types and custom representations like [NumVal], [StrVal], [BoolVal].
 * - Range checks for numeric types (e.g., `isInIntRange`).
 * - String and regex manipulations for [StrVal].
 * - General-purpose functions for handling and comparing [IPrimitiveVal] instances.
 */

import java.io.File
import java.math.BigDecimal
import java.nio.file.Path
import java.text.NumberFormat
import kotlin.io.path.pathString

private val BIG_LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE)
private val BIG_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE)

/**
 * Extension property to determine if a `Number` is within the range of `Long`.
 *
 * The range is inclusive of [Long.MIN_VALUE] and [Long.MAX_VALUE].
 *
 * @return `true` if the number is within the range of `Long`, `false` otherwise.
 */
val Number.isInLongRange: Boolean
    get() = BigDecimal(toString()) in BIG_LONG_MIN_VALUE..BIG_LONG_MAX_VALUE

/**
 * Extension property to determine if a `Number` is within the range of `Int`.
 *
 * The range is inclusive of [Int.MIN_VALUE] and [Int.MAX_VALUE].
 *
 * @return `true` if the number is within the range of `Int`, `false` otherwise.
 */
val Number.isInIntRange: Boolean
    get() = toLong().let { it >= Int.MIN_VALUE && it <= Int.MAX_VALUE }

/**
 * Extension property to determine if a `Number` is within the range of `Short`.
 *
 * The range is inclusive of [Short.MIN_VALUE] and [Short.MAX_VALUE].
 *
 * @return `true` if the number is within the range of `Short`, `false` otherwise.
 */
val Number.isInShortRange: Boolean
    get() = toLong().let { it >= Short.MIN_VALUE && it <= Short.MAX_VALUE }

/**
 * Extension property to determine if a `Number` is within the range of `Byte`.
 *
 * The range is inclusive of [Byte.MIN_VALUE] and [Byte.MAX_VALUE].
 *
 * @return `true` if the number is within the range of `Byte`, `false` otherwise.
 */
val Number.isInByteRange: Boolean
    get() = toLong().let { it >= Byte.MIN_VALUE && it <= Byte.MAX_VALUE }

private val numberFormatter = NumberFormat.getNumberInstance()

/**
 * Converts a [Number] (such as Int, Long, Double, etc.) to a [NumVal], which is a custom wrapper
 * for handling numeric values in a uniform way.
 *
 * This extension is useful when you need to handle various numeric types as a single [NumVal] type.
 *
 * @return A [NumVal] wrapping the current number.
 */
val Number.numVal: NumVal get() = NumVal(this)

/**
 * Attempts to convert a [String] into a [NumVal], determining whether the string represents
 * an integer, long, or floating-point number. The conversion process uses a number formatter
 * and determines the appropriate numeric type based on the content of the string.
 *
 * - If the string contains a decimal point, it is treated as a floating-point number.
 * - If the string represents a large integer outside the range of [Int], it is converted to [Long].
 * - Otherwise, it is converted to [Int].
 *
 * @return A [NumVal] based on the parsed number from the string.
 */
val String.numVal: NumVal
    get() {
        val number = numberFormatter.parse(this)
        if ("." in this) return number.numVal
        val longNum = number.toLong()
        return if (longNum < Int.MIN_VALUE || longNum > Int.MAX_VALUE) longNum.numVal
        else NumVal(number.toInt())
    }

/**
 * Converts a [String] to a [StrVal], a custom wrapper for handling string values.
 *
 * This extension is useful for converting strings to a uniform representation within the system.
 *
 * @return A [StrVal] wrapping the string value.
 */
val String.strVal: StrVal get() = StrVal(this)

val Char.strVal: StrVal get() = StrVal(this.toString())

/**
 * Converts a [Path] to a [StrVal], using the string representation of the file path.
 *
 * @return A [StrVal] wrapping the string representation of the path.
 */
val Path.strVal: StrVal get() = StrVal(this.pathString)

/**
 * Converts a [File] to a [StrVal], using the file's path as the string representation.
 *
 * @return A [StrVal] wrapping the file path.
 */
val File.strVal: StrVal get() = StrVal(this.path)

/**
 * Checks if this [String] starts with the string contained in the [StrVal].
 *
 * This extension function allows comparison between a regular string and a [StrVal].
 *
 * @param other The [StrVal] whose value is compared.
 * @return True if the current string starts with the value of [other], false otherwise.
 */
fun String.startsWith(other: StrVal): Boolean = startsWith(other.core)

/**
 * Converts a [StrVal] to a [Regex], escaping any special characters in the string and optionally
 * ignoring case sensitivity. Additionally, it replaces specific patterns such as `ANY`, `STR`,
 * `NUM`, and `BOOL` with their regex equivalents.
 *
 * @param doCaseIgnore Whether to ignore case in the regex.
 * @return A [Regex] object created from the [StrVal].
 */
fun StrVal.toRegex(doCaseIgnore: Boolean = false): Regex {
    val tarChars = ".^$*+?-()[]{}\\|".toSet()
    val sBuilder = StringBuilder() // the string buffer to create a new string
    core.forEach { sBuilder.append(if (it in tarChars) "\\$it" else "$it") }
    return sBuilder.toString()
        .replace(Unsure.ANY.core, ".*")
        .replace(Unsure.STR.core, ".*")
        .replace(Unsure.NUM.core, "\\d+")
        .replace(Unsure.BOOL.core, "(true|false)")
        .toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())
}

/**
 * Converts a [Boolean] to a [BoolVal], which is a custom wrapper for handling boolean values.
 * It returns [BoolVal.T] for true and [BoolVal.F] for false.
 *
 * @return A [BoolVal] representing the current boolean value.
 */
val Boolean.boolVal: BoolVal get() = if (this) BoolVal.T else BoolVal.F

/**
 * Converts any primitive value (or null) into its corresponding [IPrimitiveVal] representation.
 *
 * This is useful when handling values in a uniform way, converting them into the appropriate custom type:
 * - [NullVal] for null.
 * - [NumVal] for numbers.
 * - [StrVal] for strings.
 * - [BoolVal] for booleans.
 *
 * @return An [IPrimitiveVal] representing the current value.
 * @throws IllegalArgumentException If the value cannot be converted to an [IPrimitiveVal].
 */
val Any?.primitiveVal: IPrimitiveVal
    get() = when (this) {
        null -> NullVal
        is Number -> numVal
        is String -> strVal
        is Boolean -> boolVal
        is IPrimitiveVal -> this
        else -> throw IllegalArgumentException("Cannot convert $this to IPrimitiveVal")
    }

/**
 * Compares two [IPrimitiveVal] instances for ordering.
 *
 * This function supports comparison between the following types:
 * - [NumVal]: Compares their underlying numeric values by converting them to [Double].
 * - [StrVal]: Compares their string values using the standard string comparison.
 * - [BoolVal]: Compares their boolean values, where `false` is considered less than `true`.
 * - [NullVal]: Any two [NullVal] instances are considered equal.
 *
 * If the two values are of incompatible types, an [IllegalArgumentException] is thrown.
 *
 * @param other The other [IPrimitiveVal] to compare against.
 * @return An integer value representing the comparison result:
 * - `0` if the two values are equal.
 * - A negative value if `this` is less than `other`.
 * - A positive value if `this` is greater than `other`.
 * @throws IllegalArgumentException If the two values are of incompatible types (e.g., comparing a [NumVal] with a [StrVal]).
 */
operator fun IPrimitiveVal.compareTo(other: IPrimitiveVal): Int = when {
    this is NumVal && other is NumVal -> core.toDouble().compareTo(other.core.toDouble())
    this is StrVal && other is StrVal -> core.compareTo(other.core)
    this is BoolVal && other is BoolVal -> core.compareTo(other.core)
    this is NullVal && other is NullVal -> 0
    else -> throw IllegalArgumentException("Cannot compare $this and $other")
}
