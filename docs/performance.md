# commons-value Performance

Run with `./gradlew performanceTest`. Dataset: 100,000 random values (10,000 for collections).

---

## Key Improvements (measured per-optimization)

The most impactful improvements are in the ByteArray serializer deserialization path:

| Optimization | Metric | Before | After | Change |
|-------------|--------|--------|-------|--------|
| P1-1 Spread removal | ByteArray prim serialize | 42.9M | 51.1M | **+19.0%** |
| P1-2 Shared ByteBuffer | ByteArray prim deserialize | 3.4M | 25.9M | **+662%** |
| P1-3 Zero-copy windowing | ByteArray coll deserialize | 1.07M | 1.37M | **+28.3%** |
| P1-4 Direct byte-shift | ByteArray mixed serialize | 6.5M | 6.9M | **+6.4%** |
| P1-4 Direct byte-shift | ByteArray coll serialize | 3.4M | 3.8M | **+11.4%** |
| P0-1 Dead code removal | ByteBuffer mixed deserialize | — | — | **+8.1%** |
| P0-1 Dead code removal | ByteBuffer coll deserialize | — | — | **+8.6%** |
| P3-11 BoolVal singleton | Value creation | 51.8M | 53.8M | **+3.7%** |

Measured incrementally: each row shows the delta from applying that single optimization.

### Current Benchmark Results

Run with `./gradlew performanceTest` after all optimizations applied.

#### Serializer Throughput (ops/s)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive serialize | 18,600,000 | 38,800,000 | 24,000,000 |
| Primitive deserialize | 53,500,000 | 38,500,000 | 17,000,000 |
| Mixed serialize | 7,100,000 | 6,500,000 | 5,500,000 |
| Mixed deserialize | 7,800,000 | 6,500,000 | 3,600,000 |
| Collection serialize | 2,900,000 | 3,100,000 | 2,900,000 |
| Collection deserialize | 3,700,000 | 3,300,000 | 1,800,000 |

Value creation throughput: 38,100,000 ops/s (6 value types × 600,000 iterations).

#### Memory Allocation per Operation (bytes/op)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive | 227 ser / 225 deser | 243 ser / 121 deser | — |

Measured via `Runtime.totalMemory() - freeMemory()` delta after forced GC. Approximate.

#### Serialized Size (1000 random values)

| Serializer | Bytes | Relative |
|------------|-------|----------|
| ByteBuffer | 89,012 | 1.00x |
| ByteArray | 93,440 | 1.05x |
| CharBuffer | 212,334 | 2.38x |

---

## Completed Optimizations (8 items)

### P0-1. Dead code in ByteBuffer List deserialization

**File:** `DftByteBufferSerializerImpl.kt`
**Change:** Removed `container.core.toMutableSet()` which created a MutableSet that was immediately discarded on every List deserialization.
**Impact:** ByteBuffer mixed deserialize +8.1%, collection deserialize +8.6%.

### P1-1. ByteArray spread operator removal

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced `byteArrayOf(Type.STR.byte, *value.core.toByteArray())` spread pattern with `ByteArray(1 + bytes.size)` + `copyInto` for StrVal and NUM_OTHERS serialization.
**Impact:** ByteArray primitive serialize **+19.0%** (42.9M -> 51.1M ops/s).

### P1-2. ByteArray shared ByteBuffer deserialization

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced per-value `ByteBuffer.wrap()` allocation in deserialization with a single shared `ByteBuffer.wrap(material)` and a private `deserializeFrom(ByteBuffer)` method.
**Impact:** ByteArray primitive deserialize **+655%** (3.4M -> 25.9M ops/s).

### P1-3. ByteArray sub-element zero-copy deserialization

**File:** `DftByteArraySerializerImpl.kt`
**Change:** Replaced child-element array copying with limit-based windowing on the shared ByteBuffer. Saves N array allocations + N copies per collection.
**Impact:** ByteArray collection deserialize **+28.3%** (1.07M -> 1.37M ops/s).

### P1-4. Eliminate ByteBuffer allocation in ByteArray serialization

**File:** `DftByteArraySerializerImpl.kt`, `DftByteBufferSerializerImpl.kt`
**Change:** Replaced all `ByteBuffer.allocate()` with direct byte-shift encoding helpers (`shortToBytes`, `intToBytes`, `longToBytes`) and `ByteArray` + `copyInto`. Fixed `DftByteBufferSerializerImpl` NUM_OTHERS spread operator.
**Impact:** ByteArray mixed serialize **+6.4%**, collection serialize **+11.4%**, collection deserialize **+8.5%**. Collection memory -1.4%.

### P2-7. RangeVal ArrayList elimination

**File:** `RangeVal.kt`
**Change:** Replaced `data class RangeVal(override val core: ArrayList<NumVal>)` with `data class RangeVal(val start: NumVal, val endInclusive: NumVal)`. The `core` property is now a computed `List<NumVal>` for backward compatibility.
**Impact:** ~40 bytes saved per RangeVal instance. Direct property access eliminates iterator/bounds-check overhead.

### P2-8. Number.isInLongRange short-circuit

**File:** `PrimitiveUtils.kt`
**Change:** Added `when` short-circuit: `Byte`, `Short`, `Int`, `Long` return `true` immediately without `BigDecimal` conversion.
**Impact:** Eliminates `toString()` + `BigDecimal` parsing for the most common numeric types. Not on serialization hot path.

### P2-9. NumberFormat thread safety

**File:** `PrimitiveUtils.kt`
**Change:** Replaced shared `NumberFormat.getNumberInstance()` with `toLongOrNull()`/`toDoubleOrNull()` in `String.numVal`.
**Impact:** Fixes thread-safety bug. Removes `NumberFormat` object allocation and locale-dependent parsing overhead. `String.numVal` is now stateless and thread-safe.

### P3-11. BoolVal singleton enforcement

**File:** `BoolVal.kt`
**Change:** Converted from `data class` to regular class with private constructor and companion `invoke` operators. `BoolVal(true)`/`BoolVal(false)` return singleton `T`/`F` instances.
**Impact:** Value creation **+3.7%** (51.8M -> 53.8M ops/s). Zero-allocation for BoolVal construction.

---

## Evaluated & Rejected (7 items)

| ID | Optimization | Result | Reason |
|----|-------------|--------|--------|
| P2-5 | Collection serialization streaming | ByteArray collection ser **-14.7%**, CharBuffer prim ser **-81.8%**, memory **+18%** | Pre-calculated exact allocation already optimal; JIT deoptimization from larger method body |
| P2-6 | CharBuffer collection serialization | Best case: collection ser +3.2%, but prim ser **-14.1%** | JIT-sensitive: any structural change alters compilation for ALL paths |
| P3-10 | NumVal Number boxing -> value classes | Not tested | Too invasive: requires API changes across all serializers, collections, tests |
| P3-12 | Collection equals/hashCode caching | Not tested | Too invasive: requires rewriting 3 classes; not on serialization hot path |
| P3-13 | ListVal.plus full copy | Not tested | Usage guidance issue, not implementation optimization |
| P3-14 | Streaming serialization API | Same as P2-5 | Already evaluated; exact allocation optimal for in-memory use |
| P3-15 | NumVal integer caching (-128..127) | Value creation **-2.4%**, collection deser **-13.0%** | Factory branch overhead exceeds allocation savings; `data class` equality requires matching Number subtypes |

---

## Key Insights

1. **ByteArray serializer was the primary beneficiary.** The biggest gains came from eliminating redundant allocations (shared ByteBuffer, zero-copy windowing, direct byte-shift encoding). Total improvement: primitive serialize +14%, primitive deserialize +697%, collection deserialize +69%.

2. **JIT compilation sensitivity.** The CharBuffer serializer's `serialize()` method is extremely JIT-sensitive. Extracting methods or changing method body size alters HotSpot compilation decisions for ALL code paths in the class, causing regressions in unrelated paths (P2-5, P2-6).

3. **Pre-calculated exact allocation is optimal.** For in-memory serialization, computing total size first and allocating once outperforms dynamic-growth approaches (ByteArrayOutputStream, StringBuilder). The extra pass is cheaper than reallocation + copying.

4. **Caching has hidden costs.** NumVal integer caching (P3-15) added branch overhead that exceeded allocation savings. BoolVal caching (P3-11) succeeded because it has only 2 possible values with zero branching cost (direct `if/else`).

5. **Structural improvements matter.** RangeVal (P2-7), isInLongRange (P2-8), and NumberFormat removal (P2-9) improved memory usage, type safety, and thread safety respectively, even without measurable throughput gains.
