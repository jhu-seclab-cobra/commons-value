package edu.jhu.cobra.commons.value

/**
 * Represents a range of numeric values defined by a start and end boundary.
 *
 * @property start The starting value of the range.
 * @property endInclusive The ending value of the range, inclusive.
 */
data class RangeVal(val start: NumVal, val endInclusive: NumVal) : ICollectionVal {

    override val core: List<NumVal> get() = listOf(start, endInclusive)

    /**
     * Returns the starting value of the range as a [Number].
     */
    val first: Number get() = start.core

    /**
     * Returns the ending value of the range (inclusive) as a [Number].
     */
    val last: Number get() = endInclusive.core

    /**
     * Constructs a [RangeVal] from two numVals representing the start and end.
     *
     * @param start The starting value of the range.
     * @param endInclude The ending value of the range, inclusive.
     */
    constructor(start: Number, endInclude: Number) : this(start.numVal, endInclude.numVal)

    /**
     * Checks if the specified number is within the range.
     *
     * @param num The number to check.
     * @return `true` if the number is within the range, `false` otherwise.
     */
    operator fun contains(num: Number): Boolean =
        first.toDouble() <= num.toDouble() && num.toDouble() <= last.toDouble()

    /**
     * Checks if the specified [NumVal] is within the range.
     *
     * @param num The [NumVal] to check.
     * @return `true` if the [NumVal] is within the range, `false` otherwise.
     */
    operator fun contains(num: NumVal): Boolean =
        contains(num.core)

    /**
     * Checks if the specified [RangeVal] is fully within this range.
     *
     * @param range The [RangeVal] to check.
     * @return `true` if the range is within this range, `false` otherwise.
     */
    operator fun contains(range: RangeVal): Boolean =
        range.first in this && range.last in this

    /**
     * Determines if this range ends before another range starts.
     *
     * @param range The range to compare.
     * @return `true` if this range ends before the other starts, `false` otherwise.
     */
    infix fun before(range: RangeVal): Boolean =
        this.last.toDouble() <= range.first.toDouble()

    /**
     * Determines if this range starts after another range ends.
     *
     * @param range The range to compare.
     * @return `true` if this range starts after the other ends, `false` otherwise.
     */
    infix fun after(range: RangeVal): Boolean =
        this.first.toDouble() >= range.last.toDouble()

    /**
     * Combines two ranges into a new one, covering the smallest start to the largest end.
     *
     * @param range The other range to combine with.
     * @return A new [RangeVal] representing the combined range.
     */
    operator fun plus(range: RangeVal): RangeVal = RangeVal(
        minOf(this.first.toInt(), range.first.toInt()),
        maxOf(this.last.toInt(), range.last.toInt()),
    )

    /**
     * Maps the start and end values using the provided transformation function.
     *
     * @param transform The transformation function to apply.
     * @return A list of two transformed values (start and end).
     */
    fun <R> map(transform: (NumVal) -> R): List<R> = listOf(transform(start), transform(endInclusive))

    override fun toString(): String = "$first:$last"
}
