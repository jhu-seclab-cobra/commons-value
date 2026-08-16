# IR Value Types

> Sealed type hierarchy for language-agnostic intermediate representation values.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.*

val s = StrVal("hello")
val n = 42.intVal              // IntVal(42L)
val f = 3.14.floatVal          // FloatVal(3.14)
val b = BoolVal(true)
val list = ListVal(s, n, f, b)

when (val v: IValue = list[0]) {
    is StrVal -> v.core
    is IntVal -> v.toInt()
    is FloatVal -> v.toLong()
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

- **`IValue`** -- Root sealed interface. Property: `val core: Any?`. Method: `deepCopy(): IValue` -- structurally independent copy; primitives return self, collections copy recursively.
- **`IPrimitiveVal : IValue, Comparable<IPrimitiveVal>`** -- Sealed interface for primitive IR values. `compareTo` is a total order: kinds rank `NullVal < BoolVal < numeric < StrVal < Unsure`; `IntVal` and `FloatVal` compare by exact numeric value; `NaN` orders above every number; `-0.0` is order-equivalent to `0.0`; `StrVal` compares lexicographically; `Unsure` by declaration order.
- **`ICollectionVal : IValue`** -- Sealed interface for collection IR values.

### Primitive Types

- **`StrVal(core: String)`** -- String IR value. Default constructor creates empty string. Methods: `startsWith(String)`, `substringAfter(String)`, `substringBefore(String)`, `contains(String)`, `uppercase()`, `lowercase()`, `trim()`, `equals(String, ignoreCase)`, `equals(IPrimitiveVal, ignoreCase)`, `length`, `get(Int)`, `get(IntVal)`.
- **`IntVal(core: Long)`** -- Integer IR value. Stores `Long` internally. Default constructor creates `0L`. Methods: `toInt()`, `toDouble()`, `toFloat()`, `compareTo(Int)`, `compareTo(Long)`. Extension constructors: `Int.intVal`, `Long.intVal`, `Short.intVal`, `Byte.intVal`, `String.intVal`.
- **`FloatVal(core: Double)`** -- Floating-point IR value. Stores `Double` internally. Default constructor creates `0.0`. Methods: `toIntVal()`, `toInt()`, `toLong()`, `toFloat()`, `compareTo(Double)`. Extension constructors: `Double.floatVal`, `Float.floatVal`, `String.floatVal`.
- **`BoolVal`** -- Boolean IR value. Private constructor; obtain via `BoolVal(true)`, `BoolVal(false)`, `BoolVal.T`, `BoolVal.F`. Methods: `isTrue()`, `isFalse()`.
- **`NullVal`** -- Singleton data object. `core` is always `null`.
- **`Unsure`** -- Enum for unknown-but-typed values. Entries: `ANY`, `STR`, `NUM`, `BOOL`. Factory: `Unsure.new(String): Unsure?`, `Unsure.new(IPrimitiveVal): Unsure`, `Unsure.new<T>(): Unsure`. Operator: `String in Unsure`.

### Collection Types

- **`ListVal(core: ArrayList<IValue>) : MutableList<IValue>`** -- Mutable ordered list delegating to `core`; the full `MutableList` API and stdlib collection extensions apply. Constructors: `ListVal()`, `ListVal(size: Int)`, `ListVal(List<IValue>)`, `ListVal(vararg IValue)`. Override: `deepCopy(): ListVal`.
- **`SetVal(core: LinkedHashSet<IValue>) : MutableSet<IValue>`** -- Mutable insertion-ordered set delegating to `core`; the full `MutableSet` API and stdlib collection extensions apply. Constructors: `SetVal()`, `SetVal(size: Int)`, `SetVal(Collection<IValue>)`, `SetVal(vararg IValue)`, `SetVal(Sequence<IValue>)`. Override: `deepCopy(): SetVal`.
- **`MapVal(core: LinkedHashMap<String, IValue>) : MutableMap<String, IValue>`** -- Mutable insertion-ordered string-keyed map delegating to `core`; the full `MutableMap` API and stdlib map extensions apply. Constructors: `MapVal()`, `MapVal(size: Int)`, `MapVal(Map<String, IValue>)`, `MapVal(vararg Pair<String, IValue>)`, `MapVal(Sequence<Pair>)`, `MapVal(List<Pair>)`. Override: `deepCopy(): MapVal`.
- **`RangeVal(start: IntVal, endInclusive: IntVal)`** -- Numeric range. Constructor: `RangeVal(Number, Number)`. Properties: `first: Long`, `last: Long`. Operators: `contains(Number)`, `contains(IntVal)`, `contains(RangeVal)`, `plus(RangeVal)`. Infix: `before(RangeVal)`, `after(RangeVal)` (strict: ranges sharing a boundary point are neither). Methods: `map((IntVal) -> R) {}`.

## Gotchas

- `MapVal` keys are `String`, not `IValue`. Use `StrVal.core` to extract the key.
- `+=`/`-=` on collection values mutate in place. `+`/`-` are stdlib extensions returning plain `List<IValue>`/`Set<IValue>`/`Map<String, IValue>`, not `ListVal`/`SetVal`/`MapVal`.
- Collection equality follows the JDK collection contract: a `ListVal`/`SetVal`/`MapVal` equals any `List`/`Set`/`Map` with equal content. `ListVal(IntVal(1L)) == listOf(IntVal(1L))` is `true`.
- `StrVal.equals(IPrimitiveVal, ignoreCase)` returns `false` for `NullVal`; it never matches the string `"null"`.
- Sharing a collection value stores the same mutable instance. Use `deepCopy()` for an independent copy.
- `BoolVal` uses singleton instances. `BoolVal(true) === BoolVal.T` is always `true`.
- `NullVal` is a `data object`. Identity comparison (`===`) and equality (`==`) both work.
- `Unsure.new(String)` returns `null` when the string does not match a known `Unsure.core` value.
- `IntVal` stores `Long`, `FloatVal` stores `Double` -- no type ambiguity.
