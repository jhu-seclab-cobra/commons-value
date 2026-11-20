# Serializers

> Serialize and deserialize `IValue` instances to `ByteArray`, `ByteBuffer`, or `CharBuffer`.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.serializer.DftByteArraySerializerImpl

val serializer = DftByteArraySerializerImpl
val bytes = serializer.serialize(IntVal(42L))
val restored = serializer.deserialize(bytes)
```

## API

### Interface

- **`IValSerializer<Material : Any>`** -- Generic serialization interface. Methods: `serialize(IValue): Material`, `deserialize(Material): IValue`.
- **`ValFormatException : IllegalArgumentException`** -- Raised by `deserialize` on malformed material.

### Implementations

- **`DftByteArraySerializerImpl : IValSerializer<ByteArray>`** -- Singleton. Compact binary encoding to `ByteArray`.
- **`DftByteBufferSerializerImpl : IValSerializer<ByteBuffer>`** -- Singleton. Binary encoding to `ByteBuffer`.
- **`DftCharBufferSerializerImpl : IValSerializer<CharBuffer>`** -- Singleton. Human-readable text encoding to `CharBuffer`. Format: `TYPE:value:`.

### Error Contract (all three implementations)

- `serialize` raises `IllegalArgumentException` when value nesting exceeds 1000 levels (including cyclic value graphs) or a map key contains an unpaired UTF-16 surrogate.
- `deserialize` raises `ValFormatException` on malformed material: empty input, truncated or garbage data, invalid size or count prefixes, unknown Type Tag values, malformed UTF-8 in string or key bytes, a `STR_BYTES` payload whose bytes are all well-formed UTF-8, wrong delimiter characters (`DftCharBufferSerializerImpl`), trailing material after the value, or nesting beyond 1000 levels.

### Type Enum

- **`Type(byte: Byte, str: String)`** -- Type Tag enum. Entries: `NULL`, `STR`, `STR_BYTES`, `BOOL`, `BOOL_TRUE`, `BOOL_FALSE`, `UNSURE_ANY`, `UNSURE_STR`, `UNSURE_NUM`, `UNSURE_BOOL`, `INT`, `FLOAT`, `RANGE`, `LIST`, `SET`, `MAP`.

## Configuration

- No configuration is required.

## Gotchas

- `DftByteArraySerializerImpl` and `DftByteBufferSerializerImpl` use different binary layouts. Some primitive encodings coincide, but the formats are not interchangeable; use the serializer that owns the material.
- `DftByteBufferSerializerImpl` serializes `RangeVal` bounds as two `IntVal` values (`Long`-backed).
- `DftCharBufferSerializerImpl` uses hex-encoded element counts for collections and string lengths; hex prefixes parse as unsigned Int bit patterns; size validation rejects a decoded negative size or count.
- All three serializers are `object` singletons. No instantiation needed.
- Serializers handle nested collections (e.g., `ListVal` containing `MapVal`) up to 1000 nesting levels.
- `deserialize` consumes the whole material; a valid encoding followed by extra bytes or chars raises `ValFormatException`.
- A `StrVal` holding an unpaired unit U+DC80..U+DCFF (a byte outside UTF-8) serializes under `STR_BYTES` and round-trips; reject or repair unpaired surrogates in map keys before `serialize`.
