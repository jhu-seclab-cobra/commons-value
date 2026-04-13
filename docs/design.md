# commons-value Design

IR value type system for the Cobra static analysis engine.

Related design documents:
- [Collection types](design-collection.md)
- [Serializers](design-serializer.md)
- [Extension functions and exceptions](design-utils.md)

## Design Overview

- **Classes**: `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`, `ListVal`, `SetVal`, `MapVal`, `RangeVal`, `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`, `Type`
- **Relationships**: `IPrimitiveVal` extends `IValue`, `ICollectionVal` extends `IValue`
- **Abstract**: `IValue` (sealed, implemented by `IPrimitiveVal`, `ICollectionVal`), `IPrimitiveVal` (sealed, implemented by `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`), `ICollectionVal` (sealed, implemented by `ListVal`, `SetVal`, `MapVal`, `RangeVal`), `IValSerializer<Material>` (implemented by `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`)
- **Exceptions**: `IllegalArgumentException` raised by value conversion and serialization on unknown types
- **Dependency roles**: Data holders: all value types, `Type`. Helpers: three serializer singletons (stateless, inputs by argument).

---

## Class / Type Specifications

### IValue

**Responsibility:** Root sealed interface for all IR value types.

**State/Fields:**
- `core: Any?` — The actual data content of the value.

---

### IPrimitiveVal

**Responsibility:** Sealed interface for atomic, non-decomposable value types.

**State/Fields:** Inherits `core` from `IValue`.

---

### StrVal

**Responsibility:** Wraps a `String` as an IR value with string manipulation utilities.

**State/Fields:**
- `core: String` — The actual string content.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `startsWith(other: String)` | Checks if core starts with prefix | `other: String` | `Boolean` | — |
| `substringAfter(delimiter: String)` | Returns substring after first delimiter occurrence | `delimiter: String` | `String` | — |
| `substringBefore(delimiter: String)` | Returns substring before first delimiter occurrence | `delimiter: String` | `String` | — |
| `equals(string: String, ignoreCase: Boolean)` | Case-optional string equality | `string: String`, `ignoreCase: Boolean` | `Boolean` | — |
| `equals(value: IPrimitiveVal, ignoreCase: Boolean)` | Case-optional equality with another primitive | `value: IPrimitiveVal`, `ignoreCase: Boolean` | `Boolean` | — |
| `uppercase()` | Converts to uppercase | — | `StrVal` | — |
| `lowercase()` | Converts to lowercase | — | `StrVal` | — |
| `trim()` | Removes leading/trailing whitespace | — | `StrVal` | — |
| `contains(substring: String)` | Checks if core contains substring | `substring: String` | `Boolean` | — |
| `get(index: Int)` | Returns char at index; negative indices count from end | `index: Int` | `Char` | `IndexOutOfBoundsException` |
| `get(index: NumVal)` | Returns char at NumVal index | `index: NumVal` | `Char` | `IndexOutOfBoundsException` |

**Properties:**
- `length: Int` — Number of characters in the string.

---

### NumVal

**Responsibility:** Wraps a `Number` as an IR value with numeric type introspection and conversion.

**State/Fields:**
- `core: Number` — The actual numeric content (Byte, Short, Int, Long, Float, Double, or other Number).

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `toInt()` | Converts to Int | — | `Int` | — |
| `toDouble()` | Converts to Double | — | `Double` | — |
| `toFloat()` | Converts to Float | — | `Float` | — |
| `toLong()` | Converts to Long | — | `Long` | — |
| `toShort()` | Converts to Short | — | `Short` | — |
| `compareTo(other: Int)` | Compares with an integer | `other: Int` | `Int` | — |
| `truncate(numVal: NumVal)` (companion) | Truncates to smallest integer type without precision loss | `numVal: NumVal` | `NumVal` | — |

**Properties:**
- `isInt: Boolean`, `isLong: Boolean`, `isShort: Boolean`, `isByte: Boolean`, `isFloat: Boolean`, `isDouble: Boolean` — Type introspection.
- `isPrimitiveIntegerType: Boolean` — True if Byte, Short, Int, or Long.
- `isPrimitiveFloatingType: Boolean` — True if Float or Double.

---

### BoolVal

**Responsibility:** Wraps a `Boolean` as an IR value. Singleton-enforced: private constructor prevents external instantiation.

**State/Fields:**
- `core: Boolean` — The actual boolean content.

**Constructors:** Private. Instances obtained only through companion factory methods.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `isTrue()` | Returns true if core is true | — | `Boolean` | — |
| `isFalse()` | Returns true if core is false | — | `Boolean` | — |

**Companion:**
- `T: BoolVal` — Singleton for `true`.
- `F: BoolVal` — Singleton for `false`.
- `invoke(value: Boolean): BoolVal` — Factory returning `T` for true, `F` for false.
- `invoke(): BoolVal` — Factory returning `F`.

---

### NullVal

**Responsibility:** Singleton representing a null value in the IR.

**State/Fields:**
- `core: null` — Always null.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `infix isNull(value: IValue)` | Checks if value is NullVal | `value: IValue` | `Boolean` | — |
| `infix isNotNull(value: IValue)` | Checks if value is not NullVal | `value: IValue` | `Boolean` | — |

---

### Unsure

**Responsibility:** Enum representing uncertain/placeholder values for pattern matching scenarios.

**State/Fields:**
- `core: String` — Internal identifier string (e.g., `__StrVal__`, `__NumVal__`).

**Entries:** `ANY`, `STR`, `NUM`, `BOOL`

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `new(core: String)` (companion) | Creates Unsure from identifier string | `core: String` | `Unsure?` | — |
| `new(example: IPrimitiveVal)` (companion) | Infers Unsure type from example value | `example: IPrimitiveVal` | `Unsure` | — |
| `new<T>()` (companion, reified) | Creates Unsure from generic type | — | `Unsure` | — |
| `contains(string: String)` (companion) | Checks if string is a valid Unsure identifier | `string: String` | `Boolean` | — |

---

## Validation Rules

### Value Creation
- `NumVal` accepts any `Number` subtype (Byte, Short, Int, Long, Float, Double, or others).
- `ListVal` may contain any mix of `IValue` subtypes, including nested collections.
- `MapVal` keys are `String` only; values are any `IValue`.

### Serialization
- Every serialized value starts with a type tag (byte or string).
- Type tags are unique and non-overlapping across all value types.
- Deserialization reconstructs the exact same `IValue` subtype as the original.
