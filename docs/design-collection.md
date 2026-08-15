# commons-value Design -- Collection Types

Part of [commons-value design](design-primitive.md). Specifies the `ICollectionVal` sealed hierarchy.

---

## Class / Type Specifications

### ICollectionVal

**Responsibility:** Sealed interface for aggregate value types containing multiple values.

**State/Fields:** Inherits `core` from `IValue`.

---

### Mutable Collection Contract (ListVal, SetVal, MapVal)

- Each class implements the matching Kotlin mutable collection interface by delegating to `core`; all interface members and stdlib collection extensions apply. No hand-written re-implementations of interface members exist.
- The primary constructor adopts the passed container (shared ownership: later external mutation of the container mutates the value). Every secondary constructor copies its input.
- `equals`/`hashCode` are content-based and defined per class (`ListVal` equals only `ListVal`, etc.).
- No copy-returning `plus`/`minus` members: `+=` resolves to the stdlib `plusAssign` (in-place); copies are taken via the copying constructors or `deepCopy()`.
- Mutable-element caveat (JDK set semantics): mutating a collection while it is an element of a `SetVal` corrupts membership; value graphs must stay acyclic. Documented, not enforced.

---

### ListVal

**Responsibility:** Mutable ordered collection value; `MutableList<IValue>` by delegation to `core`.

**State/Fields:**
- `core: ArrayList<IValue>` — The internal list storage.

**Constructors:**
- `ListVal(core: ArrayList<IValue>)` — Primary constructor (adopts).
- `ListVal()` — Empty list.
- `ListVal(size: Int)` — Pre-sized empty list.
- `ListVal(value: List<IValue>)` — Copy from list.
- `ListVal(vararg value: IValue)` — From varargs.

**Methods (own, beyond `MutableList`):**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `deepCopy()` | Recursive structural copy | — | `ListVal` | — |

`subList` keeps the `MutableList` contract: a live view backed by this list.

---

### SetVal

**Responsibility:** Mutable insertion-ordered set value; `MutableSet<IValue>` by delegation to `core`.

**State/Fields:**
- `core: LinkedHashSet<IValue>` — The internal set storage (preserves insertion order).

**Constructors:**
- `SetVal(core: LinkedHashSet<IValue>)` — Primary constructor (adopts).
- `SetVal()` — Empty set.
- `SetVal(size: Int)` — Pre-sized empty set.
- `SetVal(value: Collection<IValue>)` — Copy from collection.
- `SetVal(vararg value: IValue)` — From varargs.
- `SetVal(values: Sequence<IValue>)` — From sequence.

**Methods (own, beyond `MutableSet`):**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `deepCopy()` | Recursive structural copy | — | `SetVal` | — |

---

### MapVal

**Responsibility:** Mutable string-keyed map value; `MutableMap<String, IValue>` by delegation to `core`.

**State/Fields:**
- `core: HashMap<String, IValue>` — The internal map storage (String keys only).

**Constructors:**
- `MapVal(core: HashMap<String, IValue>)` — Primary constructor (adopts).
- `MapVal()` — Empty map.
- `MapVal(size: Int)` — Pre-sized empty map.
- `MapVal(value: Map<String, IValue>)` — Copy from map.
- `MapVal(vararg value: Pair<String, IValue>)` — From vararg pairs.
- `MapVal(values: Sequence<Pair<String, IValue>>)` — From sequence.
- `MapVal(values: List<Pair<String, IValue>>)` — From list of pairs.

**Methods (own, beyond `MutableMap`):**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `deepCopy()` | Recursive structural copy | — | `MapVal` | — |

`keys`/`values`/`entries` are the `MutableMap` properties; the former function forms, `add`, `toPairArray`, and copy-returning `plus`/`minus` are removed.

---

### RangeVal

**Responsibility:** Represents an inclusive integer range as a collection value.

**State/Fields:**
- `start: IntVal` -- Starting value of the range.
- `endInclusive: IntVal` -- Ending value of the range (inclusive).
- `core: List<IntVal>` -- Computed property returning `listOf(start, endInclusive)`.

**Constructors:**
- `RangeVal(start: IntVal, endInclusive: IntVal)` -- Primary constructor.
- `RangeVal(start: Long, endInclusive: Long)` -- Secondary constructor converting Longs to IntVal.
- `RangeVal(start: Number, endInclude: Number)` -- Secondary constructor converting Numbers to IntVal.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `contains(num: Number)` | Checks if number is within range; compares the exact numeric value for every `Number` kind (no `Double` rounding for `BigInteger`/`BigDecimal`/`AtomicLong`) | `num: Number` | `Boolean` | -- |
| `contains(num: Long)` | Checks if long is within range | `num: Long` | `Boolean` | -- |
| `contains(num: IntVal)` | Checks if IntVal is within range | `num: IntVal` | `Boolean` | -- |
| `contains(range: RangeVal)` | Checks if sub-range is fully contained | `range: RangeVal` | `Boolean` | -- |
| `infix before(range: RangeVal)` | Checks if this range ends strictly before other starts (`last < range.first`; a shared boundary point is not before) | `range: RangeVal` | `Boolean` | -- |
| `infix after(range: RangeVal)` | Checks if this range starts strictly after other ends (`first > range.last`; a shared boundary point is not after) | `range: RangeVal` | `Boolean` | -- |
| `plus(range: RangeVal)` | Combines two ranges into their union bounds | `range: RangeVal` | `RangeVal` | -- |
| `map(transform)` | Transforms start and end values | `transform: (IntVal) -> R` | `List<R>` | -- |

**Properties:**
- `first: Long` -- Start of range (delegates to `start.core`).
- `last: Long` -- End of range inclusive (delegates to `endInclusive.core`).
