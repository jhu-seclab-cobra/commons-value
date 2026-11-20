# commons-value Implementation

## Libraries

- No runtime dependencies beyond Kotlin stdlib and the JDK; they cover parsing and buffers.

## Developer Instructions

- `IntVal` is Long-backed. `FloatVal` is Double-backed.
- `String.intVal` uses `toLongOrNull()` for parsing. `String.floatVal` uses `toDoubleOrNull()`.
- Value instances model no source language's type system: language-specific semantics (type juggling, casting, overflow behavior, narrower integer types) belong in the consuming analysis module's adapter layer.

## Design-specific

### design-collection.md

- [@kotlin] `class ListVal(...) : ICollectionVal, MutableList<IValue> by core` — delegation does not forward `equals`/`hashCode`/`toString`; keep the explicit overrides.
- [@kotlin] `listVal += x` resolves to `MutableCollection.plusAssign` (in-place): the Collection Value instances declare no member `plus`/`minus`, and stdlib `Collection.plus` returns `List<IValue>`, unassignable to a `ListVal` variable, so the plus/plusAssign ambiguity rule never triggers.
- [@kotlin] Delegated `subList` returns the backing `ArrayList`'s live view (`MutableList<IValue>`), not a copy.

### design-primitive.md / design-collection.md (exact numeric compare)

- [@jdk] `BigDecimal(long)` / `BigDecimal(double)` + `compareTo` — exact cross-kind comparison; fast path `Long.toDouble()` is lossless when `abs(long) <= 2^53`.
- [@jdk] `BigDecimal(Double)` throws `NumberFormatException` on NaN/infinity — branch on `Double.isFinite` first.

### design-serializer.md

- [@jdk] `Char.isHighSurrogate()` / `Char.isLowSurrogate()` — linear scan detects unpaired surrogates; `String.encodeToByteArray()` silently replaces them with `?` (0x3F), so a string holding an unpaired unit U+DC80..U+DCFF encodes through `StrVal.toByteArray()` and a map key is rejected first.
- [@jdk] `ByteBuffer.get` throws `BufferUnderflowException` (extends `RuntimeException`, not `IllegalArgumentException`) — wrap at the `deserialize` boundary into `ValFormatException`.
