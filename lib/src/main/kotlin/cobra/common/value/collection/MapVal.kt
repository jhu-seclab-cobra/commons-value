package cobra.common.value

import kotlin.math.ceil


/**
 * Represents a map with string keys and [IValue] values, providing various operations for map manipulation.
 * It behaves similarly to Kotlin's [Map] interface, allowing common map operations.
 *
 * @property core The internal map of key-value pairs.
 *
 * Example usage:
 * ```
 * val mapVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
 * println(mapVal["key1"]) // Outputs: StrVal{value1}
 * mapVal["key3"] = BoolVal(true)
 * println(mapVal.size) // Outputs: 3
 * ```
 */
data class MapVal(override val core: HashMap<String, IValue> = HashMap()) : ICollectionVal {

    /**
     * Returns the size of the map.
     *
     * @return The number of key-value pairs in the map.
     */
    val size: Int get() = core.size

    constructor(size: Int) : this(HashMap(ceil(size / 0.75).toInt()))

    /**
     * Constructs a [MapVal] from an existing map of string keys and [IValue] values.
     *
     * @param value The map to initialize the [MapVal] with.
     */
    constructor(value: Map<String, IValue>) : this(HashMap(value))

    /**
     * Constructs a [MapVal] from vararg key-value pairs.
     *
     * @param value Vararg key-value pairs to initialize the map with.
     */
    constructor(vararg value: Pair<String, IValue>) : this(hashMapOf(*value))

    /**
     * Constructs a [MapVal] from a sequence of key-value pairs.
     *
     * @param values The sequence to initialize the map with.
     */
    constructor(values: Sequence<Pair<String, IValue>>) : this(values.toMap())

    /**
     * Constructs a [MapVal] from a list of key-value pairs.
     *
     * @param values The list to initialize the map with.
     */
    constructor(values: List<Pair<String, IValue>>) : this(values.toMap())

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key The key to lookup.
     * @return The value associated with the key, or `null` if the key is not present.
     */
    operator fun get(key: String): IValue? = core[key]

    /**
     * Updates or adds a new key-value pair in the map.
     *
     * @param key The key to update or add.
     * @param value The value associated with the key.
     */
    operator fun set(key: String, value: IValue) {
        core[key] = value
    }

    /**
     * Adds a new key-value pair to the map.
     *
     * @param key The key to add.
     * @param value The value to associate with the key.
     */
    fun add(key: String, value: IValue) = core.put(key, value)

    /**
     * Adds a key-value pair using the plus operator.
     *
     * @param pair The key-value pair to add.
     */
    operator fun plus(pair: Pair<String, IValue>) = core.put(pair.first, pair.second)

    /**
     * Removes the specified key from the map.
     *
     * @param key The key to remove.
     * @return The removed value, or `null` if the key was not present.
     */
    operator fun minus(key: String) = core.remove(key)

    /**
     * Removes the specified key from the map.
     *
     * @param key The key to remove.
     * @return The removed value, or `null` if the key was not present.
     */
    fun remove(key: String) = core.remove(key)

    /**
     * Returns all keys in the map.
     *
     * @return A set containing all keys in the map.
     */
    fun keys(): Set<String> = core.keys

    /**
     * Returns all values in the map.
     *
     * @return A collection of all values in the map.
     */
    fun values(): Collection<IValue> = core.values

    fun forEach(action: (Map.Entry<String, IValue>) -> Unit) = core.forEach(action)

    /**
     * Applies the given behavior function to each entry in the map.
     *
     * @param behavior The function to apply to each map entry.
     * @return A list of results produced by the behavior function.
     */
    fun <R> map(behavior: (Map.Entry<String, IValue>) -> R): List<R> = core.map(behavior)

    fun <R> mapValues(behavior: (Map.Entry<String, IValue>) -> R) = core.mapValues { behavior(it) }

    fun <R> flatMap(behavior: (Map.Entry<String, IValue>) -> Iterable<R>): List<R> = core.flatMap(behavior)

    fun isEmpty() = core.isEmpty()

    operator fun contains(key: String): Boolean = core.containsKey(key)

    /**
     * Converts the map to an array of key-value pairs.
     *
     * @return An array of key-value pairs.
     */
    fun toTypeArray(): Array<Pair<String, IValue>> {
        val pairIterator = core.iterator() // get the iterator
        return Array(core.size) { pairIterator.next().toPair() }
    }

    /**
     * Converts the map to a list of key-value pairs.
     *
     * @return A list of key-value pairs.
     */
    fun toList(): List<Pair<String, IValue>> = core.toList()

    override fun toString(): String = core.map { (k, v) -> "$k=$v" }.joinToString(prefix = "{", postfix = "}")
}

