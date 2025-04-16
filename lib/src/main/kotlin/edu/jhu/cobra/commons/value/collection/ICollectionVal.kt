package edu.jhu.cobra.commons.value

/**
 * Represents a collection value in the system. This sealed interface extends [IValue] and is used
 * as the base interface for all collection-based values, such as lists, sets, maps, or ranges.
 *
 * Implementations of this interface should handle collections of [IValue] objects and provide
 * specific behavior for different types of collections.
 *
 * Example implementations:
 * - [ListVal]: Represents a list of values.
 * - [SetVal]: Represents a set of values.
 * - [MapVal]: Represents a map of key-value pairs.
 * - [RangeVal]: Represents a range of numeric values.
 *
 * Example usage:
 * ```
 * val listVal: ICollectionVal = ListVal(listOf(StrVal("Item1"), StrVal("Item2")))
 * val setVal: ICollectionVal = SetVal(setOf(NumVal(1), NumVal(2)))
 * val mapVal: ICollectionVal = MapVal(mapOf("key1" to StrVal("value1"), "key2" to NumVal(42)))
 * val rangeVal: ICollectionVal = RangeVal(NumVal(1), NumVal(10)) // Range from 1 to 10
 * ```
 */
sealed interface ICollectionVal : IValue
