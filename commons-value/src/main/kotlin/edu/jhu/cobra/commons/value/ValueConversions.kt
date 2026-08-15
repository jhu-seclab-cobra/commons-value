package edu.jhu.cobra.commons.value

/**
 * Converts any value to its corresponding [IValue] representation. This is useful for converting
 * primitive types, collections, maps, and ranges into a uniform [IValue] type.
 *
 * @return The [IValue] representing the current value.
 * @throws IllegalArgumentException If the value cannot be converted to an [IValue].
 */
public val Any?.toVal: IValue
    get() =
        when (this) {
            // IValue first: collection values are themselves List/Set/Map and must not be re-wrapped.
            is IValue -> this
            is List<*> -> listVal
            is Map<*, *> -> mapVal
            is IntRange -> rangeVal
            is Set<*> -> setVal
            else -> primitiveVal
        }
