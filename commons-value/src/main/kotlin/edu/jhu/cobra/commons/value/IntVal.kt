package edu.jhu.cobra.commons.value

/**
 * Represents an integer value backed by [Long].
 *
 * @property core The underlying [Long] value.
 */
public data class IntVal(
    override val core: Long,
) : IPrimitiveVal {
    /** Default constructor initializing the value to `0`. */
    public constructor() : this(0L)

    /** Converts this value to [Int], truncating if necessary. */
    public fun toInt(): Int = core.toInt()

    /** Converts this value to [Double]. */
    public fun toDouble(): Double = core.toDouble()

    /** Converts this value to [Float]. */
    public fun toFloat(): Float = core.toFloat()

    /** Compares this value with [other], returning a negative, zero, or positive result. */
    public operator fun compareTo(other: Int): Int = core.compareTo(other.toLong())

    /** Compares this value with [other], returning a negative, zero, or positive result. */
    public operator fun compareTo(other: Long): Int = core.compareTo(other)

    override fun toString(): String = "IntVal{$core}"
}
