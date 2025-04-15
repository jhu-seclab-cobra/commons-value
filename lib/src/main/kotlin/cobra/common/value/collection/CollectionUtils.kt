package cobra.commons.value

/**
 * Converts a collection of values into a [ListVal], wrapping the list of values as a custom list type.
 *
 * Each element in the collection is converted to an [IValue] using [toVal].
 *
 * @return A [ListVal] representing the collection of values.
 */
val Collection<*>.listVal: ListVal get() = ListVal(map { it.toVal })

val Collection<*>.setVal: SetVal get() = SetVal(map { it.toVal })

/**
 * Returns the current [ListVal] if it is not null, or an empty [ListVal] if the current value is null.
 *
 * This is useful for providing a non-null fallback when working with nullable [ListVal] instances.
 *
 * @return The original [ListVal] if not null, otherwise an empty [ListVal].
 */
fun ListVal?.orEmpty(): ListVal = this ?: ListVal()

/**
 * Converts a map of key-value pairs to a [MapVal], with the keys converted to strings and the values converted to [IValue].
 *
 * This extension processes each key-value pair in the map, converting them into a uniform [MapVal].
 *
 * @return A [MapVal] representing the map.
 */
val Map<*, *>.mapVal: MapVal get() = MapVal(asSequence().map { it.key.toString() to it.value.toVal })

/**
 * Returns the current [MapVal] if it is not null, or an empty [MapVal] if the current value is null.
 *
 * This is useful for providing a non-null fallback when working with nullable [MapVal] instances.
 *
 * @return The original [MapVal] if not null, otherwise an empty [MapVal].
 */
fun MapVal?.orEmpty(): MapVal = this ?: MapVal()

/**
 * Converts a set of values into a [SetVal], wrapping the set of values as a custom set type.
 *
 * Each element in the set is converted to an [IValue] using [toVal].
 *
 * @return A [SetVal] representing the set of values.
 */
val Set<*>.setVal: SetVal get() = SetVal(map { it.toVal })

/**
 * Returns the current [SetVal] if it is not null, or an empty [SetVal] if the current value is null.
 *
 * This is useful for providing a non-null fallback when working with nullable [SetVal] instances.
 *
 * @return The original [SetVal] if not null, otherwise an empty [SetVal].
 */
fun SetVal?.orEmpty(): SetVal = this ?: SetVal()

/**
 * Converts an [IntRange] to a [RangeVal], representing a range of integer values.
 *
 * The [IntRange] is converted into a [RangeVal] by using its first and last values as the bounds of the range.
 *
 * @return A [RangeVal] representing the integer range.
 */
val IntRange.rangeVal: RangeVal get() = RangeVal(first, last)
