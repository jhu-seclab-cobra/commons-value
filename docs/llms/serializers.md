# Serializers

> Serialize and deserialize `IValue` instances to `ByteArray`, `ByteBuffer`, or `CharBuffer`.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.*
import edu.jhu.cobra.commons.value.serializer.*

val serializer = DftByteArraySerializerImpl
val bytes = serializer.serialize(NumVal(42))
val restored = serializer.deserialize(bytes) // NumVal(42)
```

## API

### Interface

- **`IValSerializer<Material : Any>`** -- Generic serialization interface. Methods: `serialize(IValue): Material`, `deserialize(Material): IValue`.

### Implementations

- **`DftByteArraySerializerImpl : IValSerializer<ByteArray>`** -- Singleton. Compact binary encoding to `ByteArray`. Raises `IllegalArgumentException` on unknown `IValue` subtype or empty input.
- **`DftByteBufferSerializerImpl : IValSerializer<ByteBuffer>`** -- Singleton. Binary encoding to `ByteBuffer`. Raises `IllegalArgumentException` on unknown type or empty buffer.
- **`DftCharBufferSerializerImpl : IValSerializer<CharBuffer>`** -- Singleton. Human-readable text encoding to `CharBuffer`. Format: `TYPE:value:`. Raises `IllegalArgumentException` on unknown type.

### Type Enum

- **`Type(byte: Byte, str: String)`** -- Internal type tag enum. Entries: `NULL`, `STR`, `BOOL`, `BOOL_TRUE`, `BOOL_FALSE`, `UNSURE_ANY`, `UNSURE_STR`, `UNSURE_NUM`, `UNSURE_BOOL`, `NUM_BYTE`, `NUM_SHORT`, `NUM_INT`, `NUM_LONG`, `NUM_FLOAT`, `NUM_DOUBLE`, `NUM_OTHERS`, `RANGE`, `LIST`, `SET`, `MAP`.

## Gotchas

- `DftByteArraySerializerImpl` and `DftByteBufferSerializerImpl` use different binary layouts. Bytes from one cannot be deserialized by the other.
- `DftByteBufferSerializerImpl` serializes `RangeVal` bounds as `Int` only. Long/Double range bounds are truncated.
- `DftCharBufferSerializerImpl` uses hex-encoded element counts for collections and string lengths.
- All three serializers are `object` singletons. No instantiation needed.
- Serializers handle nested collections (e.g., `ListVal` containing `MapVal`).
