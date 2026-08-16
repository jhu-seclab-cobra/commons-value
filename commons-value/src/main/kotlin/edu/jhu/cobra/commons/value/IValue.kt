package edu.jhu.cobra.commons.value

/**
 * Represents a generic value within the system with a flexible core property.
 * This interface is designed to encapsulate any type of value, allowing implementations
 * to define specific types of values with additional properties or behaviors.
 */
public sealed interface IValue {
    /**
     * The core content of the value, which can be any type or null.
     * This property holds the actual data represented by the value instance.
     */
    public val core: Any?

    /**
     * Returns a structurally independent copy of this value.
     *
     * Immutable values return themselves; mutable collections copy recursively, so no
     * mutable state is shared between the original and the copy.
     *
     * @return A value equal to this one that shares no mutable state with it.
     * @throws IllegalArgumentException If value nesting exceeds the supported depth, including
     *   cyclic value graphs.
     */
    public fun deepCopy(): IValue
}

// Bounds recursive value-graph traversal in deepCopy and the serializers: keeps stack use
// finite and rejects cyclic value graphs with a diagnosable error.
internal const val MAX_NESTING_DEPTH = 1000

/**
 * Validates a recursive value-nesting depth against [MAX_NESTING_DEPTH].
 *
 * @param depth The current nesting depth, zero for the top-level value
 * @return The validated depth
 * @throws IllegalArgumentException if [depth] exceeds [MAX_NESTING_DEPTH]
 */
internal fun checkNestingDepth(depth: Int): Int {
    require(depth <= MAX_NESTING_DEPTH) { "Value nesting exceeds $MAX_NESTING_DEPTH levels" }
    return depth
}

// Depth-carrying dispatch for the recursive copy: mutable collections continue through their
// guarded overloads; immutable values are their own copy.
internal fun IValue.deepCopy(depth: Int): IValue =
    when (this) {
        is ListVal -> deepCopy(depth)
        is SetVal -> deepCopy(depth)
        is MapVal -> deepCopy(depth)
        is RangeVal -> this
        is IPrimitiveVal -> this
    }
