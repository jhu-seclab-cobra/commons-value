package edu.jhu.cobra.commons.value

/**
 * Represents a map with string keys and [IValue] values, providing various operations for map manipulation.
 * It behaves similarly to Kotlin's [Map] interface, allowing common map operations.
 *
 * @property core The internal map of key-value pairs.
 */
public class MapVal(
    override val core: HashMap<String, IValue> = HashMap(),
) : ICollectionVal {
    override fun equals(other: Any?): Boolean = this === other || (other is MapVal && core == other.core)

    override fun hashCode(): Int = core.hashCode()

    /**
     * Returns the size of the map.
     *
     * @return The number of key-value pairs in the map.
     */
    public val size: Int get() = core.size

    /**
     * Constructs an empty [MapVal] sized to hold [size] entries without rehashing.
     *
     * @param size The expected number of entries.
     */
    public constructor(size: Int) : this(HashMap(hashCapacityFor(size)))

    /**
     * Constructs a [MapVal] from an existing map of string keys and [IValue] values.
     *
     * @param value The map to initialize the [MapVal] with.
     */
    public constructor(value: Map<String, IValue>) : this(HashMap(value))

    /**
     * Constructs a [MapVal] from vararg key-value pairs.
     *
     * @param value Vararg key-value pairs to initialize the map with.
     */
    public constructor(
        vararg value: Pair<String, IValue>,
    ) : this(HashMap<String, IValue>(hashCapacityFor(value.size)).apply { value.forEach { (k, v) -> put(k, v) } })

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
     * Retrieves the value associated with the specified key.
     *
     * @param key The key to lookup.
     * @return The value associated with the key, or `null` if the key is not present.
     */
    public operator fun get(key: String): IValue? = core[key]

    /**
     * Updates or adds a new key-value pair in the map.
     *
     * @param key The key to update or add.
     * @param value The value associated with the key.
     */
    public operator fun set(
        key: String,
        value: IValue,
    ) {
        core[key] = value
    }

    /**
     * Adds a new key-value pair to the map.
     *
     * @param key The key to add.
     * @param value The value to associate with the key.
     */
    public fun add(
        key: String,
        value: IValue,
    ): IValue? = core.put(key, value)

    /**
     * Returns a new [MapVal] containing the entries of this map plus the given pair.
     *
     * @param pair The key-value pair to add.
     * @return A new [MapVal] with the pair added; this map is unchanged.
     */
    public operator fun plus(pair: Pair<String, IValue>): MapVal = MapVal(core + pair)

    /**
     * Adds a key-value pair to this map in place.
     *
     * @param pair The key-value pair to add.
     */
    public operator fun plusAssign(pair: Pair<String, IValue>) {
        core[pair.first] = pair.second
    }

    /**
     * Returns a new [MapVal] containing the entries of this map without the given key.
     *
     * @param key The key to remove.
     * @return A new [MapVal] without the key; this map is unchanged.
     */
    public operator fun minus(key: String): MapVal = MapVal(core - key)

    /**
     * Removes the specified key from this map in place.
     *
     * @param key The key to remove.
     */
    public operator fun minusAssign(key: String) {
        core.remove(key)
    }

    /**
     * Removes the specified key from the map.
     *
     * @param key The key to remove.
     * @return The removed value, or `null` if the key was not present.
     */
    public fun remove(key: String): IValue? = core.remove(key)

    /**
     * Returns all keys in the map.
     *
     * @return A set containing all keys in the map.
     */
    public fun keys(): Set<String> = core.keys

    /**
     * Returns all values in the map.
     *
     * @return A collection of all values in the map.
     */
    public fun values(): Collection<IValue> = core.values

    /**
     * Applies the given action to each entry in the map.
     *
     * @param action The action to perform on each entry.
     */
    public fun forEach(action: (Map.Entry<String, IValue>) -> Unit): Unit = core.forEach(action)

    /**
     * Applies the given behavior function to each entry in the map.
     *
     * @param behavior The function to apply to each map entry.
     * @return A list of results produced by the behavior function.
     */
    public fun <R> map(behavior: (Map.Entry<String, IValue>) -> R): List<R> = core.map(behavior)

    /**
     * Transforms the value of each entry using the given behavior function, keeping the keys.
     *
     * @param behavior The function producing the new value for each entry.
     * @return A map with the same keys and the transformed values.
     */
    public fun <R> mapValues(behavior: (Map.Entry<String, IValue>) -> R): Map<String, R> = core.mapValues(behavior)

    /**
     * Flattens the results of applying the behavior function to each entry in the map.
     *
     * @param behavior The function producing an iterable of results for each entry.
     * @return A list of all results produced by the behavior function.
     */
    public fun <R> flatMap(behavior: (Map.Entry<String, IValue>) -> Iterable<R>): List<R> = core.flatMap(behavior)

    /**
     * Checks if the map is empty.
     *
     * @return `true` if the map contains no entries, `false` otherwise.
     */
    public fun isEmpty(): Boolean = core.isEmpty()

    /**
     * Checks if the map contains the specified key.
     *
     * @param key The key to check for.
     * @return `true` if the key is present, `false` otherwise.
     */
    public operator fun contains(key: String): Boolean = core.containsKey(key)

    /**
     * Converts the map to an array of key-value pairs.
     *
     * @return An array of key-value pairs.
     */
    public fun toPairArray(): Array<Pair<String, IValue>> {
        val pairIterator = core.iterator() // get the iterator
        return Array(core.size) { pairIterator.next().toPair() }
    }

    /**
     * Converts the map to a list of key-value pairs.
     *
     * @return A list of key-value pairs.
     */
    public fun toList(): List<Pair<String, IValue>> = core.toList()

    override fun toString(): String = core.map { (k, v) -> "$k=$v" }.joinToString(prefix = "{", postfix = "}")
}
