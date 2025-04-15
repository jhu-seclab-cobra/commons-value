package cobra.common.value

/**
 * Represents a list of [IValue] objects, providing various operations for list manipulation.
 * This class behaves similarly to Kotlin's [MutableList] interface, supporting common list operations
 * such as addition, removal, indexing, mapping, and filtering.
 *
 * @property core The internal list of [IValue] elements.
 *
 * Example usage:
 * ```
 * val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
 * println(listVal[0]) // Outputs: StrVal{Item1}
 * listVal += NumVal(42)
 * println(listVal.size) // Outputs: 3
 * ```
 *
 * @property core The internal list of [IValue] elements.
 */
data class ListVal(override val core: ArrayList<IValue> = ArrayList(5)) : ICollectionVal {

    /**
     * Constructs an empty [ListVal] with the specified initial capacity.
     *
     * This constructor allows pre-allocating space for a given number of elements,
     * improving performance when the expected size of the list is known.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(10)
     * println(listVal.size) // Outputs: 0
     * ```
     *
     * @param size The initial capacity of the list. Must be a non-negative integer.
     * @throws IllegalArgumentException If the [size] is negative.
     */
    constructor(size: Int) : this(ArrayList(size))

    /**
     * Constructs a [ListVal] from an existing list of [IValue] objects.
     *
     * This constructor allows initializing the [ListVal] with a pre-existing collection
     * of elements, creating an independent copy of the provided list.
     *
     * Example usage:
     * ```
     * val initialList = listOf(StrVal("Item1"), NumVal(42))
     * val listVal = ListVal(initialList)
     * println(listVal) // Outputs: [StrVal{Item1}, NumVal{42}]
     * ```
     *
     * @param value The list to initialize the [ListVal] with. The input list is copied.
     */
    constructor(value: List<IValue>) : this(ArrayList(value))

    /**
     * Constructs a [ListVal] from a variable number of [IValue] elements.
     *
     * This constructor allows directly creating a [ListVal] with the provided elements.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), NumVal(42), BoolVal.T)
     * println(listVal) // Outputs: [StrVal{Item1}, NumVal{42}, BoolVal{true}]
     * ```
     *
     * @param value Vararg elements to initialize the [ListVal] with.
     */
    constructor(vararg value: IValue) : this(arrayListOf(*value))

    /**
     * Retrieves the element at the specified [index].
     *
     * This function provides access to elements in the list using zero-based indexing.
     * If the index is out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
     * println(listVal[1]) // Outputs: StrVal{Item2}
     * ```
     *
     * @param index The position of the element to retrieve. Must be within the range `[0, size)`.
     * @return The [IValue] at the specified index.
     * @throws IndexOutOfBoundsException If the [index] is out of the valid range.
     */
    operator fun get(index: Int): IValue = core[index]

    /**
     * Updates the element at the specified [index] with a new value.
     *
     * This function replaces the element at the given [index] in the list
     * with the provided [value]. The [index] must be within the valid range
     * of the list, otherwise an exception will be thrown.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
     * listVal[1] = NumVal(42)
     * println(listVal) // Outputs: [StrVal{Item1}, NumVal{42}]
     * ```
     *
     * @param index The position of the element to update. Must be within the range `0..size-1`.
     * @param value The new [IValue] to set at the specified position.
     * @throws IndexOutOfBoundsException If [index] is out of the list's range.
     */
    operator fun set(index: Int, value: IValue) {
        core[index] = value
    }

    /**
     * Returns the number of elements in the list.
     *
     * This property provides the total count of elements currently stored in the list.
     * It can be used to determine the list's size at any point in time.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), StrVal("Item2"))
     * println(listVal.size) // Outputs: 2
     * ```
     *
     * @return The total number of elements in the list.
     */
    val size: Int get() = core.size

    /**
     * Determines whether the list contains the specified element.
     *
     * This method checks if the given [value] is present in the list.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), NumVal(42))
     * println(listVal.contains(StrVal("Item1"))) // Outputs: true
     * println(listVal.contains(NumVal(100))) // Outputs: false
     * ```
     *
     * @param value The [IValue] element to check for.
     * @return `true` if the element exists in the list, `false` otherwise.
     */
    fun contains(value: IValue): Boolean = core.contains(value)

    /**
     * Determines whether the list contains all elements from the specified collection.
     *
     * This method checks if every element in the provided [values] collection is present in the list.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), NumVal(42))
     * val itemsToCheck = listOf(StrVal("Item1"), NumVal(42))
     * println(listVal.containsAll(itemsToCheck)) // Outputs: true
     *
     * val missingItems = listOf(StrVal("Item2"))
     * println(listVal.containsAll(missingItems)) // Outputs: false
     * ```
     *
     * @param values A collection of [IValue] elements to check for.
     * @return `true` if all elements in the collection are found in the list, `false` otherwise.
     */
    fun containsAll(values: Collection<IValue>): Boolean = core.containsAll(values)

    /**
     * Retrieves the index of the first occurrence of the specified element in the list.
     *
     * This method searches the list from the beginning and returns the zero-based index of the
     * first occurrence of the given [value]. If the [value] is not found, it returns `-1`.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), NumVal(42), StrVal("Item1"))
     * println(listVal.indexOf(StrVal("Item1"))) // Outputs: 0
     * println(listVal.indexOf(NumVal(100))) // Outputs: -1
     * ```
     *
     * @param value The [IValue] element to search for.
     * @return The zero-based index of the first occurrence of the [value], or `-1` if not found.
     */
    fun indexOf(value: IValue): Int = core.indexOf(value)

    /**
     * Retrieves the index of the last occurrence of the specified element in the list.
     *
     * This method searches the list from the end and returns the zero-based index of the
     * last occurrence of the given [value]. If the [value] is not found, it returns `-1`.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("Item1"), NumVal(42), StrVal("Item1"))
     * println(listVal.lastIndexOf(StrVal("Item1"))) // Outputs: 2
     * println(listVal.lastIndexOf(NumVal(100))) // Outputs: -1
     * ```
     *
     * @param value The [IValue] element to search for.
     * @return The zero-based index of the last occurrence of the [value], or `-1` if not found.
     */
    fun lastIndexOf(value: IValue): Int = core.lastIndexOf(value)

    /**
     * Retrieves a sublist of elements from the current list within the specified range.
     *
     * This method returns a new [ListVal] containing elements starting from [fromIndex] (inclusive)
     * to [toIndex] (exclusive). If [fromIndex] equals [toIndex], the returned sublist is empty.
     * The indices must be within the bounds of the list, or an [IndexOutOfBoundsException] is thrown.
     *
     * Example usage:
     * ```
     * val listVal = ListVal(StrVal("A"), StrVal("B"), StrVal("C"), StrVal("D"))
     * val sublist = listVal.subList(1, 3)
     * println(sublist) // Outputs: [StrVal{B}, StrVal{C}]
     * ```
     *
     * @param fromIndex The starting index of the range, inclusive.
     * @param toIndex The ending index of the range, exclusive.
     * @return A new [ListVal] containing elements within the specified range.
     * @throws IndexOutOfBoundsException If [fromIndex] or [toIndex] is out of bounds.
     * @throws IllegalArgumentException If [fromIndex] is greater than [toIndex].
     */
    fun subList(fromIndex: Int, toIndex: Int): ListVal = ListVal(core.subList(fromIndex, toIndex))

    /**
     * Returns a new [ListVal] containing all the elements of the current list
     * plus the specified [value] added at the end.
     *
     * This function does not modify the original list; instead, it creates and returns a new [ListVal].
     *
     * Example usage:
     * ```
     * val originalList = ListVal(StrVal("A"), StrVal("B"))
     * val newList = originalList + StrVal("C")
     * println(originalList) // Outputs: [StrVal{A}, StrVal{B}]
     * println(newList)      // Outputs: [StrVal{A}, StrVal{B}, StrVal{C}]
     * ```
     *
     * @param value The [IValue] to add to the list.
     * @return A new [ListVal] with the [value] appended.
     */
    operator fun plus(value: IValue): ListVal = ListVal(core + value)

    /**
     * Adds the specified [value] to the end of the current list.
     *
     * Unlike the `+` operator, this function directly modifies the original list by appending the [value].
     *
     * Example usage:
     * ```
     * val list = ListVal(StrVal("A"), StrVal("B"))
     * list += StrVal("C")
     * println(list) // Outputs: [StrVal{A}, StrVal{B}, StrVal{C}]
     * ```
     *
     * @param value The [IValue] to add to the list.
     */
    operator fun plusAssign(value: IValue) {
        core.add(value)
    }

    /**
     * Returns a new [ListVal] containing all the elements of the current list
     * except the specified [value], which is removed.
     *
     * This function does not modify the original list; instead, it creates and returns a new [ListVal].
     * If the [value] does not exist in the list, the original list remains unchanged.
     *
     * Example usage:
     * ```
     * val originalList = ListVal(StrVal("A"), StrVal("B"), StrVal("C"))
     * val newList = originalList - StrVal("B")
     * println(originalList) // Outputs: [StrVal{A}, StrVal{B}, StrVal{C}]
     * println(newList)      // Outputs: [StrVal{A}, StrVal{C}]
     * ```
     *
     * @param value The [IValue] to remove from the list.
     * @return A new [ListVal] with the [value] removed.
     */
    operator fun minus(value: IValue): ListVal = ListVal(core - value)

    /**
     * Removes the specified [value] from the current list.
     *
     * Unlike the `-` operator, this function directly modifies the original list by removing the [value].
     * If the [value] does not exist in the list, the function has no effect.
     *
     * Example usage:
     * ```
     * val list = ListVal(StrVal("A"), StrVal("B"), StrVal("C"))
     * list -= StrVal("B")
     * println(list) // Outputs: [StrVal{A}, StrVal{C}]
     * ```
     *
     * @param value The [IValue] to remove from the list.
     */
    operator fun minusAssign(value: IValue) {
        core.remove(value)
    }

    /**
     * Checks if the list is empty.
     *
     * @return `true` if the list is empty, `false` otherwise.
     */
    fun isEmpty(): Boolean = core.isEmpty()

    /**
     * Checks if the list is not empty.
     *
     * @return `true` if the list is not empty, `false` otherwise.
     */
    fun isNotEmpty(): Boolean = core.isNotEmpty()

    /**
     * Maps each element of the list to another value using the provided transformation function.
     *
     * @param transform The transformation function to apply.
     * @return A list containing the results of the transformation.
     */
    fun <R> map(transform: (IValue) -> R): List<R> = core.map(transform)

    /**
     * Flattens the list by applying a transformation function to each element that returns a list.
     *
     * @param transform The transformation function to apply.
     * @return A flattened list resulting from applying the transformation to each element.
     */
    fun <R> flatMap(transform: (IValue) -> List<R>): List<R> = core.flatMap(transform)

    /**
     * Applies the given action to each element in the list.
     *
     * @param action The action to perform on each element.
     */
    fun forEach(action: (IValue) -> Unit) = core.forEach(action)

    /**
     * Returns a sequence of the elements in the list.
     *
     * @return A sequence of [IValue] elements.
     */
    fun asSequence(): Sequence<IValue> = core.asSequence()

    /**
     * Converts the list to a mutable set.
     *
     * @return A mutable set containing the elements of the list.
     */
    fun toMutableSet() = linkedSetOf<IValue>().apply { addAll(core) }

    override fun toString(): String = core.joinToString(prefix = "[", postfix = "]")
}