# Serializers

> Serialize and deserialize `IValue` instances to `ByteArray`, `ByteBuffer`, or `CharBuffer`.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.*
import edu.jhu.cobra.commons.value.serializer.*

val serializer = DftByteArraySerializerImpl
val bytes = serializer.serialize(IntVal(42L))
val restored = serializer.deserialize(bytes) // IntVal(42L)
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

- `serialize` raises `IllegalArgumentException` when value nesting exceeds 1000 levels (including cyclic value graphs) or a string or map key contains an unpaired UTF-16 surrogate.
- `deserialize` raises `ValFormatException` on malformed material: empty input, truncated or garbage data, invalid size or count prefixes, unknown type tags, malformed UTF-8 in string or key bytes, wrong delimiter characters (`DftCharBufferSerializerImpl`), trailing material after the value, or nesting beyond 1000 levels.

### Type Enum

- **`Type(byte: Byte, str: String)`** -- Type tag enum. Entries: `NULL`, `STR`, `BOOL`, `BOOL_TRUE`, `BOOL_FALSE`, `UNSURE_ANY`, `UNSURE_STR`, `UNSURE_NUM`, `UNSURE_BOOL`, `INT`, `FLOAT`, `RANGE`, `LIST`, `SET`, `MAP`.

## Gotchas

- `DftByteArraySerializerImpl` and `DftByteBufferSerializerImpl` use different binary layouts. Bytes from one cannot be deserialized by the other.
- `DftByteBufferSerializerImpl` serializes `RangeVal` bounds as two `IntVal` values (`Long`-backed).
- `DftCharBufferSerializerImpl` uses hex-encoded element counts for collections and string lengths; hex prefixes parse as unsigned, so a negative prefix is rejected as non-hexadecimal.
- All three serializers are `object` singletons. No instantiation needed.
- Serializers handle nested collections (e.g., `ListVal` containing `MapVal`) up to 1000 nesting levels.
- `deserialize` consumes the whole material; a valid encoding followed by extra bytes or chars raises `ValFormatException`.
- Reject or repair unpaired surrogates before `serialize`; they never round-trip.
