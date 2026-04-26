# commons-value Design -- Collection Types

> **Target design.** Current implementation uses `NumVal`; migration to `IntVal`/`FloatVal` is tracked in `performance.md` P6-1.

Part of [commons-value design](design-primitive.md). Specifies the `ICollectionVal` sealed hierarchy.

---

## Class / Type Specifications

### ICollectionVal

**Responsibility:** Sealed interface for aggregate value types containing multiple values.

**State/Fields:** Inherits `core` from `IValue`.

---

### ListVal

**Responsibility:** Wraps an `ArrayList<IValue>` as a mutable ordered collection value.

**State/Fields:**
- `core: ArrayList<IValue>` — The internal list storage.

**Constructors:**
- `ListVal(core: ArrayList<IValue>)` — Primary constructor.
- `ListVal()` — Empty list.
- `ListVal(size: Int)` — Pre-sized empty list.
- `ListVal(value: List<IValue>)` — Copy from list.
- `ListVal(vararg value: IValue)` — From varargs.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `get(index: Int)` | Returns element at index | `index: Int` | `IValue` | `IndexOutOfBoundsException` |
| `set(index: Int, value: IValue)` | Replaces element at index | `index: Int`, `value: IValue` | — | `IndexOutOfBoundsException` |
| `contains(value: IValue)` | Checks element membership | `value: IValue` | `Boolean` | — |
| `containsAll(values: Collection<IValue>)` | Checks all elements present | `values: Collection<IValue>` | `Boolean` | — |
| `indexOf(value: IValue)` | First index of element, or -1 | `value: IValue` | `Int` | — |
| `lastIndexOf(value: IValue)` | Last index of element, or -1 | `value: IValue` | `Int` | — |
| `subList(fromIndex: Int, toIndex: Int)` | Returns sub-range as new ListVal | `fromIndex: Int`, `toIndex: Int` | `ListVal` | `IndexOutOfBoundsException`, `IllegalArgumentException` |
| `plus(value: IValue)` | Returns new ListVal with element appended | `value: IValue` | `ListVal` | — |
| `plusAssign(value: IValue)` | Mutably appends element | `value: IValue` | — | — |
| `minus(value: IValue)` | Returns new ListVal with element removed | `value: IValue` | `ListVal` | — |
| `minusAssign(value: IValue)` | Mutably removes element | `value: IValue` | — | — |
| `isEmpty()` / `isNotEmpty()` | Emptiness checks | — | `Boolean` | — |
| `map(transform)` | Transforms each element | `transform: (IValue) -> R` | `List<R>` | — |
| `flatMap(transform)` | Flat-maps each element | `transform: (IValue) -> List<R>` | `List<R>` | — |
| `forEach(action)` | Iterates each element | `action: (IValue) -> Unit` | — | — |
| `asSequence()` | Returns lazy sequence | — | `Sequence<IValue>` | — |
| `toMutableSet()` | Converts to a mutable linked set | — | `LinkedHashSet<IValue>` | — |

**Properties:**
- `size: Int` — Number of elements.

---

### SetVal

**Responsibility:** Wraps a `LinkedHashSet<IValue>` as a mutable ordered set value.

**State/Fields:**
- `core: LinkedHashSet<IValue>` — The internal set storage (preserves insertion order).

**Constructors:**
- `SetVal(core: LinkedHashSet<IValue>)` — Primary constructor.
- `SetVal()` — Empty set.
- `SetVal(size: Int)` — Pre-sized empty set.
- `SetVal(value: Collection<IValue>)` — Copy from collection.
- `SetVal(vararg value: IValue)` — From varargs.
- `SetVal(values: Sequence<IValue>)` — From sequence.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `add(new: IValue)` | Adds element, returns true if new | `new: IValue` | `Boolean` | — |
| `remove(prev: IValue)` | Removes element, returns true if present | `prev: IValue` | `Boolean` | — |
| `plus(new: IValue)` | Returns new SetVal with element added | `new: IValue` | `SetVal` | — |
| `plusAssign(value: IValue)` | Mutably adds element | `value: IValue` | — | — |
| `minus(prev: IValue)` | Returns new SetVal with element removed | `prev: IValue` | `SetVal` | — |
| `minusAssign(prev: IValue)` | Mutably removes element | `prev: IValue` | — | — |
| `contains(value: IValue)` | Checks element membership | `value: IValue` | `Boolean` | — |
| `containsAll(values: Collection<IValue>)` | Checks all elements present | `values: Collection<IValue>` | `Boolean` | — |
| `isEmpty()` / `isNotEmpty()` | Emptiness checks | — | `Boolean` | — |
| `map(transform)` | Transforms each element | `transform: (IValue) -> R` | `List<R>` | — |
| `forEach(action)` | Iterates each element | `action: (IValue) -> Unit` | — | — |
| `asSequence()` | Returns lazy sequence | — | `Sequence<IValue>` | — |
| `toList()` | Converts to list | — | `List<IValue>` | — |

**Properties:**
- `size: Int` — Number of elements.

---

### MapVal

**Responsibility:** Wraps a `HashMap<String, IValue>` as a mutable string-keyed map value.

**State/Fields:**
- `core: HashMap<String, IValue>` — The internal map storage (String keys only).

**Constructors:**
- `MapVal(core: HashMap<String, IValue>)` — Primary constructor.
- `MapVal()` — Empty map.
- `MapVal(size: Int)` — Pre-sized empty map.
- `MapVal(value: Map<String, IValue>)` — Copy from map.
- `MapVal(vararg value: Pair<String, IValue>)` — From vararg pairs.
- `MapVal(values: Sequence<Pair<String, IValue>>)` — From sequence.
- `MapVal(values: List<Pair<String, IValue>>)` — From list of pairs.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `get(key: String)` | Returns value for key, or null | `key: String` | `IValue?` | — |
| `set(key: String, value: IValue)` | Sets key-value pair | `key: String`, `value: IValue` | — | — |
| `add(key: String, value: IValue)` | Adds key-value pair | `key: String`, `value: IValue` | `IValue?` | — |
| `plus(pair: Pair<String, IValue>)` | Adds pair | `pair: Pair<String, IValue>` | `IValue?` | — |
| `minus(key: String)` / `remove(key: String)` | Removes by key, returns previous value | `key: String` | `IValue?` | — |
| `keys()` | Returns all keys | — | `Set<String>` | — |
| `values()` | Returns all values | — | `Collection<IValue>` | — |
| `contains(key: String)` | Checks key presence | `key: String` | `Boolean` | — |
| `isEmpty()` | Emptiness check | — | `Boolean` | — |
| `forEach(action)` | Iterates each entry | `action: (Map.Entry<String, IValue>) -> Unit` | — | — |
| `map(behavior)` | Transforms each entry | `behavior: (Map.Entry<String, IValue>) -> R` | `List<R>` | — |
| `mapValues(behavior)` | Transforms values | `behavior: (Map.Entry<String, IValue>) -> R` | `Map<String, R>` | — |
| `flatMap(behavior)` | Flat-maps entries | `behavior: (Map.Entry<String, IValue>) -> Iterable<R>` | `List<R>` | — |
| `toList()` | Converts to pair list | — | `List<Pair<String, IValue>>` | — |
| `toTypeArray()` | Converts to pair array | — | `Array<Pair<String, IValue>>` | — |

**Properties:**
- `size: Int` — Number of key-value pairs.

---

### RangeVal

**Responsibility:** Represents an inclusive integer range as a collection value.

**State/Fields:**
- `start: NumVal` -- Starting value of the range.
- `endInclusive: NumVal` -- Ending value of the range (inclusive).
- `core: List<NumVal>` -- Computed property returning `listOf(start, endInclusive)`.

Primary storage uses NumVal. IntVal and Long constructors convert to NumVal internally.

**Constructors:**
- `RangeVal(start: NumVal, endInclusive: NumVal)` -- Primary constructor from two NumVal values.
- `RangeVal(start: IntVal, endInclusive: IntVal)` -- Secondary constructor converting IntVal to NumVal.
- `RangeVal(start: Long, endInclude: Long)` -- Secondary constructor converting Longs to NumVal.

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `contains(num: Long)` | Checks if number is within range | `num: Long` | `Boolean` | -- |
| `contains(num: NumVal)` | Checks if NumVal is within range | `num: NumVal` | `Boolean` | -- |
| `contains(range: RangeVal)` | Checks if sub-range is fully contained | `range: RangeVal` | `Boolean` | -- |
| `infix before(range: RangeVal)` | Checks if this range ends before other starts | `range: RangeVal` | `Boolean` | -- |
| `infix after(range: RangeVal)` | Checks if this range starts after other ends | `range: RangeVal` | `Boolean` | -- |
| `plus(range: RangeVal)` | Combines two ranges into their union bounds | `range: RangeVal` | `RangeVal` | -- |
| `map(transform)` | Transforms start and end values | `transform: (NumVal) -> R` | `List<R>` | -- |

**Properties:**
- `first: Number` -- Start of range (delegates to `start.core`).
- `last: Number` -- End of range inclusive (delegates to `endInclusive.core`).
