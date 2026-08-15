package edu.jhu.cobra.commons.value

import java.math.BigDecimal

/**
 * A sealed interface that represents a primitive value in the storage system.
 * This interface extends [IValue], providing a common structure for all primitive data types.
 *
 * Implementing classes:
 * - [StrVal]: String values.
 * - [IntVal]: Integer values (Long-backed).
 * - [FloatVal]: Floating-point values (Double-backed).
 * - [BoolVal]: Boolean values.
 * - [NullVal]: Null values.
 * - [Unsure]: Uncertain or undefined values.
 *
 * @property core Holds the actual value of the primitive type.
 */
public sealed interface IPrimitiveVal :
    IValue,
    Comparable<IPrimitiveVal> {
    /**
     * Compares primitives under a total order.
     *
     * Kinds rank `NullVal < BoolVal < numeric < StrVal < Unsure`. [IntVal] and [FloatVal] form one
     * numeric family compared by exact numeric value, with `NaN` above every number and `-0.0`
     * order-equivalent to `0.0`. [StrVal] compares lexicographically, [BoolVal] as `false < true`,
     * [Unsure] by declaration order. The order is inconsistent with [equals]: equal-valued members
     * of different numeric kinds compare as equivalent yet remain distinct values.
     *
     * @param other The primitive to compare with
     * @return A negative number, zero, or a positive number as this value orders below,
     *         equivalent to, or above [other]
     */
    override fun compareTo(other: IPrimitiveVal): Int {
        val rankDiff = kindRank().compareTo(other.kindRank())
        if (rankDiff != 0) return rankDiff
        return when (this) {
            is NullVal -> 0
            is BoolVal -> core.compareTo((other as BoolVal).core)
            is IntVal, is FloatVal -> compareNumeric(this, other)
            is StrVal -> core.compareTo((other as StrVal).core)
            is Unsure -> ordinal.compareTo((other as Unsure).ordinal)
        }
    }
}

// Largest Long magnitude below which every Long converts to Double without rounding.
private const val DOUBLE_EXACT_LONG_BOUND = 1L shl 53

private fun IPrimitiveVal.kindRank(): Int =
    when (this) {
        is NullVal -> 0
        is BoolVal -> 1
        is IntVal, is FloatVal -> 2
        is StrVal -> 3
        is Unsure -> 4
    }

// Exact ordering within the numeric family; both operands are IntVal or FloatVal.
private fun compareNumeric(
    a: IPrimitiveVal,
    b: IPrimitiveVal,
): Int =
    when {
        a is IntVal && b is IntVal -> a.core.compareTo(b.core)
        a is FloatVal && b is FloatVal -> compareDoubles(a.core, b.core)
        a is IntVal && b is FloatVal -> compareLongToDouble(a.core, b.core)
        a is FloatVal && b is IntVal -> -compareLongToDouble(b.core, a.core)
        else -> error("Non-numeric operands: $a, $b")
    }

// Double ordering with NaN above every number and -0.0 order-equivalent to 0.0.
private fun compareDoubles(
    a: Double,
    b: Double,
): Int =
    when {
        a.isNaN() || b.isNaN() -> a.isNaN().compareTo(b.isNaN())
        a == b -> 0
        else -> a.compareTo(b)
    }

// Exact Long-vs-Double ordering: lossless Double fast path, BigDecimal beyond 2^53.
private fun compareLongToDouble(
    long: Long,
    double: Double,
): Int =
    when {
        double.isNaN() -> -1
        double == Double.POSITIVE_INFINITY -> -1
        double == Double.NEGATIVE_INFINITY -> 1
        long in -DOUBLE_EXACT_LONG_BOUND..DOUBLE_EXACT_LONG_BOUND -> {
            val widened = long.toDouble()
            if (widened == double) 0 else widened.compareTo(double)
        }
        else -> BigDecimal(long).compareTo(BigDecimal(double))
    }
