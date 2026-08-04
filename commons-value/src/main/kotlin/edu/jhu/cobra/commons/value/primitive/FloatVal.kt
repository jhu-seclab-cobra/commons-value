package edu.jhu.cobra.commons.value

/**
 * Represents a floating-point value backed by [Double].
 *
 * @property core The underlying [Double] value.
 */
data class FloatVal(
    override val core: Double,
) : IPrimitiveVal {
    /** Default constructor initializing the value to `0.0`. */
    constructor() : this(0.0)

    /** Converts this value to [Int], truncating the fractional part. */
    fun toInt(): Int = core.toInt()

    /** Converts this value to [Long], truncating the fractional part. */
    fun toLong(): Long = core.toLong()

    /** Converts this value to [Float]. */
    fun toFloat(): Float = core.toFloat()

    /** Converts this value to [IntVal], truncating the fractional part. */
    fun toIntVal(): IntVal = IntVal(core.toLong())

    operator fun compareTo(other: Double): Int = core.compareTo(other)

    override fun toString(): String = "FloatVal{$core}"
}
