package edu.jhu.cobra.commons.value

/**
 * Converts a collection of values into a [ListVal].
 *
 * This extension property creates a new [ListVal] instance by converting each element
 * in the collection to an [IValue] using [toVal]. The order of elements is preserved.
 *
 * Example:
 * ```kotlin
 * val list = listOf(1, "text", true)
 * val listVal = list.listVal // Creates ListVal containing NumVal(1), StrVal("text"), BoolVal(true)
 * ```
 *
 * @return A [ListVal] containing the converted elements
 * @throws IllegalArgumentException if any element cannot be converted to an [IValue]
 */
val Collection<*>.listVal: ListVal get() = ListVal(map { it.toVal })

/**
 * Converts a collection of values into a [SetVal].
 *
 * This extension property creates a new [SetVal] instance by converting each element
 * in the collection to an [IValue] using [toVal]. Duplicate elements are removed.
 *
 * Example:
 * ```kotlin
 * val collection = listOf(1, 1, 2, "text")
 * val setVal = collection.setVal // Creates SetVal containing NumVal(1), NumVal(2), StrVal("text")
 * ```
 *
 * @return A [SetVal] containing the unique converted elements
 * @throws IllegalArgumentException if any element cannot be converted to an [IValue]
 */
val Collection<*>.setVal: SetVal get() = SetVal(map { it.toVal })

/**
 * Returns the current [ListVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [ListVal] instances
 * by returning either the original list or a new empty list, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * Example:
 * ```kotlin
 * val nullList: ListVal? = null
 * val safeList = nullList.orEmpty() // Returns empty ListVal
 * ```
 *
 * @return The original [ListVal] if not null, otherwise a new empty [ListVal]
 */
fun ListVal?.orEmpty(): ListVal = this ?: ListVal()

/**
 * Converts a map to a [MapVal].
 *
 * This extension property creates a new [MapVal] instance by converting each key to a string
 * and each value to an [IValue] using [toVal]. The mapping between keys and values is preserved.
 *
 * Example:
 * ```kotlin
 * val map = mapOf("key" to 1, "value" to true)
 * val mapVal = map.mapVal // Creates MapVal with "key" -> NumVal(1), "value" -> BoolVal(true)
 * ```
 *
 * @return A [MapVal] containing the converted key-value pairs
 * @throws IllegalArgumentException if any value cannot be converted to an [IValue]
 */
val Map<*, *>.mapVal: MapVal get() = MapVal(asSequence().map { it.key.toString() to it.value.toVal })

/**
 * Returns the current [MapVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [MapVal] instances
 * by returning either the original map or a new empty map, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * Example:
 * ```kotlin
 * val nullMap: MapVal? = null
 * val safeMap = nullMap.orEmpty() // Returns empty MapVal
 * ```
 *
 * @return The original [MapVal] if not null, otherwise a new empty [MapVal]
 */
fun MapVal?.orEmpty(): MapVal = this ?: MapVal()

/**
 * Converts a set to a [SetVal].
 *
 * This extension property creates a new [SetVal] instance by converting each element
 * in the set to an [IValue] using [toVal]. The uniqueness of elements is preserved.
 *
 * Example:
 * ```kotlin
 * val set = setOf(1, true, "text")
 * val setVal = set.setVal // Creates SetVal containing NumVal(1), BoolVal(true), StrVal("text")
 * ```
 *
 * @return A [SetVal] containing the converted elements
 * @throws IllegalArgumentException if any element cannot be converted to an [IValue]
 */
val Set<*>.setVal: SetVal get() = SetVal(map { it.toVal })

/**
 * Returns the current [SetVal] or an empty one if null.
 *
 * This extension function provides a safe way to handle nullable [SetVal] instances
 * by returning either the original set or a new empty set, similar to Kotlin's
 * standard library `orEmpty()` function.
 *
 * Example:
 * ```kotlin
 * val nullSet: SetVal? = null
 * val safeSet = nullSet.orEmpty() // Returns empty SetVal
 * ```
 *
 * @return The original [SetVal] if not null, otherwise a new empty [SetVal]
 */
fun SetVal?.orEmpty(): SetVal = this ?: SetVal()

/**
 * Converts an [IntRange] to a [RangeVal].
 *
 * This extension property creates a new [RangeVal] instance using the first and last values
 * of the [IntRange]. The inclusivity of the range bounds is preserved.
 *
 * Example:
 * ```kotlin
 * val range = 1..10
 * val rangeVal = range.rangeVal // Creates RangeVal(1, 10)
 * ```
 *
 * @return A [RangeVal] representing the same range of integers
 */
val IntRange.rangeVal: RangeVal get() = RangeVal(first, last)
