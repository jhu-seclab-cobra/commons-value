# Conversion Extensions

> Kotlin extension properties and functions for native-to-IR conversion, comparison, and regex.

## Quick Start

```kotlin
import edu.jhu.cobra.commons.value.*

val n = 42.intVal            // IntVal(42L)
val f = 3.14.floatVal        // FloatVal(3.14)
val s = "hello".strVal       // StrVal("hello")
val b = true.boolVal         // BoolVal.T
val v = null.toVal           // NullVal
val list = listOf(1, 2).listVal  // ListVal(IntVal(1L), IntVal(2L))
```

## API

### Primitive Conversion Extensions

- **`Int.intVal: IntVal`** -- Converts `Int` to `IntVal` (widens to `Long`).
- **`Long.intVal: IntVal`** -- Wraps `Long` in `IntVal`.
- **`Short.intVal: IntVal`** -- Converts `Short` to `IntVal` (widens to `Long`).
- **`Byte.intVal: IntVal`** -- Converts `Byte` to `IntVal` (widens to `Long`).
- **`String.intVal: IntVal`** -- Parses string to `IntVal`. Raises `NumberFormatException` on invalid input.
- **`Double.floatVal: FloatVal`** -- Wraps `Double` in `FloatVal`.
- **`Float.floatVal: FloatVal`** -- Converts `Float` to `FloatVal` (widens to `Double`).
- **`String.floatVal: FloatVal`** -- Parses string to `FloatVal`. Raises `NumberFormatException` on invalid input.
- **`String.strVal: StrVal`** -- Wraps string in `StrVal`.
- **`Char.strVal: StrVal`** -- Wraps character as single-char `StrVal`.
- **`Path.strVal: StrVal`** -- Wraps `java.nio.file.Path` as `StrVal`.
- **`File.strVal: StrVal`** -- Wraps `java.io.File` path as `StrVal`.
- **`Boolean.boolVal: BoolVal`** -- Returns `BoolVal.T` or `BoolVal.F`.
- **`Any?.primitiveVal: IPrimitiveVal`** -- Converts `null`, `Number`, `String`, `Boolean`, or existing `IPrimitiveVal`. Raises `IllegalArgumentException` on unsupported types.
- **`Any?.toVal: IValue`** -- Converts any supported type including collections (`List`, `Set`, `Map`, `IntRange`) and existing `IValue`. Raises `IllegalArgumentException` on unsupported types.

### Collection Conversion Extensions

- **`Collection<*>.listVal: ListVal`** -- Converts collection elements via `toVal`.
- **`Collection<*>.setVal: SetVal`** -- Converts collection elements via `toVal`, deduplicating.
- **`Map<*, *>.mapVal: MapVal`** -- Converts keys via `toString()`, values via `toVal`.
- **`IntRange.rangeVal: RangeVal`** -- Converts `IntRange` to `RangeVal`.

### Null-Safe Defaults

- **`ListVal?.orEmpty(): ListVal`** -- Returns receiver or empty `ListVal`.
- **`MapVal?.orEmpty(): MapVal`** -- Returns receiver or empty `MapVal`.
- **`SetVal?.orEmpty(): SetVal`** -- Returns receiver or empty `SetVal`.

### Comparison

- **`IPrimitiveVal.compareTo(other: IPrimitiveVal): Int`** -- Compares primitives. `IntVal` and `FloatVal` compare numerically, including mixed `IntVal`/`FloatVal` pairs; `StrVal` compares lexicographically; `BoolVal` compares `false < true`; `NullVal` equals `NullVal`. Raises `IllegalArgumentException` on any other cross-type comparison.

### Regex

- **`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Escapes special characters, replaces `Unsure` core strings with regex patterns (`.*`, `\d+`, `(true|false)`).
- **`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Returns the regex pattern for the `Unsure` variant.

### Range Checks (on `Number`)

- **`Number.isInLongRange: Boolean`** -- `true` if the exact numeric value fits in `Long` (BigDecimal comparison; `false` for NaN and infinities; fractional values judged by numeric value).
- **`Number.isInIntRange: Boolean`** -- `true` if the exact numeric value fits in `Int` (same semantics as `isInLongRange`).
- **`Number.isInShortRange: Boolean`** -- `true` if the exact numeric value fits in `Short` (same semantics as `isInLongRange`).
- **`Number.isInByteRange: Boolean`** -- `true` if the exact numeric value fits in `Byte` (same semantics as `isInLongRange`).

### String Interop

- **`String.startsWith(other: StrVal): Boolean`** -- Checks prefix against `StrVal.core`.

## Gotchas

- `Any?.toVal` delegates to type-specific extensions. Unsupported types raise `IllegalArgumentException`.
- `Map<*, *>.mapVal` calls `toString()` on keys -- non-string keys lose type information.
- `IPrimitiveVal.compareTo` supports cross-type comparison only for numeric pairs (`IntVal` vs `FloatVal`); any other cross-type pair (e.g., `IntVal` vs `StrVal`) raises `IllegalArgumentException`.
