package cobra.commons.value

/**
 * Represents a null value in the storage system.
 * As a [data object], all instances of [NullVal] are inherently equal, as they represent the same conceptual null value.
 *
 * Example usage:
 * ```kotlin
 * val nullVal = NullVal
 * println(nullVal) // Outputs: NullVal
 *
 * val anotherNullVal = NullVal
 * println(nullVal == anotherNullVal) // Outputs: true
 * ```
 *
 * @property core Always `null`, representing the null value.
 */
data object NullVal : IPrimitiveVal {

    /**
     * The core value of this instance, which is always `null`.
     *
     * Example usage:
     * ```kotlin
     * println(NullVal.core) // Outputs: null
     * ```
     */

    override val core = null

    /**
     * Provides a string representation of this null value.
     *
     * Example usage:
     * ```kotlin
     * println(NullVal.toString()) // Outputs: NullVal
     * ```
     *
     * @return The string "NullVal".
     */
    override fun toString(): String = "NullVal"
}