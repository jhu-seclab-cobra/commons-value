package edu.jhu.cobra.commons.value

/**
 * Projects this value onto a JSON tree of plain JVM objects: `null`, [String], [Long], [Double],
 * [Boolean], [List] and [Map] with [String] keys. Any JSON writer serializes the result directly,
 * so this module carries no JSON dependency.
 *
 * The projection is deterministic and one-way: [Unsure] renders as its enum name, [RangeVal] as
 * the two-element list of its bounds, map entries sort by key, and set elements sort in the
 * canonical order of their projected trees. Two equal values project onto equal trees.
 *
 * @return The JSON tree for this value.
 * @throws IllegalArgumentException If value nesting exceeds the supported depth, including
 *   cyclic value graphs.
 */
public fun IValue.toJsonTree(): Any? = toJsonTree(depth = 0)

private fun IValue.toJsonTree(depth: Int): Any? {
    checkNestingDepth(depth)
    return when (this) {
        is Unsure -> name
        is IPrimitiveVal -> core
        is ICollectionVal -> toJsonCollection(depth)
    }
}

private fun ICollectionVal.toJsonCollection(depth: Int): Any =
    when (this) {
        is ListVal -> core.map { it.toJsonTree(depth + 1) }
        is SetVal -> core.map { it.toJsonTree(depth + 1) }.sortedWith(JsonTreeOrder)
        is MapVal -> core.entries.sortedBy { it.key }.associateTo(LinkedHashMap()) { it.key to it.value.toJsonTree(depth + 1) }
        is RangeVal -> core.map { it.core }
    }

// Canonical total order over projected trees: by kind, then by content. Numbers compare by
// numeric value, then by rendering so 1 and 1.0 stay distinct; lists and sorted map entries
// compare element-wise, shorter first on a common prefix.
private object JsonTreeOrder : Comparator<Any?> {
    override fun compare(
        a: Any?,
        b: Any?,
    ): Int {
        val byKind = a.kindRank().compareTo(b.kindRank())
        if (byKind != 0) return byKind
        return when (a) {
            null -> 0
            is Boolean -> a.compareTo(b as Boolean)
            is Number -> compareNumbers(a, b as Number)
            is String -> a.compareTo(b as String)
            is List<*> -> compareLists(a, b as List<*>)
            is Map<*, *> -> compareLists(a.entryLists(), (b as Map<*, *>).entryLists())
            else -> error("Unsupported JSON tree node: $a")
        }
    }

    private fun Any?.kindRank(): Int =
        when (this) {
            null -> 0
            is Boolean -> 1
            is Number -> 2
            is String -> 3
            is List<*> -> 4
            is Map<*, *> -> 5
            else -> error("Unsupported JSON tree node: $this")
        }

    private fun Map<*, *>.entryLists(): List<List<Any?>> = entries.map { listOf(it.key, it.value) }

    private fun compareNumbers(
        a: Number,
        b: Number,
    ): Int {
        val byValue = a.toDouble().compareTo(b.toDouble())
        return if (byValue != 0) byValue else a.toString().compareTo(b.toString())
    }

    private fun compareLists(
        a: List<*>,
        b: List<*>,
    ): Int {
        a.zip(b).forEach { (x, y) ->
            val c = compare(x, y)
            if (c != 0) return c
        }
        return a.size.compareTo(b.size)
    }
}
