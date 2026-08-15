# commons-value Performance

Run with `./gradlew performanceTest`. Dataset: 100,000 random values (10,000 for collections).
Pipeline: `~/.claude/rules/workflow/performance.md` defines the optimization workflow.

---

## Current Baseline

Captured 2026-08-06 with all applied optimizations (through P7-1).

### Serializer Throughput (ops/s)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive serialize | 43,600,000 | 83,600,000 | 20,900,000 |
| Primitive deserialize | 74,700,000 | 114,800,000 | 10,700,000 |
| Mixed serialize | 10,200,000 | 8,340,000 | 6,390,000 |
| Mixed deserialize | 11,600,000 | 11,200,000 | 4,270,000 |
| Collection serialize | 5,170,000 | 4,420,000 | 3,260,000 |
| Collection deserialize | 5,950,000 | 5,410,000 | 2,090,000 |

Value creation throughput: 70,600,000 ops/s (6 value types x 600,000 iterations).

### Memory Allocation per Operation (bytes/op)

| Scenario | ByteArray | ByteBuffer | CharBuffer |
|----------|-----------|------------|------------|
| Primitive | 36 ser / 83 deser | 94 ser / 31 deser | 213 ser / 283 deser |
| Collection | 471 ser / 472 deser | 734 ser / 419 deser | 1,363 ser / 2,044 deser |

Measured via `Runtime.totalMemory() - freeMemory()` delta after forced GC. Approximate.

### Serialized Size (1000 random values)

| Serializer | Bytes | Relative |
|------------|-------|----------|
| ByteBuffer | ~23,200 | 1.00x |
| ByteArray | ~26,700 | 1.15x |
| CharBuffer | ~89,400 | 3.85x |

---

## Applied Optimizations

| ID | Change | Measured impact |
|----|--------|-----------------|
| P0-1 | Removed discarded MutableSet allocation in ByteBuffer list deserialization | ByteBuffer mixed deser +8.1%, coll deser +8.6% |
| P1-1 | Pre-sized array + `copyInto` replacing spread-operator concatenation in ByteArray serialization | ByteArray prim ser +19.0% |
| P1-2 | One shared ByteBuffer across ByteArray deserialization instead of per-value wrap | ByteArray prim deser +662% (3.4M -> 25.9M) |
| P1-3 | Zero-copy limit-based windowing for collection children in ByteArray deserialization | ByteArray coll deser +28.3% |
| P1-4 | Direct byte-shift encoding helpers replacing `ByteBuffer.allocate` in serialization | ByteArray mixed ser +6.4%, coll ser +11.4% |
| P2-7 | RangeVal stores start and end directly instead of an ArrayList | ~40 bytes saved per instance |
| P2-8 | Exact-width integer short-circuit in numeric range checks | Removes BigDecimal conversion; off hot path |
| P2-9 | `toLongOrNull`/`toDoubleOrNull` string parsing replacing shared NumberFormat | Thread-safety fix; removes per-call allocation |
| P3-11 | BoolVal singleton enforcement (private constructor, `T`/`F` instances) | Value creation +3.7% |
| P4-3 | Bulk `ByteBuffer.get(ByteArray)` replacing byte-by-byte copy in `getArray()` | ByteBuffer prim ser +35.6%, prim deser +29.3%, mixed deser +19.8% |
| P4-4 | Pre-sized collection construction replacing vararg spread in ListVal/MapVal | ByteArray prim ser +84.2% (JIT behavior change) |
| P5-1 | `DataInput.readFully` bulk read replacing byte-by-byte loop | Removes per-call ByteBuffer allocation; off hot path |
| P5-2 | Pre-sized LinkedHashSet in SetVal vararg constructor | Round-5 combined: ByteArray coll deser +10.5% |
| P5-3 | `forEach` replacing `fold` accumulator in ByteBuffer collection serialization | Round-5 combined: CharBuffer coll deser +5.1% |
| P5-4 | Corrected inverted EOF check in `DataInput.asByteSequence` | Correctness fix |
| P6-1 | Split numeric value into 64-bit integer and float kinds with fixed 8-byte encoding | Removes equality/hash defects and consumer type checks |
| P7-1 | Inline RangeVal encoding (tag + two longs) replacing recursive element serialization | Coll ser: ByteArray +12.8%, ByteBuffer +9.7%, CharBuffer +20.0% |

---

## Hardening Overhead

Captured 2026-08-15 for the serializer hardening (nesting depth bound, unpaired-surrogate
scan at serialize, deserialize boundary validation with ValFormatException wrapping).

| Scenario | Overhead vs pre-hardening HEAD |
|----------|--------------------------------|
| ByteArray primitive serialize | +4.9% |
| CharBuffer primitive serialize | +0.4% |
| ByteBuffer primitive serialize | Within noise |

Under the 10% budget set at the design gate. The surrogate scan is a single mask-compare
per char (`code and 0xF800 == 0xD800`) with a slow pairing path entered only when a
surrogate exists. Rejected alternatives (micro-benchmarked with a result sink): naive
`isSurrogate` char loop (~33% string-serialize overhead), ThreadLocal `CharsetEncoder`
with `CodingErrorAction.REPORT` (~2x), `encodeToByteArray(throwOnInvalidSequence = true)`
(+30-80%, loses to the intrinsified `toByteArray`).

---

## Rejected Approaches

| ID | Approach | Result | Reason |
|----|----------|--------|--------|
| P2-5 | Collection serialization streaming | ByteArray coll ser -14.7%, CharBuffer prim ser -81.8%, memory +18% | Pre-calculated exact allocation already optimal; JIT deoptimization from larger method body |
| P2-6 | CharBuffer collection serialization restructure | Best case coll ser +3.2%, but prim ser -14.1% | JIT-sensitive: any structural change alters compilation for all paths |
| P3-12 | Collection equals/hashCode caching | Not tested | Too invasive: rewrites 3 classes; not on serialization hot path |
| P3-13 | ListVal.plus full copy | Not tested | Usage guidance issue, not implementation optimization |
| P3-14 | Streaming serialization API | Same as P2-5 | Exact allocation optimal for in-memory use |
| P4-1 | ByteArray serialize method extraction | CharBuffer prim ser -75% | Cross-class JIT deoptimization |
| P4-2 | ByteBuffer collection single-allocation | Not tested | JIT sensitivity risk (P4-1, P2-5, P2-6 precedent) |
| P4-5 | `String.asCharBuffer()` zero-copy wrap | CharBuffer mixed ser -16%, coll ser -22% | Read-only CharBuffer adds overhead in `put(CharBuffer)` |
| P4-6 | ByteArray collection pre-size hint | Not tested | Wire format lacks element count; pre-scan cost/benefit questionable with JIT risk |
| P5-5 | Type byte dispatch array lookup | Not tested | `when(byte)` already compiles to `tableswitch`; array indirection may deoptimize |
| P5-6 | CharBuffer constant string caching | Not tested | Structural change to the CharBuffer serializer risks cross-class deoptimization |

---

## Candidates

No current candidates.

---

## Known Bottlenecks

- **ByteArray `serialize()` method body size.** The `serialize()` when-expression is large (~80 lines), which may prevent JIT inlining for primitive paths. Extracting collection cases was attempted (P4-1) but caused cross-class JIT deoptimization. ByteArray primitive serialize (44M) still trails ByteBuffer (84M).
- **ByteBuffer collection per-element allocation.** Each child `serialize()` call allocates its own ByteBuffer. Skipped (P4-2) due to JIT sensitivity risk.
- **Benchmark noise floor.** CharBuffer primitive serialize shows 3-4x variance between runs due to test execution order affecting JIT compilation. Current benchmark methodology cannot reliably detect < 5% changes.
- **Dead-code elimination in serialize loops.** The benchmark serialize loops discard their results, so the JIT can eliminate the serialization work and inflate apparent throughput up to 5x. Any A/B comparison across commits requires patching a result sink (e.g. accumulate `hashCode()`) into both sides before measuring.

---

## Optimization Constraints

- **Cross-class JIT sensitivity.** Structural changes to one serializer alter HotSpot compilation of the others in the same JVM: method extraction in the ByteArray serializer regressed the untouched CharBuffer serializer by 75%. Any structural change requires a full-suite re-measure.
- **Pre-calculated exact allocation beats dynamic growth.** Computing total size first and allocating once outperforms ByteArrayOutputStream/StringBuilder growth; the extra sizing pass is cheaper than reallocation and copying.
- **Instance caching pays only for degenerate value sets.** BoolVal's two-instance cache is branch-free and wins; range-based caches add factory branch overhead exceeding the allocation saved.
- **Platform bulk I/O beats element-by-element loops.** `DataInput.readFully`, `ByteBuffer.get(ByteArray)`, and `copyInto` consistently outperform iterative per-byte patterns.
