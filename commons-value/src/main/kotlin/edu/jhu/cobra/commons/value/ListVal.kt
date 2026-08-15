package edu.jhu.cobra.commons.value

// Most ListVal instances hold only a few elements; a small initial capacity avoids
// ArrayList's default ten-slot allocation.
private const val DEFAULT_INITIAL_CAPACITY = 5

/**
 * Represents a list of [IValue] objects as a [MutableList] backed by [core].
 *
 * All [MutableList] members and standard library collection extensions operate directly on [core].
 * The primary constructor adopts the passed list; secondary constructors copy their input.
 * Equality is content-based and holds only between [ListVal] instances.
 *
 * @property core The internal list of [IValue] elements.
 */
public class ListVal(
    override val core: ArrayList<IValue> = ArrayList(DEFAULT_INITIAL_CAPACITY),
) : ICollectionVal,
    MutableList<IValue> by core {
    /**
     * Constructs an empty [ListVal] with the specified initial capacity.
     *
     * @param size The initial capacity of the list. Must be a non-negative integer.
     * @throws IllegalArgumentException If the [size] is negative.
     */
    public constructor(size: Int) : this(ArrayList(size))

    /**
     * Constructs a [ListVal] from an existing list of [IValue] objects.
     *
     * @param value The list to initialize the [ListVal] with. The input list is copied.
     */
    public constructor(value: List<IValue>) : this(ArrayList(value))

    /**
     * Constructs a [ListVal] from a variable number of [IValue] elements.
     *
     * @param value Vararg elements to initialize the [ListVal] with.
     */
    public constructor(vararg value: IValue) : this(ArrayList<IValue>(value.size).apply { addAll(value) })

    /**
     * Returns a recursive structural copy of this list.
     *
     * @return A new [ListVal] whose elements are deep copies of this list's elements.
     */
    override fun deepCopy(): ListVal = ListVal(core.mapTo(ArrayList(core.size)) { it.deepCopy() })

    override fun equals(other: Any?): Boolean = this === other || (other is ListVal && core == other.core)

    override fun hashCode(): Int = core.hashCode()

    override fun toString(): String = core.joinToString(prefix = "[", postfix = "]")
}
