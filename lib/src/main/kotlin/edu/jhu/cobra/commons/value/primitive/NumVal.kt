package edu.jhu.cobra.commons.value

/**
 * Represents a numeric value in the storage system.
 *
 * This class encapsulates numeric values and provides utility methods to work with
 * various numeric types such as [Int], [Long], [Float], and [Double].
 *
 * @property core The actual numeric value stored in this instance.
 *
 * Example usage:
 * ```kotlin
 * val numVal = NumVal(42)
 * println(numVal + 8) // Outputs: NumVal{50.0}
 * println(numVal.toInt()) // Outputs: 42
 * ```
 */
data class NumVal(override val core: Number) : IPrimitiveVal {

    companion object {
        /**
         * Truncates the given `NumVal` to the smallest possible integer type that can
         * fully represent its value without precision loss.
         *
         * Example usage:
         * ```kotlin
         * val numVal = NumVal(123456L)
         * val truncated = NumVal.truncate(numVal)
         * println(truncated) // Outputs: NumVal{123456}
         * ```
         *
         * @param numVal The `NumVal` instance to be truncated.
         * @return A new `NumVal` truncated to the appropriate integer type.
         */
        fun truncate(numVal: NumVal): NumVal {
            val inner = numVal.core.toLong()
            if (inner.isInByteRange) return inner.toByte().numVal
            if (inner.isInShortRange) return inner.toShort().numVal
            if (inner.isInIntRange) return inner.toInt().numVal
            return inner.numVal
        }
    }

    /**
     * Default constructor initializing the value to `0`.
     *
     * Example usage:
     * ```kotlin
     * val defaultNumVal = NumVal()
     * println(defaultNumVal) // Outputs: NumVal{0}
     * ```
     */
    constructor() : this(0)

    /**
     * Converts this number to an integer.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.5)
     * println(numVal.toInt()) // Outputs: 42
     * ```
     *
     * @return The integer representation of this number.
     */
    fun toInt(): Int = core.toInt()

    /**
     * Converts this number to a double.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.toDouble()) // Outputs: 42.0
     * ```
     *
     * @return The double representation of this number.
     */
    fun toDouble(): Double = core.toDouble()

    /**
     * Converts a [NumVal] to a [Float]. This is helpful when operations require lower
     * precision floating-point arithmetic.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.toFloat()) // Outputs: 42.0
     * ```
     *
     * @return The float representation of this number.
     */
    fun toFloat(): Float = core.toFloat()

    /**
     * Converts this number to a long.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.toLong()) // Outputs: 42
     * ```
     *
     * @return The long representation of this number.
     */
    fun toLong(): Long = core.toLong()

    /**
     * Converts a [NumVal] to a [Short]. Useful when you need to represent the numeric value
     * as a 16-bit integer.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.toShort()) // Outputs: 42
     * ```
     *
     * @return The short representation of this number.
     */
    fun toShort(): Short = core.toShort()

    /**
     * Checks if the current [NumVal] represents an [Int].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.isInt) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is an [Int], false otherwise.
     */
    val isInt get() = core is Int

    /**
     * Checks if the current [NumVal] represents a [Long].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42L)
     * println(numVal.isLong) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is a [Long], false otherwise.
     */
    val isLong get() = core is Long

    /**
     * Checks if the current [NumVal] represents a [Short].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.toShort())
     * println(numVal.isShort) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is a [Short], false otherwise.
     */
    val isShort get() = core is Short

    /**
     * Checks if the current [NumVal] represents a [Byte].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.toByte())
     * println(numVal.isByte) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is a [Byte], false otherwise.
     */
    val isByte get() = core is Byte

    /**
     * Checks if the current [NumVal] represents a [Float].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.0f)
     * println(numVal.isFloat) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is a [Float], false otherwise.
     */
    val isFloat get() = core is Float

    /**
     * Checks if the current [NumVal] represents a [Double].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.0)
     * println(numVal.isDouble) // Outputs: true
     * ```
     *
     * @return True if the underlying numeric value is a [Double], false otherwise.
     */
    val isDouble get() = core is Double

    /**
     * Checks if the current [NumVal] is a primitive integer type.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal.isPrimitiveIntegerType) // Outputs: true
     * ```
     *
     * @return True if the value is a primitive integer type, false otherwise.
     */
    val isPrimitiveIntegerType get() = core is Int || core is Long || core is Short || core is Byte

    /**
     * Checks if the current [NumVal] is a primitive floating-point type.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42.0)
     * println(numVal.isPrimitiveFloatingType) // Outputs: true
     * ```
     *
     * @return True if the value is a floating-point type, false otherwise.
     */
    val isPrimitiveFloatingType get() = core is Float || core is Double

    /**
     * Returns the string representation of this [NumVal].
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal) // Outputs: NumVal{42}
     * ```
     *
     * @return The string representation of this [NumVal].
     */
    override fun toString(): String = "NumVal{$core}"

    /**
     * Compares this [NumVal] with an integer value.
     *
     * Example usage:
     * ```kotlin
     * val numVal = NumVal(42)
     * println(numVal > 40) // Outputs: true
     * ```
     *
     * @param other The integer to compare with.
     * @return A comparison result.
     */
    operator fun compareTo(other: Int): Int = core.toInt().compareTo(other)
}