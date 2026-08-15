package edu.jhu.cobra.commons.value

/**
 * Converts a collection of values into a [ListVal].
 *
 * This extension property creates a new [ListVal] instance by converting each element
 * in the collection to an [IValue] using [toVal]. The order of elements is preserved.
 *
 * @return A [ListVal] containing the converted elements
 * @throws IllegalArgumentException if any element cannot be converted to an [IValue]
 */
public val Collection<*>.listVal: ListVal get() = ListVal(map { it.toVal })

/**
 * Converts a collection of values into a [SetVal].
 *
 * This extension property creates a new [SetVal] instance by converting each element
 * in the collection to an [IValue] using [toVal]. Duplicate elements are removed.
 *
 * @return A [SetVal] containing the unique converted elements
 * @throws IllegalArgumentException if any element cannot be converted to an [IValue]
 */
public val Collection<*>.setVal: SetVal get() = SetVal(map { it.toVal })

/**
 * Returns the current [ListVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [ListVal] instances
 * by returning either the original list or a new empty list, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * @return The original [ListVal] if not null, otherwise a new empty [ListVal]
 */
public fun ListVal?.orEmpty(): ListVal = this ?: ListVal()

/**
 * Converts a map to a [MapVal].
 *
 * This extension property creates a new [MapVal] instance by converting each key to a string
 * and each value to an [IValue] using [toVal]. The mapping between keys and values is preserved.
 *
 * @return A [MapVal] containing the converted key-value pairs
 * @throws IllegalArgumentException if any value cannot be converted to an [IValue]
 */
public val Map<*, *>.mapVal: MapVal get() = MapVal(asSequence().map { it.key.toString() to it.value.toVal })

/**
 * Returns the current [MapVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [MapVal] instances
 * by returning either the original map or a new empty map, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * @return The original [MapVal] if not null, otherwise a new empty [MapVal]
 */
public fun MapVal?.orEmpty(): MapVal = this ?: MapVal()

/**
 * Returns the current [SetVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [SetVal] instances
 * by returning either the original set or a new empty set, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * @return The original [SetVal] if not null, otherwise a new empty [SetVal]
 */
public fun SetVal?.orEmpty(): SetVal = this ?: SetVal()

/**
 * Converts an [IntRange] to a [RangeVal].
 *
 * This extension property creates a new [RangeVal] instance using the first and last values
 * of the [IntRange]. The inclusivity of the range bounds is preserved.
 *
 * @return A [RangeVal] representing the same range of integers
 */
public val IntRange.rangeVal: RangeVal get() = RangeVal(first, last)

/**
 * Converts a [LongRange] to a [RangeVal].
 *
 * This extension property creates a new [RangeVal] instance using the first and last values
 * of the [LongRange]. The inclusivity of the range bounds is preserved.
 *
 * @return A [RangeVal] representing the same range of integers
 */
public val LongRange.rangeVal: RangeVal get() = RangeVal(first, last)
