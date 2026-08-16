package edu.jhu.cobra.commons.value

/**
 * Represents a map with string keys and [IValue] values as a [MutableMap] backed by [core].
 *
 * All [MutableMap] members and standard library map extensions operate directly on [core],
 * which preserves insertion order. The primary constructor adopts the passed map; secondary
 * constructors copy their input.
 * Equality follows the JDK collection contract: a [MapVal] equals any [Map] with equal content.
 *
 * @property core The internal map of key-value pairs.
 */
public class MapVal(
    override val core: LinkedHashMap<String, IValue> = LinkedHashMap(),
) : ICollectionVal,
    MutableMap<String, IValue> by core {
    /**
     * Constructs an empty [MapVal] sized to hold [size] entries without rehashing.
     *
     * @param size The expected number of entries.
     */
    public constructor(size: Int) : this(LinkedHashMap(hashCapacityFor(size)))

    /**
     * Constructs a [MapVal] from an existing map of string keys and [IValue] values.
     *
     * @param value The map to initialize the [MapVal] with.
     */
    public constructor(value: Map<String, IValue>) : this(LinkedHashMap(value))

    /**
     * Constructs a [MapVal] from vararg key-value pairs.
     *
     * @param value Vararg key-value pairs to initialize the map with.
     */
    public constructor(
        vararg value: Pair<String, IValue>,
    ) : this(LinkedHashMap<String, IValue>(hashCapacityFor(value.size)).apply { value.forEach { (k, v) -> put(k, v) } })

    /**
     * Constructs a [MapVal] from a sequence of key-value pairs.
     *
     * @param values The sequence to initialize the map with.
     */
    public constructor(values: Sequence<Pair<String, IValue>>) : this(values.toMap())

    /**
     * Constructs a [MapVal] from a list of key-value pairs.
     *
     * @param values The list to initialize the map with.
     */
    public constructor(values: List<Pair<String, IValue>>) : this(values.toMap())

    /**
     * Returns a recursive structural copy of this map.
     *
     * @return A new [MapVal] whose values are deep copies of this map's values.
     */
    override fun deepCopy(): MapVal =
        MapVal(
            core.mapValuesTo(LinkedHashMap(hashCapacityFor(core.size))) { (_, value) -> value.deepCopy() },
        )

    override fun equals(other: Any?): Boolean = this === other || (other is Map<*, *> && core == other)

    override fun hashCode(): Int = core.hashCode()

    override fun toString(): String = core.map { (k, v) -> "$k=$v" }.joinToString(prefix = "{", postfix = "}")
}
