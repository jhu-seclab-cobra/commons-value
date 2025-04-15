package cobra.common.value

/**
 * Represents a range of integer values, providing range operations.
 *
 * @property core The internal list of start and end values.
 * @property first The starting value of the range.
 * @property last The ending value of the range, inclusive.
 */
data class RangeVal(override val core: ArrayList<NumVal> = ArrayList(2)) : ICollectionVal {

    /**
     * Returns the first (starting) value of the range.
     */
    val first: Number get() = core.first().core

    /**
     * Returns the last (ending) value of the range, inclusive.
     */
    val last: Number get() = core.last().core

    /**
     * Constructs a [RangeVal] from two numVals representing the start and end.
     *
     * @param start The starting value of the range.
     * @param endInclude The ending value of the range, inclusive.
     */
    constructor(start: NumVal, endInclude: NumVal) : this(start.toInt(), endInclude.toInt())

    /**
     * Constructs a [RangeVal] from two number values representing the start and end.
     *
     * @param start The starting value of the range.
     * @param endInclude The ending value of the range, inclusive.
     */
    constructor(start: Number, endInclude: Number) : this(arrayListOf(start.toInt().numVal, endInclude.toInt().numVal))


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
     * Maps each value in the range to a new form using the provided transformation function.
     *
     * @param transform The transformation function to apply.
     * @return A list of the transformed values.
     */
    fun <R> map(transform: (NumVal) -> R): List<R> = core.map(transform)

    override fun toString(): String = "$first:$last"

}
