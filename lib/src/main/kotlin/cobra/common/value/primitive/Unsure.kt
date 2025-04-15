package cobra.common.value


/**
 * Represents an undefined or uncertain value in the storage system.
 *
 * This class models various types (string, number, boolean) when the exact type is not known.
 * It provides predefined constants for each supported uncertain type.
 *
 * @property core The string representing the uncertain or undefined type.
 *
 * Example usage:
 * ```kotlin
 * val uncertainStr = Unsure.STR
 * println(uncertainStr) // Outputs: Unsure{__StrVal__}
 *
 * val uncertainNum = Unsure.NUM
 * println(uncertainNum) // Outputs: Unsure{__NumVal__}
 * ```
 */
enum class Unsure(override val core: String) : IPrimitiveVal {

    /**
     * Represents an uncertain or unspecified value of any type.
     */
    ANY("__IPrimitiveVal__"),

    /**
     * Represents an uncertain or undefined string value.
     */
    STR("__StrVal__"),

    /**
     * Represents an uncertain or undefined numeric value.
     */
    NUM("__NumVal__"),

    /**
     * Represents an uncertain or undefined boolean value.
     */
    BOOL("__BoolVal__");

    companion object {

        private val stringValues = entries.map { it.core }.toSet()

        /**
         * Returns the appropriate [Unsure] instance based on the provided [core] string.
         * If the core does not match any predefined type, `null` is returned.
         *
         * @param core The string representing the uncertain type.
         * @return The corresponding [Unsure] instance or `null` if no match is found.
         */
        fun valueOf(core: String): Unsure? = when (core) {
            STR.core -> STR
            NUM.core -> NUM
            BOOL.core -> BOOL
            ANY.core -> ANY
            else -> null
        }

        /**
         * Creates an [Unsure] object based on the type of the given example value.
         * This method returns the predefined type that corresponds to the example value.
         *
         * Example usage:
         * ```kotlin
         * val example = StrVal("example")
         * val uncertain = Unsure.new(example)
         * println(uncertain) // Outputs: Unsure{__StrVal__}
         * ```
         *
         * @param example The [IPrimitiveVal] example used to determine the uncertain type.
         * @return The corresponding [Unsure] instance: [STR], [NUM], [BOOL], or [ANY].
         */
        fun new(example: IPrimitiveVal): Unsure = when (example) {
            is StrVal -> STR
            is NumVal -> NUM
            is BoolVal -> BOOL
            is Unsure -> example
            else -> ANY
        }

        /**
         * Creates an [Unsure] object based on the generic type [T].
         * This method determines the appropriate uncertain type using reified generics.
         *
         * Example usage:
         * ```kotlin
         * val uncertain = Unsure.new<NumVal>()
         * println(uncertain) // Outputs: Unsure{__NumVal__}
         * ```
         *
         * @return The corresponding [Unsure] instance: [STR], [NUM], [BOOL], or [ANY].
         */
        inline fun <reified T : IPrimitiveVal> new(): Unsure = when (T::class) {
            StrVal::class -> STR
            NumVal::class -> NUM
            BoolVal::class -> BOOL
            else -> ANY
        }

        /**
         * Checks if the given string is contained within the predefined string values of [Unsure].
         *
         * @param string The string to check for containment.
         * @return `true` if the string is contained in the predefined values; otherwise, `false`.
         */
        operator fun contains(string: String): Boolean = string in stringValues
    }


}
