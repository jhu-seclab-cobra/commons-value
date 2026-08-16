package edu.jhu.cobra.commons.value

/**
 * Represents a set of [IValue] objects as a [MutableSet] backed by [core].
 *
 * All [MutableSet] members and standard library collection extensions operate directly on [core],
 * which preserves insertion order. The primary constructor adopts the passed set; secondary
 * constructors copy their input. Equality follows the JDK collection contract: a [SetVal] equals
 * any [Set] with equal content.
 *
 * Mutating an element while it is a member of the set corrupts membership, matching the
 * [java.util.Set] contract for mutable elements.
 *
 * @property core The internal set of [IValue] elements.
 */
public class SetVal(
    override val core: LinkedHashSet<IValue> = LinkedHashSet(),
) : ICollectionVal,
    MutableSet<IValue> by core {
    /**
     * Constructs an empty [SetVal] sized to hold [size] elements without rehashing.
     *
     * @param size The expected number of elements.
     */
    public constructor(size: Int) : this(LinkedHashSet(hashCapacityFor(size)))

    /**
     * Constructs a [SetVal] from a collection of [IValue] objects.
     *
     * @param value The collection to initialize the set with.
     */
    public constructor(value: Collection<IValue>) : this(LinkedHashSet(value))

    /**
     * Constructs a [SetVal] from a vararg of [IValue] elements.
     *
     * @param value Vararg elements to initialize the set with.
     */
    public constructor(vararg value: IValue) : this(LinkedHashSet<IValue>(hashCapacityFor(value.size)).apply { addAll(value) })

    /**
     * Constructs a [SetVal] from a sequence of [IValue] elements.
     *
     * @param values The sequence to initialize the set with.
     */
    public constructor(values: Sequence<IValue>) : this(LinkedHashSet<IValue>().apply { addAll(values) })

    /**
     * Returns a recursive structural copy of this set.
     *
     * @return A new [SetVal] whose elements are deep copies of this set's elements.
     */
    override fun deepCopy(): SetVal = SetVal(core.mapTo(LinkedHashSet(hashCapacityFor(core.size))) { it.deepCopy() })

    override fun equals(other: Any?): Boolean = this === other || (other is Set<*> && core == other)

    override fun hashCode(): Int = core.hashCode()

    override fun toString(): String = core.joinToString(prefix = "{", postfix = "}")
}
