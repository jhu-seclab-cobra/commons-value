# commons-value Design -- Serializers

Part of [commons-value design](design-primitive.md). Specifies the `IValSerializer` interface and implementations.

---

## Design Overview

Public IValSerializer implementations and Type are in edu.jhu.cobra.commons.value.serializer, module commons-value. The three stateless objects own distinct material formats and use internal WireValidation helpers. The algorithm owner is [serialization](spec-serialization.md).

## Class / Type Specifications

### IValSerializer\<Material : Any\>

**Responsibility:** Interface defining serialization and deserialization of IValue to/from a material format. `Material` is upper-bounded by `Any` (non-nullable). Declares `ValFormatException`, the single malformed-input error type.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `serialize(value: IValue)` | Encodes value into material format | `value: IValue` | `Material` | `IllegalArgumentException` on nesting deeper than `MAX_NESTING_DEPTH` or string content with unpaired surrogates |
| `deserialize(material: Material)` | Decodes material into IValue; consumes the material exactly | `material: Material` | `IValue` | `ValFormatException` on any malformed material |

**Deserialization contract (all implementations):**
- Every malformed-material failure raises `ValFormatException`: unknown tag, truncated payload, invalid size/count prefix, unparsable number, malformed UTF-8 in string or key bytes, nesting deeper than `MAX_NESTING_DEPTH`, empty/exhausted material, trailing material after the top-level value. Underlying `BufferUnderflowException`/`NumberFormatException`/`CharacterCodingException` are wrapped at the serializer boundary, never leaked.
- Full consumption: after decoding the top-level value, remaining material is malformed.
- Nested length-prefixed parses assert that the nested decode consumed its window exactly (position equals window end).

---

### ValFormatException

**Responsibility:** Signals malformed serialized material. Extends `IllegalArgumentException`; carries the original cause when wrapping a lower-level exception. Declared in `IValSerializer.kt`.

---

### DftByteArraySerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteArray` using binary format with type-byte prefix.

**Serialization Format:** `[type_byte][payload_bytes]`. Lists and sets use size-prefixed element blocks. Maps encode key bytes and value bytes with size prefixes. Ranges encode a size prefix for the first bound followed by two tagged 8-byte Longs. Strings carry no length prefix: the payload extends to the end of the value's window.

---

### DftByteBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteBuffer` using binary format with type-byte prefix and element count for collections.

**Serialization Format:** `[type_byte][count (for collections)][elements]`. Strings are length-prefixed. Booleans use distinct type bytes (BOOL_TRUE/BOOL_FALSE). IntVal serializes as 8-byte Long. FloatVal serializes as 8-byte Double. Ranges store two tagged 8-byte Longs (INT tag before each bound).

---

### DftCharBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `CharBuffer` using human-readable text format.

**Serialization Format:** Null, boolean and uncertain forms are tag-only; numbers and ranges are delimiter-terminated; strings have hexadecimal UTF-16-code-unit lengths; collections have hexadecimal element counts. Maps encode keys as serialized StrVal with an `=` delimiter.

---

### Type

**Responsibility:** Enum mapping each value type to a unique byte tag and string label for serialization.

**State/Fields:**
- `byte: Byte` — Binary Type Tag.
- `str: String` — String type label.

**Entries:** NULL(10), STR(20), BOOL(30), BOOL_TRUE(31), BOOL_FALSE(32), UNSURE_ANY(40), UNSURE_STR(41), UNSURE_NUM(42), UNSURE_BOOL(43), INT(57, "IntV"), FLOAT(58, "FloatV"), RANGE(60), LIST(70), SET(71), MAP(80).

`Type` owns the wire-format tag vocabulary: every tag a serializer emits or decodes is declared here, and no serializer defines tags of its own. Boolean tags are split by implementation: the byte-array serializer emits BOOL with a payload byte; the byte-buffer and char-buffer serializers emit the payload-free BOOL_TRUE / BOOL_FALSE tags.

**Serialization notes:** IntVal serializes as 8-byte Long (tag INT). FloatVal serializes as 8-byte Double (tag FLOAT).

---

## Function Specifications

### WireValidation (validation helpers)

Internal validation helpers shared by the serializer implementations.

**`MAX_NESTING_DEPTH` (internal const, value 1000)** -- Maximum supported Value-occurrence depth, with the root at depth zero. Declared in `IValue.kt` with `checkNestingDepth(depth: Int): Int`; serializers check every occurrence and copy traversal checks mutable containers. The accepted bound is shared by copying and serialization; [collection Open Gaps](design-collection.md#open-gaps) records the copy implementation difference at immutable leaves. The bound limits recursive stack use and rejects cyclic Value graphs.

**`String.requireWellFormedUtf16(): String`** -- Returns the receiver; throws `IllegalArgumentException` when the string contains an unpaired UTF-16 surrogate. Called by all three serializers before encoding string content.

**`decodeMaterial(block: () -> T): T`** -- Runs a deserialization body and rethrows every decoding failure (`BufferUnderflowException`, `NumberFormatException`, `CharacterCodingException`, `IllegalArgumentException`) as `ValFormatException` with the original cause. Wraps the body of every `deserialize` implementation.

**`checkSizePrefix(size: Int, remaining: Int, context: String): Int`** -- Returns the validated size; throws `IllegalArgumentException` when a decoded size or count prefix is negative or exceeds the remaining material.

**`requireDecodedType<T : IValue>(value: IValue, context: String): T`** -- Returns the value narrowed to `T`; throws `IllegalArgumentException` when a decoded value has an unexpected type (map keys decoded as non-`StrVal`).

### WireFormat (extension functions)

Buffer and encoding helpers shared by the serializer implementations.

**`String.asHexInt(): Int`** -- Parses a hexadecimal string to Int as unsigned, so every `Int.asHexString()` output round-trips (including 8-digit renderings of negative values). Throws `NumberFormatException` on invalid input.

**`Int.asHexString(): String`** -- Converts Int to its hexadecimal string form.

**`ByteBuffer.getArray(size: Int): ByteArray`** -- Reads `size` bytes into a new array. Throws `IllegalArgumentException` when `size` is negative or exceeds remaining bytes.

**`ByteBuffer.getString(size: Int? = null): String`** -- Reads a string of `size` bytes; when `size` is null, reads an Int length prefix first. Throws `IllegalArgumentException` on invalid size, `CharacterCodingException` on malformed UTF-8.

**`byteBufferOf(vararg elements: Byte): ByteBuffer`** -- Wraps the given bytes in a ByteBuffer.

**`ByteBuffer.put(type: Type): ByteBuffer`** -- Writes the type's tag byte; returns the buffer for chaining. Throws `BufferOverflowException` when no space remains.

**`String.asCharBuffer(): CharBuffer`** -- Copies the string into a CharBuffer.

**`CharBuffer.getBuffer(until: Char): CharBuffer` / `CharBuffer.getString(until: Char): String`** -- Reads characters up to `until` (delimiter consumed, excluded from result); reads all remaining characters when the delimiter is absent.

**`CharBuffer.getBuffer(size: Int): CharBuffer` / `CharBuffer.getString(size: Int): String`** -- Reads exactly `size` characters. Throws `IllegalArgumentException` when `size` is negative or exceeds remaining characters.

## Exception / Error Types

See [the shared error contract](design-conversions.md#exception--error-types) for construction, conversion and material failures.

## Caller Examples

```kotlin
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.serializer.DftByteArraySerializerImpl
val material = DftByteArraySerializerImpl.serialize(IntVal(1L))
val result = DftByteArraySerializerImpl.deserialize(material)
result
```

## Open Gaps

- The serializers recurse through nested containers and may retain intermediate material at multiple depths; [the specification](spec-serialization.md#complexity) bounds this work in terms of expanded occurrences and bytes, rather than distinct object identities.
- SerializerPerformanceTest discards timed results and uses heap deltas after GC; those measurements do not establish allocation per operation or a current throughput comparison.
