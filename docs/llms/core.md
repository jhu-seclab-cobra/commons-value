# Value Types

> Sealed type hierarchy for language-agnostic intermediate representation values.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
val value = ListVal(IntVal(1L))
val copy = value.deepCopy()
copy.single()
```

## API

### Sealed Interfaces

- **`IValue`** -- Root sealed interface. Property: `val core: Any?`. Method: `deepCopy(): IValue` -- structurally independent copy; primitives return self, collections copy recursively. Throws `IllegalArgumentException` on nesting deeper than 1000 levels, including cyclic value graphs.
- **`IPrimitiveVal : IValue, Comparable<IPrimitiveVal>`** -- Sealed interface for Primitive Value instances. `compareTo` is a total order: kinds rank `NullVal < BoolVal < numeric < StrVal < Unsure`; `IntVal` and `FloatVal` compare by exact numeric value; `NaN` orders above every number; `-0.0` is order-equivalent to `0.0`; `StrVal` compares lexicographically; `Unsure` by declaration order. The order is inconsistent with `equals`: equal-valued `IntVal`/`FloatVal` pairs compare as equivalent yet are not equal.
- **`ICollectionVal : IValue`** -- Sealed interface for Collection Value instances.

### Primitive Types

- **`StrVal(core: String)`** -- String Value standing for a byte sequence. The constructor canonicalizes `core`: each well-formed UTF-8 sequence of the bytes is its character, and each other byte `XX` is the unpaired unit U+DCXX. Default constructor creates empty string. Byte view: `toByteArray()`, inverse `ByteArray.strVal`; `String.indexOfByteOutsideUtf8(startIndex)` finds the next unpaired unit U+DC80..U+DCFF, reading a surrogate pair as one character. Text methods: `startsWith(String)`, `substringAfter(String)`, `substringBefore(String)`, `contains(String)`, `uppercase()`, `lowercase()`, `trim()`, `equals(String, ignoreCase)`, `equals(IPrimitiveVal, ignoreCase)`, `length`, `get(Int)`, `get(IntVal)`.
- **`IntVal(core: Long)`** -- Integer Value. Stores `Long` internally. Default constructor creates `0L`. Methods: `toInt()`, `toDouble()`, `toFloat()`, `compareTo(Int)`, `compareTo(Long)`. Extension constructors: `Int.intVal`, `Long.intVal`, `Short.intVal`, `Byte.intVal`, `String.intVal`.
- **`FloatVal(core: Double)`** -- Floating-point Value. Stores `Double` internally. Default constructor creates `0.0`. Methods: `toIntVal()`, `toInt()`, `toLong()`, `toFloat()`, `compareTo(Double)`. Extension constructors: `Double.floatVal`, `Float.floatVal`, `String.floatVal`.
- **`BoolVal`** -- Boolea Value. Private constructor; obtain via `BoolVal(true)`, `BoolVal(false)`, `BoolVal.T`, `BoolVal.F`. Methods: `isTrue()`, `isFalse()`.
- **`NullVal`** -- Singleton data object. `core` is always `null`.
- **`Unsure`** -- Enum for unknown-but-typed values. Entries: `ANY`, `STR`, `NUM`, `BOOL`. Factory: `Unsure.new(String): Unsure?`, `Unsure.new(IPrimitiveVal): Unsure`, `Unsure.new<T>(): Unsure`. Operator: `String in Unsure`.

### Collection Types

- **`ListVal(core: ArrayList<IValue>) : MutableList<IValue>`** -- Mutable ordered list delegating to `core`; the full `MutableList` API and stdlib collection extensions apply. Constructors: `ListVal()`, `ListVal(size: Int)`, `ListVal(List<IValue>)`, `ListVal(vararg IValue)`. Override: `deepCopy(): ListVal`.
- **`SetVal(core: LinkedHashSet<IValue>) : MutableSet<IValue>`** -- Mutable insertion-ordered set delegating to `core`; the full `MutableSet` API and stdlib collection extensions apply. Constructors: `SetVal()`, `SetVal(size: Int)`, `SetVal(Collection<IValue>)`, `SetVal(vararg IValue)`, `SetVal(Sequence<IValue>)`. Override: `deepCopy(): SetVal`.
- **`MapVal(core: LinkedHashMap<String, IValue>) : MutableMap<String, IValue>`** -- Mutable insertion-ordered string-keyed map delegating to `core`; the full `MutableMap` API and stdlib map extensions apply. Constructors: `MapVal()`, `MapVal(size: Int)`, `MapVal(Map<String, IValue>)`, `MapVal(vararg Pair<String, IValue>)`, `MapVal(Sequence<Pair>)`, `MapVal(List<Pair>)`. Override: `deepCopy(): MapVal`.
- **`RangeVal(start: IntVal, endInclusive: IntVal)`** -- Integer range. Constructor: `RangeVal(Number, Number)`. Properties: `first: Long`, `last: Long`. Operators: `contains(Number)`, `contains(IntVal)`, `contains(RangeVal)`, `plus(RangeVal)`. Infix: `before(RangeVal)`, `after(RangeVal)` (strict: ranges sharing a boundary point are neither). Methods: `map(transform: (IntVal) -> R): List<R>`.

## Configuration

- No configuration is required.

## Gotchas

- `MapVal` keys are `String`, not `IValue`. Use `StrVal.core` to extract the key.
- `+=`/`-=` on Collection Value instances mutate in place. `+`/`-` are stdlib extensions returning plain `List<IValue>`/`Set<IValue>`/`Map<String, IValue>`, not `ListVal`/`SetVal`/`MapVal`.
- Collection equality follows the JDK collection contract: a `ListVal`/`SetVal`/`MapVal` equals any `List`/`Set`/`Map` with equal content. `ListVal(IntVal(1L)) == listOf(IntVal(1L))` is `true`.
- `StrVal.equals(IPrimitiveVal, ignoreCase)` returns `false` for `NullVal`; it never matches the string `"null"`.
- Sharing a Collection Value stores the same mutable instance. Primary constructors adopt the passed container; copying constructors copy only its outer container. Use `deepCopy()` for independent mutable descendants.
- A read-only Kotlin collection interface does not freeze caller aliases. Capture fixed content in the consuming graph/schema adapter; return independent mutable copies from compatibility reads. Keep structural record types outside the sealed `IValue` hierarchy.
- `BoolVal` uses singleton instances. `BoolVal(true) === BoolVal.T` is always `true`.
- `NullVal` is a `data object`. Identity comparison (`===`) and equality (`==`) both work.
- `Unsure.new(String)` returns `null` when the string does not match a known `Unsure.core` value.
- `IntVal` stores `Long`, `FloatVal` stores `Double` -- no type ambiguity.
- `compareTo` is inconsistent with `equals`: `IntVal(1L)` and `FloatVal(1.0)` compare as equivalent but are not equal. `TreeSet` and `TreeMap` collapse comparator-equivalent keys; `sorted()` and hash collections keep both.
