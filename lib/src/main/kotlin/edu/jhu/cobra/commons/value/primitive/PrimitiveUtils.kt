package edu.jhu.cobra.commons.value

import java.io.File
import java.math.BigDecimal
import java.nio.file.Path
import java.text.NumberFormat
import kotlin.io.path.pathString

private val BIG_LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE)
private val BIG_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE)

/**
 * Checks if this number is within the valid range of a [Long].
 *
 * This property converts the number to a [BigDecimal] and checks if it falls within
 * the inclusive range of [Long.MIN_VALUE] to [Long.MAX_VALUE].
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
val Number.isInLongRange: Boolean
    get() = BigDecimal(toString()) in BIG_LONG_MIN_VALUE..BIG_LONG_MAX_VALUE

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
val Number.isInIntRange: Boolean
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
val Number.isInShortRange: Boolean
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
val Number.isInByteRange: Boolean
    get() = toLong().let { it >= Byte.MIN_VALUE && it <= Byte.MAX_VALUE }

private val numberFormatter = NumberFormat.getNumberInstance()

/**
 * Converts this number to a [NumVal] representation.
 *
 * This property creates a new [NumVal] instance that wraps the current number,
 * preserving its value while providing COBRA's value type functionality.
 *
 * Example:
 * ```kotlin
 * val intNum = 42
 * val numVal = intNum.numVal // Creates NumVal(42)
 * 
 * val doubleNum = 3.14
 * val doubleVal = doubleNum.numVal // Creates NumVal(3.14)
 * ```
 *
 * @return A [NumVal] containing this number
 */
val Number.numVal: NumVal get() = NumVal(this)

/**
 * Parses this string as a number and converts it to a [NumVal].
 *
 * This property attempts to parse the string into the most appropriate numeric type:
 * - If the string contains a decimal point, it's parsed as a floating-point number
 * - If the value exceeds [Int] range, it's parsed as a [Long]
 * - Otherwise, it's parsed as an [Int]
 *
 * Example:
 * ```kotlin
 * "42".numVal // Creates NumVal(42) as Int
 * "3.14".numVal // Creates NumVal(3.14) as Double
 * "9999999999".numVal // Creates NumVal(9999999999) as Long
 * ```
 *
 * @return A [NumVal] containing the parsed number
 * @throws ParseException if the string cannot be parsed as a number
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
val String.strVal: StrVal get() = StrVal(this)

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
val Char.strVal: StrVal get() = StrVal(this.toString())

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
val Path.strVal: StrVal get() = StrVal(this.pathString)

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
val File.strVal: StrVal get() = StrVal(this.path)

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
fun String.startsWith(other: StrVal): Boolean = startsWith(other.core)

/**
 * Converts this [StrVal] to a [Regex] pattern, with special character escaping and pattern substitution.
 *
 * This function:
 * 1. Escapes special regex characters
 * 2. Replaces COBRA-specific patterns with their regex equivalents:
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
fun StrVal.toRegex(doCaseIgnore: Boolean = false): Regex {
    val tarChars = ".^$*+?-()[]{}\\|".toSet()
    val sBuilder = StringBuilder()
    core.forEach { sBuilder.append(if (it in tarChars) "\\$it" else "$it") }
    return sBuilder.toString()
        .replace(Unsure.ANY.core, ".*")
        .replace(Unsure.STR.core, ".*")
        .replace(Unsure.NUM.core, "\\d+")
        .replace(Unsure.BOOL.core, "(true|false)")
        .toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())
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
fun Unsure.toRegex(doCaseIgnore: Boolean = false) = when (this) {
    Unsure.ANY -> ".*"
    Unsure.STR -> ".*"
    Unsure.NUM -> "\\d+"
    Unsure.BOOL -> "(true|false)"
}.toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())

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
val Boolean.boolVal: BoolVal get() = if (this) BoolVal.T else BoolVal.F

/**
 * Converts any value to its corresponding [IPrimitiveVal] representation.
 *
 * This property handles the conversion of various types to their COBRA primitive value equivalents:
 * - `null` → [NullVal]
 * - [Number] → [NumVal]
 * - [String] → [StrVal]
 * - [Boolean] → [BoolVal]
 * - [IPrimitiveVal] → returns as is
 *
 * Example:
 * ```kotlin
 * null.primitiveVal // Returns NullVal
 * 42.primitiveVal // Returns NumVal(42)
 * "Hello".primitiveVal // Returns StrVal("Hello")
 * true.primitiveVal // Returns BoolVal.T
 * ```
 *
 * @return An [IPrimitiveVal] representing this value
 * @throws IllegalArgumentException if the value cannot be converted to an [IPrimitiveVal]
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
 * This operator function implements natural ordering for COBRA primitive values:
 * - [NumVal]: Compares numeric values after converting to [Double]
 * - [StrVal]: Uses standard string comparison
 * - [BoolVal]: `false` < `true`
 * - [NullVal]: All [NullVal] instances are equal
 *
 * Example:
 * ```kotlin
 * NumVal(1) < NumVal(2) // true
 * StrVal("a") < StrVal("b") // true
 * BoolVal.F < BoolVal.T // true
 * NullVal == NullVal // true
 * ```
 *
 * @param other The [IPrimitiveVal] to compare with
 * @return A negative number if this value is less than [other],
 *         zero if they are equal,
 *         a positive number if this value is greater than [other]
 * @throws IllegalArgumentException if comparing incompatible types
 */
operator fun IPrimitiveVal.compareTo(other: IPrimitiveVal): Int = when {
    this is NumVal && other is NumVal -> core.toDouble().compareTo(other.core.toDouble())
    this is StrVal && other is StrVal -> core.compareTo(other.core)
    this is BoolVal && other is BoolVal -> core.compareTo(other.core)
    this is NullVal && other is NullVal -> 0
    else -> throw IllegalArgumentException("Cannot compare $this and $other")
}
