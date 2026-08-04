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

### Implementations

- **`DftByteArraySerializerImpl : IValSerializer<ByteArray>`** -- Singleton. Compact binary encoding to `ByteArray`. Raises `IllegalArgumentException` on unknown `IValue` subtype or empty input.
- **`DftByteBufferSerializerImpl : IValSerializer<ByteBuffer>`** -- Singleton. Binary encoding to `ByteBuffer`. Raises `IllegalArgumentException` on unknown type or empty buffer.
- **`DftCharBufferSerializerImpl : IValSerializer<CharBuffer>`** -- Singleton. Human-readable text encoding to `CharBuffer`. Format: `TYPE:value:`. Raises `IllegalArgumentException` on unknown type.

### Type Enum

- **`Type(byte: Byte, str: String)`** -- Internal type tag enum. Entries: `NULL`, `STR`, `BOOL`, `BOOL_TRUE`, `BOOL_FALSE`, `UNSURE_ANY`, `UNSURE_STR`, `UNSURE_NUM`, `UNSURE_BOOL`, `INT`, `FLOAT`, `RANGE`, `LIST`, `SET`, `MAP`.

## Gotchas

- `DftByteArraySerializerImpl` and `DftByteBufferSerializerImpl` use different binary layouts. Bytes from one cannot be deserialized by the other.
- `DftByteBufferSerializerImpl` serializes `RangeVal` bounds as two `IntVal` values (`Long`-backed).
- `DftCharBufferSerializerImpl` uses hex-encoded element counts for collections and string lengths.
- All three serializers are `object` singletons. No instantiation needed.
- Serializers handle nested collections (e.g., `ListVal` containing `MapVal`).
