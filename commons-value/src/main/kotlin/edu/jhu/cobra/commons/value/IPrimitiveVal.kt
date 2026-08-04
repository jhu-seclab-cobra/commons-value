package edu.jhu.cobra.commons.value

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
public sealed interface IPrimitiveVal : IValue
