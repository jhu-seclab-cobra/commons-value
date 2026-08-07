package edu.jhu.cobra.commons.value

import kotlin.math.ceil

// Default load factor of HashMap and LinkedHashSet; fixed by the JDK collection implementations.
private const val HASH_LOAD_FACTOR = 0.75

// Initial capacity that lets a hash container hold [size] entries without rehashing.
internal fun hashCapacityFor(size: Int): Int = ceil(size / HASH_LOAD_FACTOR).toInt()

/**
 * Represents a collection value in the system. This sealed interface extends [IValue] and is used
 * as the base interface for all collection-based values, such as lists, sets, maps, or ranges.
 *
 * Implementations of this interface should handle collections of [IValue] objects and provide
 * specific behavior for different types of collections.
 *
 * Implementations:
 * - [ListVal]: Represents a list of values.
 * - [SetVal]: Represents a set of values.
 * - [MapVal]: Represents a map of key-value pairs.
 * - [RangeVal]: Represents a range of numeric values.
 */
public sealed interface ICollectionVal : IValue
