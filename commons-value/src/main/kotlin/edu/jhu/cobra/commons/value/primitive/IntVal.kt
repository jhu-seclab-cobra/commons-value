package edu.jhu.cobra.commons.value

/**
 * Represents an integer value backed by [Long].
 *
 * @property core The underlying [Long] value.
 */
data class IntVal(override val core: Long) : IPrimitiveVal {

    /** Default constructor initializing the value to `0`. */
    constructor() : this(0L)

    /** Converts this value to [Int], truncating if necessary. */
    fun toInt(): Int = core.toInt()

    /** Converts this value to [Double]. */
    fun toDouble(): Double = core.toDouble()

    /** Converts this value to [Float]. */
    fun toFloat(): Float = core.toFloat()

    operator fun compareTo(other: Int): Int = core.compareTo(other.toLong())

    operator fun compareTo(other: Long): Int = core.compareTo(other)

    override fun toString(): String = "IntVal{$core}"
}
