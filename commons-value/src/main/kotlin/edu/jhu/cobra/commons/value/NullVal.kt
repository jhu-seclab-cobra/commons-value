package edu.jhu.cobra.commons.value

/**
 * Represents a null value in the storage system.
 * As a [data object], all instances of [NullVal] are inherently equal, as they represent the same conceptual null value.
 *
 * @property core Always `null`, representing the null value.
 */
public data object NullVal : IPrimitiveVal {
    /**
     * The core value of this instance, which is always `null`.
     */
    override val core: Nothing? = null

    /**
     * Provides a string representation of this null value.
     *
     * @return The string "NullVal".
     */
    override fun toString(): String = "NullVal"
}
