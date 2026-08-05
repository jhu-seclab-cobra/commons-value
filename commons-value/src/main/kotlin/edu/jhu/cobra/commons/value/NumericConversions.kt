package edu.jhu.cobra.commons.value

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
