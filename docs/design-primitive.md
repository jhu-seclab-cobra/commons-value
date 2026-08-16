# commons-value Design -- Primitive Types

IR value type system for the Cobra static analysis engine.

Related design documents:
- [Collection types](design-collection.md)
- [Serializers](design-serializer.md)
- [Extensions and exceptions](design-conversions.md)

## Design Overview

- **Classes**: `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, `Unsure`
- **Relationships**: `IPrimitiveVal` extends `IValue` and `Comparable<IPrimitiveVal>`, `ICollectionVal` extends `IValue`
- **Abstract**: `IValue` (sealed, implemented by `IPrimitiveVal`, `ICollectionVal`), `IPrimitiveVal` (sealed, implemented by `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, `Unsure`), `ICollectionVal` (sealed, implemented by `ListVal`, `SetVal`, `MapVal`, `RangeVal`), `IValSerializer<Material>` (implemented by `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`)
- **Exceptions**: `IllegalArgumentException` raised by value conversion and serialization on unknown types; `ValFormatException` extends `IllegalArgumentException`, raised by deserialization on malformed material
- **Dependency roles**: Data holders: all value types, `Type`. Helpers: three serializer singletons (stateless, inputs by argument).

---

## Class / Type Specifications

### IValue

**Responsibility:** Root sealed interface for all IR value types.

**State/Fields:**
- `core: Any?` -- The actual data content of the value.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `deepCopy()` | Returns a structurally independent copy: immutable values return themselves; mutable collections copy recursively | -- | `IValue` (covariant overrides return the concrete type) | -- |

---

### IPrimitiveVal

**Responsibility:** Sealed interface for atomic, non-decomposable value types. Carries the total order over primitives.

**State/Fields:** Inherits `core` from `IValue`.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `compareTo(other: IPrimitiveVal)` | Total order: kind rank `NullVal < BoolVal < numeric < StrVal < Unsure`; within numeric, `IntVal` and `FloatVal` compare by exact numeric value (no Double rounding above 2^53), NaN above all numbers, `-0.0` equivalent to `0.0`; `StrVal` lexicographic; `BoolVal` `false < true`; `Unsure` by declaration order | `other: IPrimitiveVal` | `Int` | -- |

Ordering is inconsistent with `equals` in the `BigDecimal` sense: `IntVal(1)`, `FloatVal(1.0)` compare as equivalent but are not equal. Sorted containers keyed by primitives collapse such equivalents.

---

### StrVal

**Responsibility:** Wraps a `String` as an IR value with string manipulation utilities.

**State/Fields:**
- `core: String` -- The actual string content.

**Constructors:**
- `StrVal(core: String)` -- Primary constructor.
- `StrVal()` -- Default constructor creating empty string.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `startsWith(other: String)` | Checks if core starts with prefix | `other: String` | `Boolean` | -- |
| `substringAfter(delimiter: String)` | Returns substring after first delimiter occurrence | `delimiter: String` | `String` | -- |
| `substringBefore(delimiter: String)` | Returns substring before first delimiter occurrence | `delimiter: String` | `String` | -- |
| `equals(string: String, ignoreCase: Boolean)` | Case-optional string equality | `string: String`, `ignoreCase: Boolean` | `Boolean` | -- |
| `equals(value: IPrimitiveVal, ignoreCase: Boolean)` | Case-optional equality with another primitive's textual rendering; `NullVal` is never equal | `value: IPrimitiveVal`, `ignoreCase: Boolean` | `Boolean` | -- |
| `uppercase()` | Converts to uppercase | -- | `StrVal` | -- |
| `lowercase()` | Converts to lowercase | -- | `StrVal` | -- |
| `trim()` | Removes leading/trailing whitespace | -- | `StrVal` | -- |
| `contains(substring: String)` | Checks if core contains substring | `substring: String` | `Boolean` | -- |
| `get(index: Int)` | Returns char at index; negative indices count from end | `index: Int` | `Char` | `IndexOutOfBoundsException` |
| `get(index: IntVal)` | Returns char at IntVal index; a core outside Int range is out of bounds, never truncated | `index: IntVal` | `Char` | `IndexOutOfBoundsException` |

**Properties:**
- `length: Int` -- Number of characters in the string.

---

### IntVal

**Responsibility:** Wraps a `Long` as an IR integer value. All integer types (Byte, Short, Int, Long) are normalized to Long at construction.

**State/Fields:**
- `core: Long` -- The 64-bit integer content.

**Constructors:**
- `IntVal(core: Long)` -- Primary constructor.
- `IntVal()` -- Default constructor creating `0L`.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `toInt()` | Narrows to Int | -- | `Int` | -- |
| `toDouble()` | Widens to Double | -- | `Double` | -- |
| `toFloat()` | Narrows to Float | -- | `Float` | -- |
| `compareTo(other: Int)` | Compares with integer | `other: Int` | `Int` | -- |
| `compareTo(other: Long)` | Compares with long | `other: Long` | `Int` | -- |

---

### FloatVal

**Responsibility:** Wraps a `Double` as an IR floating-point value. All floating types (Float, Double) are normalized to Double at construction.

**State/Fields:**
- `core: Double` -- The IEEE 754 64-bit float content.

**Constructors:**
- `FloatVal(core: Double)` -- Primary constructor.
- `FloatVal()` -- Default constructor creating `0.0`.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `toInt()` | Truncates to Int | -- | `Int` | -- |
| `toLong()` | Truncates to Long | -- | `Long` | -- |
| `toFloat()` | Narrows to Float | -- | `Float` | -- |
| `toIntVal()` | Truncates to IntVal | -- | `IntVal` | -- |
| `compareTo(other: Double)` | Compares with double | `other: Double` | `Int` | -- |

---

### BoolVal

**Responsibility:** Wraps a `Boolean` as an IR value. Singleton-enforced: private constructor prevents external instantiation.

**State/Fields:**
- `core: Boolean` -- The actual boolean content.

**Constructors:** Private. Instances obtained only through companion factory methods.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `isTrue()` | Returns true if core is true | -- | `Boolean` | -- |
| `isFalse()` | Returns true if core is false | -- | `Boolean` | -- |

**Companion:**
- `T: BoolVal` -- Singleton for `true`.
- `F: BoolVal` -- Singleton for `false`.
- `invoke(value: Boolean): BoolVal` -- Factory returning `T` for true, `F` for false.
- `invoke(): BoolVal` -- Factory returning `F`.

---

### NullVal

**Responsibility:** Singleton representing a null value in the IR.

**State/Fields:**
- `core: null` -- Always null.

---

### Unsure

**Responsibility:** Enum representing uncertain/placeholder values for pattern matching scenarios.

**State/Fields:**
- `core: String` -- Internal identifier string (e.g., `__StrVal__`, `__NumVal__`).

**Entries:** `ANY`, `STR`, `NUM`, `BOOL`

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `new(core: String)` (companion) | Creates Unsure from identifier string | `core: String` | `Unsure?` | -- |
| `new(example: IPrimitiveVal)` (companion) | Infers Unsure type from example value | `example: IPrimitiveVal` | `Unsure` | -- |
| `new<T>()` (companion, reified) | Creates Unsure from generic type | -- | `Unsure` | -- |
| `contains(string: String)` (companion) | Checks if string is a valid Unsure identifier | `string: String` | `Boolean` | -- |

Domain constraints on value construction and serialization: [model.md](model.md).
