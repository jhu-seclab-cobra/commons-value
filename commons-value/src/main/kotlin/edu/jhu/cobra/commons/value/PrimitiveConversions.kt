package edu.jhu.cobra.commons.value

import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

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
