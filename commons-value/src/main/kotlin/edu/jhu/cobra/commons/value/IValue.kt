package edu.jhu.cobra.commons.value

/**
 * Represents a generic value within the system with a flexible core property.
 * This interface is designed to encapsulate any type of value, allowing implementations
 * to define specific types of values with additional properties or behaviors.
 */
public sealed interface IValue {
    /**
     * The core content of the value, which can be any type or null.
     * This property holds the actual data represented by the value instance.
     */
    public val core: Any?

    /**
     * Returns a structurally independent copy of this value.
     *
     * Immutable values return themselves; mutable collections copy recursively, so no
     * mutable state is shared between the original and the copy.
     *
     * @return A value equal to this one that shares no mutable state with it.
     */
    public fun deepCopy(): IValue
}
