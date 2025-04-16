package cobra.commons.value

/**
 * Converts any value to its corresponding [IValue] representation. This is useful for converting
 * primitive types, collections, maps, and ranges into a uniform [IValue] type.
 *
 * @return The [IValue] representing the current value.
 * @throws IllegalArgumentException If the value cannot be converted to an [IValue].
 */
val Any?.toVal: IValue
    get() = when (this) {
        null -> NullVal
        is Number -> numVal
        is String -> strVal
        is Boolean -> boolVal
        is List<*> -> listVal
        is Map<*, *> -> mapVal
        is IntRange -> rangeVal
        is Set<*> -> setVal
        is IValue -> this
        else -> throw IllegalArgumentException("Cannot convert $this to IValue")
    }
