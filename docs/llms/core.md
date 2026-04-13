# IR Value Types

> Sealed type hierarchy for language-agnostic intermediate representation values.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.*

val s = StrVal("hello")
val n = NumVal(42)
val b = BoolVal(true)
val list = ListVal(s, n, b)

when (val v: IValue = list[0]) {
    is StrVal -> v.core
    is NumVal -> v.toInt()
    is BoolVal -> v.isTrue()
    is NullVal -> null
    is Unsure -> v.core
    is ListVal -> v.size
    is SetVal -> v.size
    is MapVal -> v.size
    is RangeVal -> v.first
}
```

## API

### Sealed Interfaces

- **`IValue`** -- Root sealed interface. Property: `val core: Any?`.
- **`IPrimitiveVal : IValue`** -- Sealed interface for primitive IR values.
- **`ICollectionVal : IValue`** -- Sealed interface for collection IR values.

### Primitive Types

- **`StrVal(core: String)`** -- String IR value. Default constructor creates empty string. Methods: `startsWith(String)`, `substringAfter(String)`, `substringBefore(String)`, `contains(String)`, `uppercase()`, `lowercase()`, `trim()`, `equals(String, ignoreCase)`, `length`, `get(Int)`, `get(NumVal)`.
- **`NumVal(core: Number)`** -- Numeric IR value. Default constructor creates `0`. Methods: `toInt()`, `toDouble()`, `toFloat()`, `toLong()`, `toShort()`, `compareTo(Int)`. Properties: `isInt`, `isLong`, `isShort`, `isByte`, `isFloat`, `isDouble`, `isPrimitiveIntegerType`, `isPrimitiveFloatingType`. Static: `NumVal.truncate(NumVal): NumVal`.
- **`BoolVal`** -- Boolean IR value. Private constructor; obtain via `BoolVal(true)`, `BoolVal(false)`, `BoolVal.T`, `BoolVal.F`. Methods: `isTrue()`, `isFalse()`.
- **`NullVal`** -- Singleton data object. `core` is always `null`. Infix: `NullVal isNull value`, `NullVal isNotNull value`.
- **`Unsure`** -- Enum for unknown-but-typed values. Entries: `ANY`, `STR`, `NUM`, `BOOL`. Factory: `Unsure.new(String): Unsure?`, `Unsure.new(IPrimitiveVal): Unsure`, `Unsure.new<T>(): Unsure`. Operator: `String in Unsure`.

### Collection Types

- **`ListVal(core: ArrayList<IValue>)`** -- Mutable ordered list. Constructors: `ListVal()`, `ListVal(size: Int)`, `ListVal(List<IValue>)`, `ListVal(vararg IValue)`. Operators: `get(Int)`, `set(Int, IValue)`, `plus(IValue)`, `plusAssign(IValue)`, `minus(IValue)`, `minusAssign(IValue)`. Methods: `size`, `contains(IValue)`, `containsAll(Collection)`, `indexOf(IValue)`, `lastIndexOf(IValue)`, `subList(Int, Int)`, `isEmpty()`, `isNotEmpty()`, `map {}`, `flatMap {}`, `forEach {}`, `asSequence()`, `toMutableSet()`.
- **`SetVal(core: LinkedHashSet<IValue>)`** -- Mutable insertion-ordered set. Constructors: `SetVal()`, `SetVal(size: Int)`, `SetVal(Collection<IValue>)`, `SetVal(vararg IValue)`, `SetVal(Sequence<IValue>)`. Methods: `add(IValue)`, `remove(IValue)`, `contains(IValue)`, `containsAll(Collection)`, `size`, `isEmpty()`, `isNotEmpty()`, `map {}`, `forEach {}`, `asSequence()`, `toList()`. Operators: `plus`, `plusAssign`, `minus`, `minusAssign`.
- **`MapVal(core: HashMap<String, IValue>)`** -- Mutable string-keyed map. Constructors: `MapVal()`, `MapVal(size: Int)`, `MapVal(Map<String, IValue>)`, `MapVal(vararg Pair<String, IValue>)`, `MapVal(Sequence<Pair>)`, `MapVal(List<Pair>)`. Operators: `get(String): IValue?`, `set(String, IValue)`, `plus(Pair)`, `minus(String)`, `contains(String)`. Methods: `add(String, IValue)`, `remove(String)`, `keys()`, `values()`, `size`, `isEmpty()`, `map {}`, `mapValues {}`, `flatMap {}`, `forEach {}`, `toList()`, `toTypeArray()`.
- **`RangeVal(start: NumVal, endInclusive: NumVal)`** -- Numeric range. Constructor: `RangeVal(Number, Number)`. Properties: `first: Number`, `last: Number`. Operators: `contains(Number)`, `contains(NumVal)`, `contains(RangeVal)`, `plus(RangeVal)`. Infix: `before(RangeVal)`, `after(RangeVal)`. Methods: `map {}`.

## Gotchas

- `MapVal` keys are `String`, not `IValue`. Use `StrVal.core` to extract the key.
- `ListVal` and `SetVal` are mutable via `plusAssign`/`minusAssign`. The `plus`/`minus` operators return new instances.
- `BoolVal` uses singleton instances. `BoolVal(true) === BoolVal.T` is always `true`.
- `NullVal` is a `data object`. Identity comparison (`===`) and equality (`==`) both work.
- `Unsure.new(String)` returns `null` when the string does not match a known `Unsure.core` value.
- `NumVal.core` retains the original `Number` subtype (`Int`, `Long`, `Double`, etc.). Use `NumVal.truncate()` to normalize to the smallest integer type.
