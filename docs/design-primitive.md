# commons-value Design -- Primitive Types

Language-neutral Value types and serializers for consuming analysis modules.

Related design documents:
- [Collection types](design-collection.md)
- [Serializers](design-serializer.md)
- [Extensions and exceptions](design-conversions.md)

## Design Overview

- **Classes**: `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, `Unsure`, `ListVal`, `SetVal`, `MapVal`, `RangeVal`, `Type`, `ValFormatException`, `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`
- **Relationships**: `IPrimitiveVal` extends `IValue` and `Comparable<IPrimitiveVal>`, `ICollectionVal` extends `IValue`; `ListVal` implements `MutableList<IValue>`, `SetVal` implements `MutableSet<IValue>`, `MapVal` implements `MutableMap<String, IValue>` (each by delegation to its `core`); `RangeVal` contains two `IntVal`; serializers use `Type` and the value types (one-way: no value type uses a serializer)
- **Abstract**: `IValue` (sealed, implemented by `IPrimitiveVal`, `ICollectionVal`), `IPrimitiveVal` (sealed, implemented by `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, `Unsure`), `ICollectionVal` (sealed, implemented by `ListVal`, `SetVal`, `MapVal`, `RangeVal`), `IValSerializer<Material>` (implemented by `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`)
- **Exceptions**: `IllegalArgumentException` raised by value conversion and serialization on unknown types; `ValFormatException` extends `IllegalArgumentException`, raised by deserialization on malformed material
- **Dependency roles**: Data holders: all value types, `Type`. Helpers: three serializer singletons (stateless, inputs by argument).
- **Visibility**: Module `commons-value`, package `edu.jhu.cobra.commons.value` (values, conversions) and `edu.jhu.cobra.commons.value.serializer` (serializers). Every listed type and every extension in [design-conversions.md](design-conversions.md) is public. `MAX_NESTING_DEPTH`, `checkNestingDepth`, the depth-carrying `deepCopy(depth)` overloads, `hashCapacityFor`, and the WireValidation helpers are internal.
- **Type selection**: `IValue`, `IPrimitiveVal`, `ICollectionVal` are sealed interfaces; `IntVal`, `FloatVal`, `RangeVal` are data classes; `NullVal` is a data object; `Unsure` and `Type` are enum classes; `StrVal` is a final class whose constructor canonicalizes its argument; `BoolVal` is a final class with a private constructor; the three serializers are objects.

---

## Class / Type Specifications

`IValue` represents literal content, uncertain primitive values, generic external data and supported interchange formats. Graph or consumer schemas own immutable structural records and their graph-reference meaning; they do not extend the sealed Value hierarchy. Mutable content ownership follows [collection capture](design-collection.md#mutable-collection-contract-listval-setval-mapval); adapter conversion follows [serializer design](design-serializer.md#design-overview). The library has no logging facade.

### IValue

**Responsibility:** Root sealed interface for all Value types.

**State/Fields:**
- `core: Any?` -- The actual data content of the value.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `deepCopy()` | Returns a structurally independent copy; immutable values return themselves and mutable containers copy recursively | -- | `IValue` (covariant overrides return the concrete type) | `IllegalArgumentException` when collection nesting exceeds the supported depth, including cycles |

---

### IPrimitiveVal

**Responsibility:** Sealed interface for atomic, non-decomposable value types. Carries the total order over primitives.

**State/Fields:** Inherits `core` from `IValue`.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `compareTo(other: IPrimitiveVal)` | Total order: kind rank `NullVal < BoolVal < numeric < StrVal < Unsure`; within numeric, `IntVal` and `FloatVal` compare by exact numeric value (no Double rounding above 2^53), NaN above all numbers, `-0.0` equivalent to `0.0`; `StrVal` lexicographic; `BoolVal` `false < true`; `Unsure` by declaration order. The order is inconsistent with `equals`: equal-valued members of different numeric kinds compare as equivalent yet remain distinct values, so sorted containers keyed by primitives collapse such equivalents | `other: IPrimitiveVal` | `Int` | -- |

---

### StrVal

**Responsibility:** Wraps a `String` as a Value that stands for a byte sequence, with string manipulation utilities.

**State/Fields:**
- `core: String` -- The canonical text of the string's bytes ([model invariants](model.md#invariants)): each well-formed UTF-8 sequence is its character, and each other byte `XX` in 80..FF is the unpaired unit U+DCXX. Equality, `hashCode` and `toString` (`StrVal{<core>}`) read `core`.

**Constructors:**
- `StrVal(core: String)` -- Primary constructor; total, never throws. It stores `core` unchanged when `core` holds no unit in D800..DFFF, which allocates nothing. Otherwise it keeps each valid surrogate pair, re-decodes each run of units U+DC80..U+DCFF from its bytes, so units that form a well-formed UTF-8 sequence become that character, and maps each other unpaired surrogate to the three bytes of its generalized UTF-8 form, which are three units U+DC80..U+DCFF. Example: `StrVal("\uDCC3\uDCA9").core` is `"é"`, and `StrVal("\uD800").core` is `"\uDCED\uDCA0\uDC80"`.
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

The methods above are text operations on `core`; a byte operation reads `toByteArray()`.

**Properties:**
- `length: Int` -- Number of UTF-16 code units in the string.

**Extension functions:**
- `StrVal.toByteArray(): ByteArray` -- The byte sequence the string stands for: the UTF-8 bytes of each character and byte `XX` for each unpaired unit U+DCXX. `bytes.strVal.toByteArray()` equals `bytes` for every byte array, and `value.toByteArray().strVal` equals `value` for every `StrVal`.
- `String.indexOfByteOutsideUtf8(startIndex: Int = 0): Int` -- The index of the first unpaired unit U+DC80..U+DCFF at or after `startIndex`, or -1 when there is none. A high surrogate followed by a low surrogate is one pair, so the low unit DC80 of U+1F480 (D83D DC80) is never returned; `startIndex` is the index of a character's first unit. One scan of the UTF-16 units, without allocation. Applied to a `core`, it finds the units that stand for a byte outside UTF-8.

---

### IntVal

**Responsibility:** Wraps a `Long` as an integer Primitive Value. All integer types (Byte, Short, Int, Long) are normalized to Long at construction.

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

**Responsibility:** Wraps a `Double` as a floating-point Primitive Value. All floating types (Float, Double) are normalized to Double at construction.

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

**Responsibility:** Wraps a `Boolean` as a Value. Singleton-enforced: private constructor prevents external instantiation.

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

**Responsibility:** Singleton representing a null Primitive Value.

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
