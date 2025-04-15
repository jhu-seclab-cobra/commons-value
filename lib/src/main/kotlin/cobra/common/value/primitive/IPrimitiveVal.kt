package cobra.common.value

/**
 * A sealed interface that represents a primitive value in the storage system.
 * This interface extends [IValue], providing a common structure for all primitive data types.
 * Implementations of this interface include types such as string, number, boolean, null, and uncertain values.
 *
 * Implementing classes:
 * - [StrVal]: String values.
 * - [NumVal]: Numeric values.
 * - [BoolVal]: Boolean values.
 * - [NullVal]: Null values.
 * - [Unsure]: Uncertain or undefined values.
 *
 * Example usage:
 * ```kotlin
 * val strVal: IPrimitiveVal = StrVal("Hello")
 * val numVal: IPrimitiveVal = NumVal(42)
 * val boolVal: IPrimitiveVal = BoolVal(true)
 * val unsureVal: IPrimitiveVal = Unsure.STR
 * ```
 *
 * @property core Holds the actual value of the primitive type.
 */
sealed interface IPrimitiveVal : IValue
