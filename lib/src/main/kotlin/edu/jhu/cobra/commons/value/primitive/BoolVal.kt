package edu.jhu.cobra.commons.value

/**
 * Represents a boolean value in the storage system.
 *
 * This class encapsulates a boolean value and provides utility functions for common boolean operations.
 *
 * @property core The actual boolean value encapsulated by this instance.
 *
 * Example usage:
 * ```kotlin
 * val boolVal = BoolVal(true)
 * println(!boolVal) // Outputs: BoolVal{false}
 * println(boolVal and BoolVal(false)) // Outputs: BoolVal{false}
 * println(boolVal.isTrue()) // Outputs: true
 * ```
 */
data class BoolVal(override val core: Boolean) : IPrimitiveVal {

    companion object {
        /**
         * A constant instance representing the boolean value `true`.
         *
         * Example usage:
         * ```kotlin
         * println(BoolVal.T) // Outputs: BoolVal{true}
         * ```
         */
        val T = BoolVal(true)

        /**
         * A constant instance representing the boolean value `false`.
         *
         * Example usage:
         * ```kotlin
         * println(BoolVal.F) // Outputs: BoolVal{false}
         * ```
         */
        val F = BoolVal(false)
    }

    /**
     * Default constructor initializing to `false`.
     *
     * Example usage:
     * ```kotlin
     * val defaultBool = BoolVal()
     * println(defaultBool) // Outputs: BoolVal{false}
     * ```
     */
    constructor() : this(false)

    /**
     * Checks if the boolean value is `true`.
     *
     * Example usage:
     * ```kotlin
     * val boolVal = BoolVal(true)
     * println(boolVal.isTrue()) // Outputs: true
     * ```
     *
     * @return `true` if the value is `true`, `false` otherwise.
     */
    fun isTrue(): Boolean = core

    /**
     * Checks if the boolean value is `false`.
     *
     * Example usage:
     * ```kotlin
     * val boolVal = BoolVal(false)
     * println(boolVal.isFalse()) // Outputs: true
     * ```
     *
     * @return `true` if the value is `false`, `false` otherwise.
     */
    fun isFalse(): Boolean = !core

    /**
     * Provides a string representation of the boolean value.
     *
     * Example usage:
     * ```kotlin
     * val boolVal = BoolVal(true)
     * println(boolVal) // Outputs: BoolVal{true}
     * ```
     *
     * @return A string in the format "BoolVal{core}".
     */
    override fun toString(): String = "BoolVal{$core}"
}
