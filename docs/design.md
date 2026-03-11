# commons-value Design

## Design Overview

- **Classes**: `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`, `ListVal`, `SetVal`, `MapVal`, `RangeVal`, `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl`, `Type`
- **Relationships**: `StrVal` implements `IPrimitiveVal`, `NumVal` implements `IPrimitiveVal`, `BoolVal` implements `IPrimitiveVal`, `NullVal` implements `IPrimitiveVal`, `Unsure` implements `IPrimitiveVal`, `ListVal` implements `ICollectionVal`, `SetVal` implements `ICollectionVal`, `MapVal` implements `ICollectionVal`, `RangeVal` implements `ICollectionVal`, `IPrimitiveVal` extends `IValue`, `ICollectionVal` extends `IValue`, `DftByteArraySerializerImpl` implements `IValSerializer<ByteArray>`, `DftByteBufferSerializerImpl` implements `IValSerializer<ByteBuffer>`, `DftCharBufferSerializerImpl` implements `IValSerializer<CharBuffer>`
- **Abstract**: `IValue` (sealed, implemented by `IPrimitiveVal`, `ICollectionVal`), `IPrimitiveVal` (sealed, implemented by `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`), `ICollectionVal` (sealed, implemented by `ListVal`, `SetVal`, `MapVal`, `RangeVal`), `IValSerializer<Material>` (implemented by three serializer objects)
- **Exceptions**: `IllegalArgumentException` raised by value conversion and serialization on unknown types
- **Dependency roles**: Data holders: `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`, `ListVal`, `SetVal`, `MapVal`, `RangeVal`, `Type`. Helpers: `DftByteArraySerializerImpl`, `DftByteBufferSerializerImpl`, `DftCharBufferSerializerImpl` (stateless singleton serializers, inputs by argument).

---

## Class / Type Specifications

### IValue

**Responsibility:** Root sealed interface for all value types in the system.

**State/Fields:**
- `core: Any?` — The actual data content of the value.

---

### IPrimitiveVal

**Responsibility:** Sealed interface for atomic, non-decomposable value types.

**State/Fields:** Inherits `core` from `IValue`.

---

### StrVal

**Responsibility:** Wraps a `String` as a system value with string manipulation utilities.

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

**Responsibility:** Wraps a `Number` as a system value with numeric type introspection and conversion.

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

**Responsibility:** Wraps a `Boolean` as a system value with boolean utilities.

**State/Fields:**
- `core: Boolean` — The actual boolean content.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `isTrue()` | Returns true if core is true | — | `Boolean` | — |
| `isFalse()` | Returns true if core is false | — | `Boolean` | — |

**Companion Constants:**
- `T: BoolVal` — Singleton for `true`.
- `F: BoolVal` — Singleton for `false`.

---

### NullVal

**Responsibility:** Singleton representing a null value in the system.

**State/Fields:**
- `core: null` — Always null.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `isNull(value: IValue)` | Checks if value is NullVal | `value: IValue` | `Boolean` | — |
| `isNotNull(value: IValue)` | Checks if value is not NullVal | `value: IValue` | `Boolean` | — |

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

### ListVal

**Responsibility:** Wraps an `ArrayList<IValue>` as a mutable ordered collection value.

**State/Fields:**
- `core: ArrayList<IValue>` — The internal list storage.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `get(index: Int)` | Returns element at index | `index: Int` | `IValue` | `IndexOutOfBoundsException` |
| `set(index: Int, value: IValue)` | Replaces element at index | `index: Int`, `value: IValue` | — | `IndexOutOfBoundsException` |
| `contains(value: IValue)` | Checks element membership | `value: IValue` | `Boolean` | — |
| `containsAll(values: Collection<IValue>)` | Checks all elements present | `values: Collection<IValue>` | `Boolean` | — |
| `indexOf(value: IValue)` | First index of element, or -1 | `value: IValue` | `Int` | — |
| `lastIndexOf(value: IValue)` | Last index of element, or -1 | `value: IValue` | `Int` | — |
| `subList(fromIndex: Int, toIndex: Int)` | Returns sub-range as new ListVal | `fromIndex: Int`, `toIndex: Int` | `ListVal` | `IndexOutOfBoundsException`, `IllegalArgumentException` |
| `plus(value: IValue)` | Returns new ListVal with element appended | `value: IValue` | `ListVal` | — |
| `plusAssign(value: IValue)` | Mutably appends element | `value: IValue` | — | — |
| `minus(value: IValue)` | Returns new ListVal with element removed | `value: IValue` | `ListVal` | — |
| `minusAssign(value: IValue)` | Mutably removes element | `value: IValue` | — | — |
| `isEmpty()` / `isNotEmpty()` | Emptiness checks | — | `Boolean` | — |
| `map(transform)` | Transforms each element | `transform: (IValue) -> R` | `List<R>` | — |
| `flatMap(transform)` | Flat-maps each element | `transform: (IValue) -> List<R>` | `List<R>` | — |
| `forEach(action)` | Iterates each element | `action: (IValue) -> Unit` | — | — |
| `asSequence()` | Returns lazy sequence | — | `Sequence<IValue>` | — |

**Properties:**
- `size: Int` — Number of elements.

---

### SetVal

**Responsibility:** Wraps a `LinkedHashSet<IValue>` as a mutable ordered set value.

**State/Fields:**
- `core: LinkedHashSet<IValue>` — The internal set storage (preserves insertion order).

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `add(new: IValue)` | Adds element, returns true if new | `new: IValue` | `Boolean` | — |
| `remove(prev: IValue)` | Removes element, returns true if present | `prev: IValue` | `Boolean` | — |
| `plus(new: IValue)` | Returns new SetVal with element added | `new: IValue` | `SetVal` | — |
| `plusAssign(value: IValue)` | Mutably adds element | `value: IValue` | — | — |
| `minus(prev: IValue)` | Returns new SetVal with element removed | `prev: IValue` | `SetVal` | — |
| `minusAssign(prev: IValue)` | Mutably removes element | `prev: IValue` | — | — |
| `contains(value: IValue)` | Checks element membership | `value: IValue` | `Boolean` | — |
| `containsAll(values: Collection<IValue>)` | Checks all elements present | `values: Collection<IValue>` | `Boolean` | — |
| `isEmpty()` / `isNotEmpty()` | Emptiness checks | — | `Boolean` | — |
| `map(transform)` | Transforms each element | `transform: (IValue) -> R` | `List<R>` | — |
| `forEach(action)` | Iterates each element | `action: (IValue) -> Unit` | — | — |
| `asSequence()` | Returns lazy sequence | — | `Sequence<IValue>` | — |
| `toList()` | Converts to list | — | `List<IValue>` | — |

**Properties:**
- `size: Int` — Number of elements.

---

### MapVal

**Responsibility:** Wraps a `HashMap<String, IValue>` as a mutable string-keyed map value.

**State/Fields:**
- `core: HashMap<String, IValue>` — The internal map storage (String keys only).

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `get(key: String)` | Returns value for key, or null | `key: String` | `IValue?` | — |
| `set(key: String, value: IValue)` | Sets key-value pair | `key: String`, `value: IValue` | — | — |
| `add(key: String, value: IValue)` | Adds key-value pair | `key: String`, `value: IValue` | `IValue?` | — |
| `plus(pair: Pair<String, IValue>)` | Adds pair | `pair: Pair<String, IValue>` | `IValue?` | — |
| `minus(key: String)` | Removes by key | `key: String` | `IValue?` | — |
| `remove(key: String)` | Removes by key | `key: String` | `IValue?` | — |
| `keys()` | Returns all keys | — | `Set<String>` | — |
| `values()` | Returns all values | — | `Collection<IValue>` | — |
| `contains(key: String)` | Checks key presence | `key: String` | `Boolean` | — |
| `isEmpty()` | Emptiness check | — | `Boolean` | — |
| `forEach(action)` | Iterates each entry | `action: (Map.Entry<String, IValue>) -> Unit` | — | — |
| `map(behavior)` | Transforms each entry | `behavior: (Map.Entry<String, IValue>) -> R` | `List<R>` | — |
| `mapValues(behavior)` | Transforms values | `behavior: (Map.Entry<String, IValue>) -> R` | `Map<String, R>` | — |
| `flatMap(behavior)` | Flat-maps entries | `behavior: (Map.Entry<String, IValue>) -> Iterable<R>` | `List<R>` | — |
| `toList()` | Converts to pair list | — | `List<Pair<String, IValue>>` | — |
| `toTypeArray()` | Converts to pair array | — | `Array<Pair<String, IValue>>` | — |

**Properties:**
- `size: Int` — Number of key-value pairs.

---

### RangeVal

**Responsibility:** Represents an inclusive numeric range as a collection value.

**State/Fields:**
- `core: ArrayList<NumVal>` — Internal list holding exactly two NumVal elements (start and end).

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `contains(num: Number)` | Checks if number is within range | `num: Number` | `Boolean` | — |
| `contains(num: NumVal)` | Checks if NumVal is within range | `num: NumVal` | `Boolean` | — |
| `contains(range: RangeVal)` | Checks if sub-range is fully contained | `range: RangeVal` | `Boolean` | — |
| `before(range: RangeVal)` | Checks if this range ends before other starts | `range: RangeVal` | `Boolean` | — |
| `after(range: RangeVal)` | Checks if this range starts after other ends | `range: RangeVal` | `Boolean` | — |
| `plus(range: RangeVal)` | Combines two ranges into their union bounds | `range: RangeVal` | `RangeVal` | — |
| `map(transform)` | Transforms start and end values | `transform: (NumVal) -> R` | `List<R>` | — |

**Properties:**
- `first: Number` — Start of range.
- `last: Number` — End of range (inclusive).

---

### IValSerializer\<Material\>

**Responsibility:** Interface defining serialization and deserialization of IValue to/from a material format.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `serialize(value: IValue)` | Encodes value into material format | `value: IValue` | `Material` | `IllegalArgumentException` on unknown type |
| `deserialize(material: Material)` | Decodes material into IValue | `material: Material` | `IValue` | `IllegalArgumentException` on unknown type |

---

### DftByteArraySerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteArray` using binary format with type-byte prefix.

**Serialization Format:** `[type_byte][payload_bytes]`. Collections use length-prefixed elements. Maps encode key bytes and value bytes with size prefixes.

---

### DftByteBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteBuffer` using binary format with type-byte prefix and element count for collections.

**Serialization Format:** `[type_byte][count (for collections)][elements]`. Strings are length-prefixed. Booleans use distinct type bytes (BOOL_TRUE/BOOL_FALSE). Ranges store two ints directly.

---

### DftCharBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `CharBuffer` using human-readable text format.

**Serialization Format:** `TypeStr:payload:`. Strings use hex length prefix. Collections use hex element count. Maps encode keys as serialized StrVal with `=` delimiter.

---

### Type

**Responsibility:** Enum mapping each value type to a unique byte tag and string label for serialization.

**State/Fields:**
- `byte: Byte` — Binary type tag.
- `str: String` — String type label.

**Entries:** NULL(10), STR(20), BOOL(30), BOOL_TRUE(31), BOOL_FALSE(32), UNSURE_ANY(40), UNSURE_STR(41), UNSURE_NUM(42), UNSURE_BOOL(43), NUM_BYTE(50), NUM_SHORT(51), NUM_INT(52), NUM_LONG(53), NUM_FLOAT(54), NUM_DOUBLE(55), NUM_OTHERS(56), RANGE(60), LIST(70), SET(71), MAP(80).

---

## Function Specifications

### PrimitiveUtils (extension functions)

**`Number.numVal: NumVal`** — Wraps Number as NumVal.

**`String.numVal: NumVal`** — Parses string to number, returns NumVal. Uses locale-aware NumberFormat. Returns Int for integer values within Int range, Long for larger integers, preserves Double for decimals. Throws `ParseException` on invalid input.

**`String.strVal: StrVal`** — Wraps String as StrVal.

**`Char.strVal: StrVal`** — Wraps Char (as String) as StrVal.

**`Path.strVal: StrVal`** — Wraps Path string representation as StrVal.

**`File.strVal: StrVal`** — Wraps File path as StrVal.

**`Boolean.boolVal: BoolVal`** — Returns `BoolVal.T` for true, `BoolVal.F` for false.

**`Any?.primitiveVal: IPrimitiveVal`** — Converts null/Number/String/Boolean/IPrimitiveVal to corresponding IPrimitiveVal. Throws `IllegalArgumentException` for unsupported types.

**`IPrimitiveVal.compareTo(other: IPrimitiveVal): Int`** — Compares two primitives of same type. NumVal by Double, StrVal by String, BoolVal by Boolean, NullVal always equal. Throws `IllegalArgumentException` for cross-type comparison.

**`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** — Escapes special regex chars, replaces Unsure placeholders with regex patterns (ANY/STR -> `.*`, NUM -> `\d+`, BOOL -> `(true|false)`).

**`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** — Returns regex pattern corresponding to Unsure type.

**`String.startsWith(other: StrVal): Boolean`** — Checks if string starts with StrVal's content.

### CollectionUtils (extension properties)

**`Collection<*>.listVal: ListVal`** — Converts collection to ListVal via `toVal` on each element.

**`Collection<*>.setVal: SetVal`** — Converts collection to SetVal via `toVal` on each element.

**`Set<*>.setVal: SetVal`** — Converts set to SetVal via `toVal` on each element.

**`Map<*, *>.mapVal: MapVal`** — Converts map to MapVal; keys toString'd, values via `toVal`.

**`IntRange.rangeVal: RangeVal`** — Converts IntRange to RangeVal.

**`ListVal?.orEmpty(): ListVal`** — Returns self or empty ListVal if null.

**`SetVal?.orEmpty(): SetVal`** — Returns self or empty SetVal if null.

**`MapVal?.orEmpty(): MapVal`** — Returns self or empty MapVal if null.

### Utils (top-level extension)

**`Any?.toVal: IValue`** — Universal converter: null -> NullVal, Number -> NumVal, String -> StrVal, Boolean -> BoolVal, List -> ListVal, Map -> MapVal, IntRange -> RangeVal, Set -> SetVal, IValue -> identity. Throws `IllegalArgumentException` for unsupported types.

### SerializerUtils (extension functions)

**`String.asNumber(): Number`** — Parses string to Number via Apache Commons NumberUtils.

**`String.asHexInt(): Int`** — Parses hex string to Int.

**`Int.asHexString(): String`** — Converts Int to hex string.

**`ByteBuffer.getArray(size: Int): ByteArray`** — Reads size bytes from buffer.

**`ByteBuffer.getString(size: Int? = null): String`** — Reads string from buffer; if size is null, reads int prefix then that many bytes.

**`ByteBuffer.put(type: Type): ByteBuffer`** — Writes Type's byte tag to buffer.

**`ByteBuffer.typedFlip(): ByteBuffer`** — Type-safe flip for Java 8 compatibility.

**`CharBuffer.typedFlip(): CharBuffer`** — Type-safe flip for Java 8 compatibility.

**`CharBuffer.typedPosition(pos: Int): CharBuffer`** — Type-safe position for Java 8 compatibility.

**`CharBuffer.remove(until: Char): Boolean`** — Advances position past delimiter char.

**`CharBuffer.getBuffer(until: Char): CharBuffer`** — Reads chars into new buffer until delimiter.

**`CharBuffer.getBuffer(size: Int): CharBuffer`** — Reads fixed-size chars into new buffer.

**`CharBuffer.getString(until: Char): String`** — Reads chars as string until delimiter.

**`CharBuffer.getString(size: Int): String`** — Reads fixed-size chars as string.

**`String.asCharBuffer(): CharBuffer`** — Wraps string as CharBuffer.

**`DataInput.asByteArray(size: Int): ByteArray`** — Reads bytes from DataInput; size=0 returns empty, size>0 reads exactly that many, size<0 reads until EOF.

**`DataInput.asByteSequence(available: Int): Sequence<Byte>`** — Lazy byte sequence from DataInput; available=0 returns empty, available>0 yields that many, available<0 reads until EOF.

---

## Exception / Error Types

| Exception | When Raised |
|-----------|------------|
| `IllegalArgumentException` | `Any?.toVal` / `Any?.primitiveVal` called on unsupported type; `IPrimitiveVal.compareTo` with incompatible types; serializer encounters unknown IValue subtype; deserializer encounters unknown type tag; empty ByteBuffer deserialization |
| `IndexOutOfBoundsException` | `ListVal.get`/`set`/`subList` with out-of-range index; `StrVal.get` with out-of-range index |
| `ParseException` | `String.numVal` when string is not a valid number |
| `NumberFormatException` | `String.asNumber()` / `String.asHexInt()` on invalid input |
| `BufferUnderflowException` | ByteBuffer/CharBuffer read operations when insufficient data remains |

---

## Validation Rules

### Value Creation
- `NumVal` accepts any `Number` subtype (Byte, Short, Int, Long, Float, Double, or others).
- `StrVal` accepts any `String` (including empty).
- `BoolVal` accepts `true` or `false`.
- `NullVal` is a singleton; `core` is always `null`.
- `Unsure` has exactly four entries: `ANY`, `STR`, `NUM`, `BOOL`.

### Collection Constraints
- `ListVal` may contain any mix of `IValue` subtypes, including nested collections.
- `SetVal` preserves insertion order via `LinkedHashSet`.
- `MapVal` keys are `String` only; values are any `IValue`.
- `RangeVal` core always contains exactly two `NumVal` elements (start and end).

### Serialization
- Every serialized value starts with a type tag (byte or string).
- Type tags are unique and non-overlapping across all value types.
- Collection serializers use element count for ByteBuffer/CharBuffer formats, and length-prefixed elements for ByteArray format.
- Deserialization must reconstruct the exact same `IValue` subtype as the original.
