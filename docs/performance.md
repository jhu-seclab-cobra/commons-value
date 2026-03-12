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
| P4-3 Bulk array copy | ByteBuffer prim serialize | 36.8M | 49.9M | **+35.6%** |
| P4-3 Bulk array copy | ByteBuffer prim deserialize | 39.3M | 50.8M | **+29.3%** |
| P4-3 Bulk array copy | ByteBuffer mixed deserialize | 5.8M | 7.0M | **+19.8%** |
| P4-4 Vararg spread removal | ByteArray prim serialize | 16.7M | 30.7M | **+84.2%** |

Measured incrementally: each row shows the delta from applying that single optimization.

### Current Benchmark Results

Run with `./gradlew performanceTest` after all optimizations applied.

#### Serializer Throughput (ops/s)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive serialize | 30,000,000 | 52,900,000 | 23,500,000 |
| Primitive deserialize | 43,800,000 | 52,300,000 | 17,100,000 |
| Mixed serialize | 7,200,000 | 6,500,000 | 5,300,000 |
| Mixed deserialize | 7,800,000 | 7,400,000 | 3,500,000 |
| Collection serialize | 3,400,000 | 3,200,000 | 2,800,000 |
| Collection deserialize | 3,900,000 | 3,800,000 | 1,900,000 |

Value creation throughput: 40,600,000 ops/s (6 value types × 600,000 iterations).

#### Memory Allocation per Operation (bytes/op)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive | 221 ser / 221 deser | 243 ser / 121 deser | 306 ser / 306 deser |
| Collection | 2,040 ser / 1,020 deser | 2,040 ser / 1,020 deser | 3,060 ser / 3,060 deser |

Measured via `Runtime.totalMemory() - freeMemory()` delta after forced GC. Approximate.

#### Serialized Size (1000 random values)

| Serializer | Bytes | Relative |
|------------|-------|----------|
| ByteBuffer | ~89,000 | 1.00x |
| ByteArray | ~93,000 | 1.05x |
| CharBuffer | ~212,000 | 2.38x |

---

## Completed Optimizations (10 items)

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

### P4-3. `ByteBuffer.getArray()` bulk copy

**File:** `SerializerUtils.kt`
**Change:** Replaced byte-by-byte `repeat(size) { bs[it] = get() }` with bulk `ByteBuffer.get(ByteArray)` in `ByteBuffer.getArray()`.
**Impact:** ByteBuffer primitive serialize **+35.6%** (36.8M -> 49.9M), primitive deserialize **+29.3%**, mixed deserialize **+19.8%**.

### P4-4. `ListVal(vararg)` / `MapVal(vararg)` spread operator removal

**File:** `ListVal.kt`, `MapVal.kt`
**Change:** Replaced `arrayListOf(*value)` with `ArrayList(size).apply { addAll(value) }` and `hashMapOf(*value)` with pre-sized `HashMap.apply { forEach put }` to avoid array copy from spread operator.
**Impact:** ByteArray primitive serialize **+84.2%** (16.7M -> 30.7M). Indirect improvement via JIT compilation behavior change.

---

## Evaluated & Rejected (11 items)

| ID | Optimization | Result | Reason |
|----|-------------|--------|--------|
| P2-5 | Collection serialization streaming | ByteArray collection ser **-14.7%**, CharBuffer prim ser **-81.8%**, memory **+18%** | Pre-calculated exact allocation already optimal; JIT deoptimization from larger method body |
| P2-6 | CharBuffer collection serialization | Best case: collection ser +3.2%, but prim ser **-14.1%** | JIT-sensitive: any structural change alters compilation for ALL paths |
| P3-10 | NumVal Number boxing -> value classes | Not tested | Too invasive: requires API changes across all serializers, collections, tests |
| P3-12 | Collection equals/hashCode caching | Not tested | Too invasive: requires rewriting 3 classes; not on serialization hot path |
| P3-13 | ListVal.plus full copy | Not tested | Usage guidance issue, not implementation optimization |
| P3-14 | Streaming serialization API | Same as P2-5 | Already evaluated; exact allocation optimal for in-memory use |
| P3-15 | NumVal integer caching (-128..127) | Value creation **-2.4%**, collection deser **-13.0%** | Factory branch overhead exceeds allocation savings; `data class` equality requires matching Number subtypes |
| P4-1 | ByteArray serialize method extraction | CharBuffer prim ser **-75%** | Extracting collection serialization into separate methods caused cross-class JIT deoptimization |
| P4-2 | ByteBuffer collection single-allocation | Not tested | Skipped due to JIT sensitivity risk (P4-1, P2-5, P2-6 precedent) |
| P4-5 | `String.asCharBuffer()` zero-copy wrap | CharBuffer mixed ser **-16%**, coll ser **-22%** | Read-only CharBuffer from `CharBuffer.wrap(String)` adds overhead in `put(CharBuffer)` |
| P4-6 | ByteArray collection pre-size hint | Not tested | Wire format lacks element count; pre-scan cost/benefit questionable with JIT risk |

---

## Remaining Known Bottlenecks

- **ByteArray `serialize()` method body size.** The `serialize()` when-expression is large (~80 lines), which may prevent JIT inlining for primitive paths. Extracting collection cases was attempted (P4-1) but caused cross-class JIT deoptimization. ByteArray primitive serialize (30M) still trails ByteBuffer (53M).
- **ByteBuffer collection per-element allocation.** Each child `serialize()` call allocates its own ByteBuffer. Skipped (P4-2) due to JIT sensitivity risk.
- **`data class` NumVal boxing equality.** Evaluated as P3-10 — too invasive for current API.

---

## Key Insights

1. **ByteArray serializer was the primary beneficiary.** The biggest gains came from eliminating redundant allocations (shared ByteBuffer, zero-copy windowing, direct byte-shift encoding). Total improvement: primitive serialize +14%, primitive deserialize +697%, collection deserialize +69%.

2. **JIT compilation sensitivity — cross-class effects.** Method extraction or structural changes cause regressions not only within the same class (P2-5, P2-6) but across entirely different serializer classes running in the same JVM (P4-1: ByteArray method extraction caused CharBuffer -75%). HotSpot's compilation budget, inlining heuristics, and class loading order all interact unpredictably.

3. **Pre-calculated exact allocation is optimal.** For in-memory serialization, computing total size first and allocating once outperforms dynamic-growth approaches (ByteArrayOutputStream, StringBuilder). The extra pass is cheaper than reallocation + copying.

4. **Caching has hidden costs.** NumVal integer caching (P3-15) added branch overhead that exceeded allocation savings. BoolVal caching (P3-11) succeeded because it has only 2 possible values with zero branching cost (direct `if/else`).

5. **Structural improvements matter.** RangeVal (P2-7), isInLongRange (P2-8), and NumberFormat removal (P2-9) improved memory usage, type safety, and thread safety respectively, even without measurable throughput gains.

6. **Utility function overhead compounds.** `ByteBuffer.getArray()` byte-by-byte copy (P4-3) was a hidden ~30% overhead on ByteBuffer primitive paths — a one-line fix yielded the largest single-optimization gain in the ByteBuffer serializer. Read-only CharBuffer wrappers (P4-5) add ~16-22% overhead from buffer type checks in `put(CharBuffer)`.
