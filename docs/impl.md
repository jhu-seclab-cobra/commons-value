# commons-value Implementation

## Libraries

- No runtime dependencies. Kotlin stdlib and the JDK cover all parsing and buffer handling.

## Developer Instructions

- `IntVal` is Long-backed. `FloatVal` is Double-backed.
- `String.intVal` uses `toLongOrNull()` for parsing. `String.floatVal` uses `toDoubleOrNull()`.
- IR types do not model any specific language's type system. `IntVal` is Long-width, `FloatVal` is Double-width. Language-specific semantics (type juggling, casting, overflow behavior, narrower integer types) belong in the consuming analysis module's adapter layer.

## Design-specific

### design-collection.md

- **[stdlib]** `class ListVal(...) : ICollectionVal, MutableList<IValue> by core` — delegation does not forward `equals`/`hashCode`/`toString`; keep the explicit overrides.
- **[stdlib]** With member `plus`/`minus` removed, `listVal += x` resolves to `MutableCollection.plusAssign` (in-place): stdlib `Collection.plus` returns `List<IValue>`, unassignable to a `ListVal` variable, so the ambiguity rule never triggers. Verify with a compile check during implementation.
- **[stdlib]** Delegated `subList` returns the backing `ArrayList`'s live view (`MutableList<IValue>`), not a copy — semantics change from the removed hand-written copy.

### design-primitive.md / design-collection.md (exact numeric compare)

- **[jdk]** `BigDecimal(long)` / `BigDecimal(double)` + `compareTo` — exact cross-kind comparison; fast path `Long.toDouble()` is lossless when `abs(long) <= 2^53`.
- **[jdk]** `BigDecimal(Double)` throws `NumberFormatException` on NaN/infinity — branch on `Double.isFinite` first.

### design-serializer.md

- **[jdk]** `Char.isHighSurrogate()` / `Char.isLowSurrogate()` — linear scan detects unpaired surrogates; `String.encodeToByteArray()` silently replaces them with `?` (0x3F), which is why serialization must reject first.
- **[jdk]** `ByteBuffer.get` throws `BufferUnderflowException` (extends `RuntimeException`, not `IllegalArgumentException`) — wrap at the `deserialize` boundary into `ValFormatException`.
