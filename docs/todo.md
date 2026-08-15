# commons-value Hardening Tasks

Fixes from the 2026-08-15 edge-case audit. Design: [design-primitive.md](design-primitive.md), [design-collection.md](design-collection.md), [design-serializer.md](design-serializer.md), [design-conversions.md](design-conversions.md). APIs: [impl.md](impl.md).

Protocol: each bug task follows `/debug` — failing reproduction test first, then fix, then one `fix(scope):` commit carrying both. Refactor/feature tasks commit as `refactor(...)`/`feat(...)` when runnable.

---

## Task 1 — Exact numeric total order (member compareTo)

- [x] Failing tests: `IntVal(2^53+1) > FloatVal(2^53.toDouble())`; cross-kind `NullVal < BoolVal < IntVal < StrVal < Unsure` no longer throws; NaN above all; `-0.0` order-equivalent to `0.0`.
- [x] `IPrimitiveVal : Comparable<IPrimitiveVal>` with default `compareTo` in IPrimitiveVal.kt per design table.
- [x] Remove extension `compareTo` + `compareNumeric` from PrimitiveConversions.kt.
- Acceptance: sort of mixed primitives is deterministic and transitive; all tests pass.
- Commit: `fix(value): implement exact total order on primitives`.

## Task 2 — RangeVal.contains(Number) exact

- [x] Failing test: `BigInteger`/`BigDecimal`/`AtomicLong` beyond Double precision not rounded into/out of range.
- [x] Route non-primitive `Number` kinds through BigDecimal comparison.
- Commit: `fix(value): compare RangeVal.contains for non-primitive Number exactly`.

## Task 3 — StrVal.get(IntVal) bounds

- [x] Failing test: index core outside Int range raises `IndexOutOfBoundsException`, never truncates.
- Commit: `fix(value): bound StrVal.get(IntVal) index without Int truncation`.

## Task 4 — Collection delegation (breaking)

- [x] `ListVal : MutableList<IValue> by core`, `SetVal : MutableSet<IValue> by core`, `MapVal : MutableMap<String, IValue> by core`.
- [x] Delete hand-written duplicate members and member `plus`/`minus`; keep explicit `equals`/`hashCode`/`toString`.
- [x] Compile check: `+=` resolves to `plusAssign` (impl.md).
- [x] Adapt library tests; note parent-repo blast radius (no parent usage of removed members found).
- Acceptance: full stdlib collection API available; tests pass.
- Commit: `refactor(value)!: delegate collections to core containers`.

## Task 5 — deepCopy

- [x] `IValue.deepCopy()` with covariant overrides; primitives return self; collections copy recursively.
- Commit: `feat(value): add recursive deepCopy to value hierarchy`.

## Task 6 — Char and LongRange conversions

- [x] `Any?.primitiveVal`/`Any?.toVal` accept `Char`; `LongRange.rangeVal`.
- Commit: `feat(value): convert Char and LongRange to values`.

## Task 7 — ValFormatException unification

- [x] Failing tests: truncated/garbage material raises `ValFormatException` (not leaked `BufferUnderflowException`/`NumberFormatException`) on all three serializers.
- [x] `ValFormatException : IllegalArgumentException` in IValSerializer.kt; wrap at deserialize boundary.
- Commit: `fix(serializer): raise ValFormatException on malformed material`.

## Task 8 — Reject trailing material

- [x] Failing test: valid encoding + trailing bytes/chars raises `ValFormatException`, all three serializers.
- Commit: `fix(serializer): reject trailing material after deserialize`.

## Task 9 — Nested window consumption

- [x] Failing test: nested length-prefixed payload with wrong inner length rejected (ByteArray serializer).
- Commit: `fix(serializer): assert nested parse consumes its window`.

## Task 10 — Depth limit 1000

- [x] Failing tests: 1001-deep list rejected in serialize and deserialize; cyclic value graph rejected in serialize.
- [x] `MAX_NESTING_DEPTH = 1000` in WireFormat.kt; enforce both directions, all three serializers.
- Commit: `fix(serializer): bound value nesting at 1000 levels`.

## Task 11 — Reject unpaired surrogates

- [x] Failing test: lone high/low surrogate in StrVal/map key raises `IllegalArgumentException` at serialize, all three serializers.
- [x] `String.requireWellFormedUtf16()` in WireFormat.kt.
- [x] Run performanceTest; >10% regression on string serialization → redesign per workflow/performance.md.
- Commit: `fix(serializer): reject unpaired surrogates at serialize`.

## Task 12 — LLM docs sync

- [x] Update llms/core.md, llms/conversions.md, llms/serializers.md for compareTo member, delegation, deepCopy, ValFormatException, depth/surrogate limits, Char/LongRange.
- [x] Regenerate llms/full.txt (hand-synced L2 mirror; exact-match replace).
- Commit: `docs(value): sync llms docs with hardening changes`.

## Task 13 — Verify

- [x] `./gradlew detekt ktlintCheck test build` clean.
- [x] performanceTest run; results recorded.
- [x] Grep parent repo for stale `import ...value.compareTo`; report adaptation needs.

---

Order: 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 12 → 13. Tasks 7–11 depend on 4 (test files touch collection APIs). One commit per task.
