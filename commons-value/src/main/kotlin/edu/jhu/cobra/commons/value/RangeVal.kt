package edu.jhu.cobra.commons.value

/**
 * Represents a range of integer values defined by a start and end boundary.
 *
 * @property start The starting value of the range.
 * @property endInclusive The ending value of the range, inclusive.
 */
public data class RangeVal(
    public val start: IntVal,
    public val endInclusive: IntVal,
) : ICollectionVal {
    override val core: List<IntVal> get() = listOf(start, endInclusive)

    /**
     * Returns the starting value of the range as a [Long].
     */
    public val first: Long get() = start.core

    /**
     * Returns the ending value of the range (inclusive) as a [Long].
     */
    public val last: Long get() = endInclusive.core

    /**
     * Constructs a [RangeVal] from two [Long] values representing the start and end.
     *
     * @param start The starting value of the range.
     * @param endInclusive The ending value of the range, inclusive.
     */
    public constructor(start: Long, endInclusive: Long) : this(IntVal(start), IntVal(endInclusive))

    /**
     * Constructs a [RangeVal] from two [Number] values representing the start and end.
     *
     * @param start The starting value of the range.
     * @param endInclude The ending value of the range, inclusive.
     */
    public constructor(start: Number, endInclude: Number) : this(start.toLong(), endInclude.toLong())

    /**
     * Checks if the specified number is within the range.
     *
     * Integral arguments are compared as [Long]. Floating-point arguments are compared exactly,
     * without rounding the range bounds to [Double]. NaN and infinite values are never contained.
     *
     * @param num The number to check.
     * @return `true` if the number is within the range, `false` otherwise.
     */
    public operator fun contains(num: Number): Boolean =
        when (num) {
            is Long, is Int, is Short, is Byte -> num.toLong() in this
            else -> containsExactly(num.toDouble())
        }

    // Long bounds above 2^53 are not exactly representable as Double; compare via BigDecimal.
    private fun containsExactly(num: Double): Boolean = num.isFinite() && num.toBigDecimal() in first.toBigDecimal()..last.toBigDecimal()

    /**
     * Checks if the specified [IntVal] is within the range.
     *
     * @param num The [IntVal] to check.
     * @return `true` if the [IntVal] is within the range, `false` otherwise.
     */
    public operator fun contains(num: IntVal): Boolean = first <= num.core && num.core <= last

    /**
     * Checks if the specified [RangeVal] is fully within this range.
     *
     * @param range The [RangeVal] to check.
     * @return `true` if the range is within this range, `false` otherwise.
     */
    public operator fun contains(range: RangeVal): Boolean = range.first in this && range.last in this

    /**
     * Checks if the specified [Long] is within the range.
     *
     * @param num The [Long] to check.
     * @return `true` if the value is within the range, `false` otherwise.
     */
    public operator fun contains(num: Long): Boolean = first <= num && num <= last

    /**
     * Determines if this range ends strictly before another range starts.
     *
     * The comparison is strict: two ranges sharing a boundary point (this range's end equals
     * the other range's start) are not before each other.
     *
     * @param range The range to compare.
     * @return `true` if this range ends strictly before the other starts, `false` otherwise.
     */
    public infix fun before(range: RangeVal): Boolean = this.last < range.first

    /**
     * Determines if this range starts strictly after another range ends.
     *
     * The comparison is strict: two ranges sharing a boundary point (this range's start equals
     * the other range's end) are not after each other.
     *
     * @param range The range to compare.
     * @return `true` if this range starts strictly after the other ends, `false` otherwise.
     */
    public infix fun after(range: RangeVal): Boolean = this.first > range.last

    /**
     * Combines two ranges into a new one, covering the smallest start to the largest end.
     *
     * @param range The other range to combine with.
     * @return A new [RangeVal] representing the combined range.
     */
    public operator fun plus(range: RangeVal): RangeVal {
        val newStart = if (this.first <= range.first) this.start else range.start
        val newEnd = if (this.last >= range.last) this.endInclusive else range.endInclusive
        return RangeVal(newStart, newEnd)
    }

    /**
     * Maps the start and end values using the provided transformation function.
     *
     * @param transform The transformation function to apply.
     * @return A list of two transformed values (start and end).
     */
    public fun <R> map(transform: (IntVal) -> R): List<R> = listOf(transform(start), transform(endInclusive))

    override fun toString(): String = "$first:$last"
}
