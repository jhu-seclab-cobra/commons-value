package edu.jhu.cobra.commons.value

/**
 * Represents a boolean value in the storage system.
 *
 * Only two instances exist: [T] for `true` and [F] for `false`.
 * Use [BoolVal]`(true)` or [BoolVal]`(false)` to obtain the corresponding singleton.
 *
 * @property core The actual boolean value encapsulated by this instance.
 */
public class BoolVal private constructor(
    override val core: Boolean,
) : IPrimitiveVal {
    public companion object {
        /**
         * A constant instance representing the boolean value `true`.
         */
        public val T: BoolVal = BoolVal(true)

        /**
         * A constant instance representing the boolean value `false`.
         */
        public val F: BoolVal = BoolVal(false)

        /**
         * Returns the singleton [BoolVal] for the given boolean value.
         *
         * @param value The boolean value.
         * @return [T] if `true`, [F] if `false`.
         */
        public operator fun invoke(value: Boolean): BoolVal = if (value) T else F

        /**
         * Returns the singleton [BoolVal] for `false`.
         *
         * @return [F]
         */
        public operator fun invoke(): BoolVal = F
    }

    /**
     * Checks if the boolean value is `true`.
     *
     * @return `true` if the value is `true`, `false` otherwise.
     */
    public fun isTrue(): Boolean = core

    /**
     * Checks if the boolean value is `false`.
     *
     * @return `true` if the value is `false`, `false` otherwise.
     */
    public fun isFalse(): Boolean = !core

    override fun equals(other: Any?): Boolean = this === other || (other is BoolVal && core == other.core)

    override fun hashCode(): Int = core.hashCode()

    override fun toString(): String = "BoolVal{$core}"
}
