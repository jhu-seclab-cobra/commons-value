# commons-value Performance

## Benchmark Results

Run with `./gradlew performanceTest`. Dataset: 100,000 random values (10,000 for collections).

### Serializer Throughput (ops/s)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive serialize | 50,873,327 | 44,184,896 | 27,029,889 |
| Primitive deserialize | 28,175,892 | 18,903,650 | 8,205,650 |
| Mixed serialize | 6,946,848 | 6,147,191 | 4,650,400 |
| Mixed deserialize | 3,577,927 | 2,940,480 | 1,661,108 |
| Collection serialize | 3,804,234 | 3,287,626 | 2,467,130 |
| Collection deserialize | 1,869,255 | 1,517,348 | 860,387 |

### Serialized Size (1000 random values)

| Serializer | Bytes | Relative |
|------------|-------|----------|
| ByteBuffer | 93,206 | 1.00x |
| ByteArray | 97,662 | 1.05x |
| CharBuffer | 221,058 | 2.37x |

### Memory Allocation per Operation (bytes/op)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive serialize | 193 | 222 | 370 |
| Primitive deserialize | 325 | 320 | 662 |
| Collection serialize | 1,564 | 1,824 | 2,897 |
| Collection deserialize | 2,608 | 2,605 | 5,532 |

Measured via `Runtime.totalMemory() - freeMemory()` delta after forced GC. Approximate — subject to GC timing variance.

### Value Creation

53,793,487 ops/s (NumVal, StrVal, BoolVal, ListVal, SetVal, MapVal combined).

---

## Completed Optimizations

### P0-1. Dead code in ByteBuffer List deserialization (DONE)

**File:** `DftByteBufferSerializerImpl.kt`
**Change:** Removed `container.core.toMutableSet()` which created a MutableSet that was immediately discarded on every List deserialization.
**Result:** ByteBuffer mixed deserialize +8.1%, collection deserialize +8.6%.

### P1-1. ByteArray spread operator removal (DONE)

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced `byteArrayOf(Type.STR.byte, *value.core.toByteArray())` spread pattern with `ByteBuffer.allocate(1 + bytes.size).put(type).put(bytes).array()` for StrVal and NUM_OTHERS serialization.
**Result:** ByteArray primitive serialize +19.0% (42.9M → 51.1M ops/s).

### P1-2. ByteArray shared ByteBuffer deserialization (DONE)

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced per-value `ByteBuffer.wrap()` allocation in deserialization with a single shared `ByteBuffer.wrap(material)` and a private `deserializeFrom(ByteBuffer)` method. Reads primitives directly from the buffer (`buffer.short`, `buffer.int`, etc.).
**Result:** ByteArray primitive deserialize +655% (3.4M → 25.9M ops/s).

### P1-3. ByteArray sub-element zero-copy deserialization (DONE)

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced child-element array copying (`ByteArray(elementSize).also { buffer.get(it) }` + `deserialize(elementBytes)`) with limit-based windowing on the shared ByteBuffer. Saves N array allocations + N copies per collection.
**Result:** ByteArray collection deserialize +28.3% (1.07M → 1.37M ops/s).

### P1-4. Eliminate ByteBuffer allocation in ByteArray serialization (DONE)

**File:** `DftByteArraySerializerImpl.kt`, `DftByteBufferSerializerImpl.kt`
**Change:** Replaced all `ByteBuffer.allocate()` in `DftByteArraySerializerImpl.serialize()` with direct byte-shift encoding helpers (`shortToBytes`, `intToBytes`, `longToBytes`) and `ByteArray` + `copyInto`. Also replaced ByteBuffer-based collection assembly with direct array offset writing (`intInto`). Fixed `DftByteBufferSerializerImpl` NUM_OTHERS spread operator.
**Result:** ByteArray mixed serialize +6.4% (6.5M → 6.9M ops/s), collection serialize +11.4% (3.4M → 3.8M ops/s), collection deserialize +8.5% (1.7M → 1.9M ops/s). Collection memory -1.4% (1,586 → 1,564 bytes/op).

---

## Identified Bottlenecks & Optimization Opportunities

### P2 - Medium Impact

#### ~~5. Collection serialization intermediate list~~ (EVALUATED — not beneficial)

**File:** `DftByteArraySerializerImpl.kt:96`, `DftByteBufferSerializerImpl.kt:77,84`, `DftCharBufferSerializerImpl.kt:75,85`
**Issue:** All three serializers use `elements.map { serialize(it) }` to materialize all child serializations into a `List` before computing total size.
**Tested:** Replaced with `ByteArrayOutputStream`/`StringBuilder` streaming. Result: ByteArray collection serialize **-14.7%**, CharBuffer primitive serialize **-81.8%** (JIT deoptimization from larger method body), memory **+18%** (BAOS doubling + `toByteArray()` copy vs pre-calculated exact allocation). The pre-calculated approach is already optimal for in-memory serialization. True streaming benefit requires a new streaming API (see P3-14).

#### 6. CharBuffer collection serialization is 5.5x slower

**File:** `DftCharBufferSerializerImpl.kt:74-102`
**Issue:** String concatenation, hex encoding (`asHexString()`), and CharBuffer allocation per element. Collection serialization at 459K ops/s vs ByteBuffer's 2.5M ops/s.
**Fix:** Use StringBuilder for intermediate building, or pre-calculate total char count to allocate once.

#### 7. RangeVal uses ArrayList for 2 elements

**File:** `RangeVal.kt:10`
**Issue:** `ArrayList<NumVal>` stores exactly 2 elements (start, end). ArrayList overhead: object header + internal array + size field + modCount.
**Fix:** Replace with two direct properties: `val start: NumVal, val end: NumVal`. Requires ICollectionVal.core contract adjustment.

#### 8. Number.isInLongRange uses BigDecimal

**File:** `PrimitiveUtils.kt:30`
**Issue:** `BigDecimal(toString())` converts Number to String then parses to BigDecimal. Called in `NumVal.truncate()`.
**Fix:** Short-circuit for known types: `when (this) { is Int, is Long, is Short, is Byte -> true; else -> BigDecimal(toString()) in range }`.

#### 9. NumberFormat thread safety

**File:** `PrimitiveUtils.kt:92,133`
**Issue:** `numberFormatter` is a shared top-level val. `NumberFormat` is not thread-safe; concurrent `String.numVal` calls may corrupt state.
**Fix:** Use `ThreadLocal<NumberFormat>`, or replace with `toLongOrNull()` / `toDoubleOrNull()`.

### P3 - Architectural

#### 10. NumVal Number boxing

**File:** `NumVal.kt:18`
**Issue:** `core: Number` forces every primitive int/long/double into a boxed object. This is the most frequently created value type.
**Fix:** Split into concrete types (`IntVal`, `LongVal`, `DoubleVal`) using `@JvmInline value class`, or provide cached instances for small integers (-128..127) similar to `Integer.valueOf()`.

#### 11. BoolVal allows redundant instances

**File:** `BoolVal.kt:18`
**Issue:** `data class` with public constructor allows `BoolVal(true)` to create new instances despite `BoolVal.T`/`BoolVal.F` singletons existing.
**Fix:** Make constructor private, expose only `BoolVal.of(Boolean)` factory or restrict to T/F constants.

#### 12. data class + mutable collection equals/hashCode

**File:** `ListVal.kt:20`, `SetVal.kt:19`, `MapVal.kt:20`
**Issue:** `data class` auto-generates `equals()`/`hashCode()` that deep-traverse the mutable collection. When used as Set elements or Map values, this is O(n) per comparison. Mutation after insertion breaks Set/Map invariants.
**Fix:** Cache hashCode with dirty flag, or separate mutable builders from immutable value types.

#### 13. ListVal.plus creates full copy

**File:** `ListVal.kt:239`
**Issue:** `operator fun plus(value: IValue): ListVal = ListVal(core + value)` copies the entire list. Chaining `list + a + b + c` is O(n^2).
**Fix:** Guide users toward `plusAssign` (`+=`) for building. Or adopt persistent data structures for immutable semantics.

#### 14. No streaming serialization API

**Issue:** All three serializers use value-to-complete-material conversion. Large nested structures require all intermediate results in memory simultaneously.
**Fix:** Add `serializeTo(value: IValue, output: OutputStream)` / `deserializeFrom(input: InputStream)` for incremental I/O.

#### 15. No common value caching

**Issue:** High-frequency values like `NumVal(0)`, `NumVal(1)`, `StrVal("")` create new instances every time.
**Fix:** Cache small integer NumVal (-128..127) and empty-string StrVal as singletons, similar to Java's `Integer.valueOf()`.
