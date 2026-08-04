package edu.jhu.cobra.commons.value

import java.io.File
import java.math.BigDecimal
import java.nio.file.Path
import kotlin.io.path.pathString

private val BIG_LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE)
private val BIG_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE)

/**
 * Checks if this number is within the valid range of a [Long].
 *
 * For types that always fit ([Byte], [Short], [Int], [Long]), returns `true` immediately.
 * For other types, converts to [BigDecimal] and checks against [Long.MIN_VALUE]..[Long.MAX_VALUE].
 *
 * Example:
 * ```kotlin
 * val num = BigDecimal("9223372036854775807") // Long.MAX_VALUE
 * println(num.isInLongRange) // true
 *
 * val tooBig = BigDecimal("9223372036854775808")
 * println(tooBig.isInLongRange) // false
 * ```
 *
 * @return `true` if the number can be represented as a [Long], `false` otherwise
 */
public val Number.isInLongRange: Boolean
    get() =
        when (this) {
            is Byte, is Short, is Int, is Long -> true
            else -> BigDecimal(toString()) in BIG_LONG_MIN_VALUE..BIG_LONG_MAX_VALUE
        }

/**
 * Checks if this number is within the valid range of an [Int].
 *
 * This property converts the number to a [Long] and checks if it falls within
 * the inclusive range of [Int.MIN_VALUE] to [Int.MAX_VALUE].
 *
 * Example:
 * ```kotlin
 * val num = 2147483647L // Int.MAX_VALUE
 * println(num.isInIntRange) // true
 *
 * val tooBig = 2147483648L
 * println(tooBig.isInIntRange) // false
 * ```
 *
 * @return `true` if the number can be represented as an [Int], `false` otherwise
 */
public val Number.isInIntRange: Boolean
    get() = toLong().let { it >= Int.MIN_VALUE && it <= Int.MAX_VALUE }

/**
 * Checks if this number is within the valid range of a [Short].
 *
 * This property converts the number to a [Long] and checks if it falls within
 * the inclusive range of [Short.MIN_VALUE] to [Short.MAX_VALUE].
 *
 * Example:
 * ```kotlin
 * val num = 32767 // Short.MAX_VALUE
 * println(num.isInShortRange) // true
 *
 * val tooBig = 32768
 * println(tooBig.isInShortRange) // false
 * ```
 *
 * @return `true` if the number can be represented as a [Short], `false` otherwise
 */
public val Number.isInShortRange: Boolean
    get() = toLong().let { it >= Short.MIN_VALUE && it <= Short.MAX_VALUE }

/**
 * Checks if this number is within the valid range of a [Byte].
 *
 * This property converts the number to a [Long] and checks if it falls within
 * the inclusive range of [Byte.MIN_VALUE] to [Byte.MAX_VALUE].
 *
 * Example:
 * ```kotlin
 * val num = 127 // Byte.MAX_VALUE
 * println(num.isInByteRange) // true
 *
 * val tooBig = 128
 * println(tooBig.isInByteRange) // false
 * ```
 *
 * @return `true` if the number can be represented as a [Byte], `false` otherwise
 */
public val Number.isInByteRange: Boolean
    get() = toLong().let { it >= Byte.MIN_VALUE && it <= Byte.MAX_VALUE }

/**
 * Converts this [Long] to an [IntVal] representation.
 *
 * @return An [IntVal] containing this value
 */
public val Long.intVal: IntVal get() = IntVal(this)

/**
 * Converts this [Int] to an [IntVal] representation.
 *
 * @return An [IntVal] containing this value widened to [Long]
 */
public val Int.intVal: IntVal get() = IntVal(this.toLong())

/**
 * Converts this [Short] to an [IntVal] representation.
 *
 * @return An [IntVal] containing this value widened to [Long]
 */
public val Short.intVal: IntVal get() = IntVal(this.toLong())

/**
 * Converts this [Byte] to an [IntVal] representation.
 *
 * @return An [IntVal] containing this value widened to [Long]
 */
public val Byte.intVal: IntVal get() = IntVal(this.toLong())

/**
 * Converts this [Double] to a [FloatVal] representation.
 *
 * @return A [FloatVal] containing this value
 */
public val Double.floatVal: FloatVal get() = FloatVal(this)

/**
 * Converts this [Float] to a [FloatVal] representation.
 *
 * @return A [FloatVal] containing this value widened to [Double]
 */
public val Float.floatVal: FloatVal get() = FloatVal(this.toDouble())

/**
 * Parses this string as an integer and converts it to an [IntVal].
 *
 * @return An [IntVal] containing the parsed value
 * @throws NumberFormatException if the string cannot be parsed as an integer
 */
public val String.intVal: IntVal
    get() {
        val longNum = toLongOrNull() ?: throw NumberFormatException("Cannot parse '$this' as integer")
        return IntVal(longNum)
    }

/**
 * Parses this string as a floating-point number and converts it to a [FloatVal].
 *
 * @return A [FloatVal] containing the parsed value
 * @throws NumberFormatException if the string cannot be parsed as a float
 */
public val String.floatVal: FloatVal
    get() {
        val doubleNum = toDoubleOrNull() ?: throw NumberFormatException("Cannot parse '$this' as float")
        return FloatVal(doubleNum)
    }

/**
 * Converts this string to a [StrVal] representation.
 *
 * This property creates a new [StrVal] instance that wraps the current string,
 * providing COBRA's value type functionality.
 *
 * Example:
 * ```kotlin
 * val str = "Hello"
 * val strVal = str.strVal // Creates StrVal("Hello")
 * ```
 *
 * @return A [StrVal] containing this string
 */
public val String.strVal: StrVal get() = StrVal(this)

/**
 * Converts this character to a [StrVal] representation.
 *
 * This property creates a new [StrVal] instance containing the string representation
 * of the character.
 *
 * Example:
 * ```kotlin
 * val char = 'A'
 * val strVal = char.strVal // Creates StrVal("A")
 * ```
 *
 * @return A [StrVal] containing this character as a string
 */
public val Char.strVal: StrVal get() = StrVal(this.toString())

/**
 * Converts this file path to a [StrVal] representation.
 *
 * This property creates a new [StrVal] instance containing the string representation
 * of the path.
 *
 * Example:
 * ```kotlin
 * val path = Path.of("/home/user/file.txt")
 * val strVal = path.strVal // Creates StrVal("/home/user/file.txt")
 * ```
 *
 * @return A [StrVal] containing the string representation of this path
 */
public val Path.strVal: StrVal get() = StrVal(this.pathString)

/**
 * Converts this file to a [StrVal] representation.
 *
 * This property creates a new [StrVal] instance containing the path of the file.
 *
 * Example:
 * ```kotlin
 * val file = File("/home/user/file.txt")
 * val strVal = file.strVal // Creates StrVal("/home/user/file.txt")
 * ```
 *
 * @return A [StrVal] containing the path of this file
 */
public val File.strVal: StrVal get() = StrVal(this.path)

/**
 * Checks if this string starts with the string contained in the specified [StrVal].
 *
 * This function provides a convenient way to compare a regular string with a [StrVal]'s content.
 *
 * Example:
 * ```kotlin
 * val prefix = StrVal("Hello")
 * "Hello, World!".startsWith(prefix) // Returns true
 * "Hi, World!".startsWith(prefix) // Returns false
 * ```
 *
 * @param other The [StrVal] to check against
 * @return `true` if this string starts with the content of [other], `false` otherwise
 */
public fun String.startsWith(other: StrVal): Boolean = startsWith(other.core)

// Characters that carry special meaning in regex syntax; each is escaped so pattern text matches literally.
private val REGEX_METACHARACTERS = ".^$*+?-()[]{}\\|".toSet()

// Escapes every regex metacharacter so the text matches itself literally inside a pattern.
private fun escapeRegexChars(text: String): String =
    buildString(text.length) {
        text.forEach { append(if (it in REGEX_METACHARACTERS) "\\$it" else "$it") }
    }

// Regex fragment each Unsure placeholder stands for.
private val Unsure.regexPattern: String
    get() =
        when (this) {
            Unsure.ANY -> ".*"
            Unsure.STR -> ".*"
            Unsure.NUM -> "\\d+"
            Unsure.BOOL -> "(true|false)"
        }

/**
 * Converts this [StrVal] to a [Regex] pattern, with special character escaping and pattern substitution.
 *
 * This function:
 * 1. Escapes special regex characters
 * 2. Replaces COBRA-specific placeholders — both the bare core spelling (e.g. `__NumVal__`) and the
 *    `Unsure{...}` rendering produced by [Unsure.toString] — with their regex equivalents:
 *    - `Unsure.ANY` → `.*`
 *    - `Unsure.STR` → `.*`
 *    - `Unsure.NUM` → `\d+`
 *    - `Unsure.BOOL` → `(true|false)`
 *
 * Example:
 * ```kotlin
 * val strVal = StrVal("Hello.*")
 * val regex = strVal.toRegex() // Creates Regex("Hello\\.\\*")
 *
 * val pattern = Unsure.ANY.strVal
 * val numRegex = pattern.toRegex() // Creates Regex(".*")
 * ```
 *
 * @param doCaseIgnore Whether to make the regex case-insensitive
 * @return A [Regex] object based on this [StrVal]'s content
 */
public fun StrVal.toRegex(doCaseIgnore: Boolean = false): Regex {
    // Substitution runs on the escaped text, so each placeholder is matched in its escaped rendering.
    // The "Unsure{...}" spelling is replaced before the bare core it contains as a substring.
    val substituted =
        Unsure.entries.fold(escapeRegexChars(core)) { pattern, unsure ->
            pattern
                .replace(escapeRegexChars(unsure.toString()), unsure.regexPattern)
                .replace(unsure.core, unsure.regexPattern)
        }
    return substituted.toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())
}

/**
 * Converts the current [Unsure] instance to its corresponding regular expression pattern as a string.
 *
 * - [Unsure.ANY] and [Unsure.STR] are represented as `.*`, allowing matching of any string.
 * - [Unsure.NUM] is represented as `\\d+`, allowing matching of one or more numeric digits.
 * - [Unsure.BOOL] is represented as `(true|false)`, allowing matching of boolean values `true` or `false`.
 *
 * @return A string containing the regular expression pattern corresponding to the current [Unsure] type.
 */
public fun Unsure.toRegex(doCaseIgnore: Boolean = false): Regex =
    regexPattern.toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())

/**
 * Converts this boolean to a [BoolVal] representation.
 *
 * This property creates a new [BoolVal] instance that wraps the current boolean value,
 * using the singleton instances [BoolVal.T] for `true` and [BoolVal.F] for `false`.
 *
 * Example:
 * ```kotlin
 * val t = true.boolVal // Returns BoolVal.T
 * val f = false.boolVal // Returns BoolVal.F
 * ```
 *
 * @return [BoolVal.T] if this boolean is `true`, [BoolVal.F] if `false`
 */
public val Boolean.boolVal: BoolVal get() = if (this) BoolVal.T else BoolVal.F

/**
 * Converts any value to its corresponding [IPrimitiveVal] representation.
 *
 * This property handles the conversion of various types to their COBRA primitive value equivalents:
 * - `null` → [NullVal]
 * - [Long], [Int], [Short], [Byte] → [IntVal]
 * - [Double], [Float] → [FloatVal]
 * - [String] → [StrVal]
 * - [Boolean] → [BoolVal]
 * - [IPrimitiveVal] → returns as is
 *
 * @return An [IPrimitiveVal] representing this value
 * @throws IllegalArgumentException if the value cannot be converted to an [IPrimitiveVal]
 */
public val Any?.primitiveVal: IPrimitiveVal
    get() =
        when (this) {
            null -> NullVal
            is Long -> intVal
            is Int -> intVal
            is Short -> intVal
            is Byte -> intVal
            is Double -> floatVal
            is Float -> floatVal
            is String -> strVal
            is Boolean -> boolVal
            is IPrimitiveVal -> this
            else -> throw IllegalArgumentException("Cannot convert $this to IPrimitiveVal")
        }

/**
 * Compares two [IPrimitiveVal] instances for ordering.
 *
 * This operator function implements natural ordering for COBRA primitive values:
 * - [IntVal]: Compares long values directly
 * - [FloatVal]: Compares double values directly
 * - [StrVal]: Uses standard string comparison
 * - [BoolVal]: `false` < `true`
 * - [NullVal]: All [NullVal] instances are equal
 *
 * @param other The [IPrimitiveVal] to compare with
 * @return A negative number if this value is less than [other],
 *         zero if they are equal,
 *         a positive number if this value is greater than [other]
 * @throws IllegalArgumentException if comparing incompatible types
 */
public operator fun IPrimitiveVal.compareTo(other: IPrimitiveVal): Int =
    when {
        this is StrVal && other is StrVal -> core.compareTo(other.core)
        this is BoolVal && other is BoolVal -> core.compareTo(other.core)
        this is NullVal && other is NullVal -> 0
        else ->
            compareNumeric(this, other)
                ?: throw IllegalArgumentException("Cannot compare $this and $other")
    }

// Cross-type numeric ordering; null when the pair is not a numeric combination.
private fun compareNumeric(
    a: IPrimitiveVal,
    b: IPrimitiveVal,
): Int? =
    when {
        a is IntVal && b is IntVal -> a.core.compareTo(b.core)
        a is IntVal && b is FloatVal -> a.core.toDouble().compareTo(b.core)
        a is FloatVal && b is IntVal -> a.core.compareTo(b.core.toDouble())
        a is FloatVal && b is FloatVal -> a.core.compareTo(b.core)
        else -> null
    }
