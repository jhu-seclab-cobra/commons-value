package edu.jhu.cobra.commons.value

import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Converts this string to a [StrVal].
 *
 * @return A [StrVal] containing this string
 */
public val String.strVal: StrVal get() = StrVal(this)

/**
 * Converts this character to a [StrVal].
 *
 * @return A [StrVal] containing this character as a one-character string
 */
public val Char.strVal: StrVal get() = StrVal(this.toString())

/**
 * Converts this file path to a [StrVal].
 *
 * @return A [StrVal] containing the string representation of this path
 */
public val Path.strVal: StrVal get() = StrVal(this.pathString)

/**
 * Converts this file to a [StrVal].
 *
 * @return A [StrVal] containing the path of this file
 */
public val File.strVal: StrVal get() = StrVal(this.path)

/**
 * Checks if this string starts with the string contained in the specified [StrVal].
 *
 * @param other The [StrVal] to check against
 * @return `true` if this string starts with the content of [other], `false` otherwise
 */
public fun String.startsWith(other: StrVal): Boolean = startsWith(other.core)

/**
 * Converts this boolean to a [BoolVal].
 *
 * @return [BoolVal.T] if this boolean is `true`, [BoolVal.F] if `false`
 */
public val Boolean.boolVal: BoolVal get() = if (this) BoolVal.T else BoolVal.F

/**
 * Converts any value to its corresponding [IPrimitiveVal]:
 * - `null` → [NullVal]
 * - [Long], [Int], [Short], [Byte] → [IntVal]
 * - [Double], [Float] → [FloatVal]
 * - [String] → [StrVal]
 * - [Char] → [StrVal]
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
            is Char -> strVal
            is Boolean -> boolVal
            is IPrimitiveVal -> this
            else -> throw IllegalArgumentException("Cannot convert $this to IPrimitiveVal")
        }
