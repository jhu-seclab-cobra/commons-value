package cobra.common.value

import kotlin.math.ceil

/**
 * Represents a set of [IValue] objects, providing various operations for set manipulation.
 * It behaves similarly to Kotlin's [Set] interface, allowing common set operations.
 *
 * @property core The internal set of [IValue] elements.
 *
 * Example usage:
 * ```
 * val setVal = SetVal(StrVal("Item1"), NumVal(42))
 * println(setVal.size) // Outputs: 2
 * setVal.add(BoolVal(true))
 * println(setVal.contains(StrVal("Item1"))) // Outputs: true
 * ```
 */
data class SetVal(override val core: LinkedHashSet<IValue> = LinkedHashSet()) : ICollectionVal {

    /**
     * Returns the size of the set.
     *
     * @return The number of elements in the set.
     */
    val size: Int get() = core.size

    constructor(size: Int) : this(LinkedHashSet(ceil(size / 0.75).toInt()))

    /**
     * Constructs a [SetVal] from a collection of [IValue] objects.
     *
     * @param value The collection to initialize the set with.
     */
    constructor(value: Collection<IValue>) : this(LinkedHashSet(value))

    /**
     * Constructs a [SetVal] from a vararg of [IValue] elements.
     *
     * @param value Vararg elements to initialize the set with.
     */
    constructor(vararg value: IValue) : this(LinkedHashSet<IValue>().apply { addAll(value) })

    /**
     * Constructs a [SetVal] from a sequence of [IValue] elements.
     *
     * @param values The sequence to initialize the set with.
     */
    constructor(values: Sequence<IValue>) : this(LinkedHashSet<IValue>().apply { addAll(values) })

    /**
     * Adds the specified [IValue] to the set.
     *
     * @param new The value to add.
     * @return `true` if the set did not already contain the specified value.
     */
    fun add(new: IValue): Boolean = core.add(new)

    /**
     * Removes the specified [IValue] from the set.
     *
     * @param prev The value to remove.
     * @return `true` if the set contained the specified value.
     */
    fun remove(prev: IValue): Boolean = core.remove(prev)

    /**
     * Adds the specified value to the set using the plus operator.
     *
     * @param new The value to add.
     * @return A new [SetVal] with the added value.
     */
    operator fun plus(new: IValue): SetVal = SetVal(core + new)

    operator fun plusAssign(value: IValue) {
        core.add(value)
    }

    /**
     * Removes the specified value from the set using the minus operator.
     *
     * @param prev The value to remove.
     * @return A new [SetVal] with the value removed.
     */
    operator fun minus(prev: IValue): SetVal = SetVal(core - prev)

    operator fun minusAssign(prev: IValue) {
        core.remove(prev)
    }

    /**
     * Checks if the set contains the specified [value].
     *
     * @param value The value to check for.
     * @return `true` if the set contains the [value], `false` otherwise.
     */
    fun contains(value: IValue): Boolean = core.contains(value)

    /**
     * Checks if the set contains all elements from the specified collection.
     *
     * @param values The collection of elements to check for.
     * @return `true` if all elements are contained in the set, `false` otherwise.
     */
    fun containsAll(values: Collection<IValue>): Boolean = core.containsAll(values)

    /**
     * Maps each element of the set to another value using the provided transformation function.
     *
     * @param transform The transformation function to apply.
     * @return A new [SetVal] with the transformed elements.
     */
    fun <R> map(transform: (IValue) -> R): List<R> = core.map { transform(it) }

    /**
     * Converts the set to a list.
     *
     * @return A list containing the elements of the set.
     */
    fun toList(): List<IValue> = core.toList()

    /**
     * Applies the given action to each element in the set.
     *
     * @param action The action to perform on each element.
     */
    fun forEach(action: (IValue) -> Unit) = core.forEach(action)

    /**
     * Returns a sequence of the elements in the set.
     *
     * @return A sequence of [IValue] elements.
     */
    fun asSequence(): Sequence<IValue> = core.asSequence()

    /**
     * Checks if the set is empty.
     *
     * @return `true` if the set is empty, `false` otherwise.
     */
    fun isEmpty(): Boolean = core.isEmpty()

    /**
     * Checks if the set is not empty.
     *
     * @return `true` if the set is not empty, `false` otherwise.
     */
    fun isNotEmpty(): Boolean = core.isNotEmpty()

    override fun toString(): String = core.joinToString(prefix = "{", postfix = "}")
}
