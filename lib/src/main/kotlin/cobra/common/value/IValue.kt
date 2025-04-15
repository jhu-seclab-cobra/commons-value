package cobra.common.value

/**
 * Represents a generic value within the system with a flexible core property.
 * This interface is designed to encapsulate any type of value, allowing implementations
 * to define specific types of values with additional properties or behaviors.
 */
sealed interface IValue {
    /**
     * The core content of the value, which can be any type or null.
     * This property holds the actual data represented by the value instance.
     */
    val core: Any?
}
