# Extension Utilities

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
- **`String.intVal: IntVal`** -- Parses string to `IntVal`. Raises `ParseException` on invalid input.
- **`Double.floatVal: FloatVal`** -- Wraps `Double` in `FloatVal`.
- **`Float.floatVal: FloatVal`** -- Converts `Float` to `FloatVal` (widens to `Double`).
- **`String.floatVal: FloatVal`** -- Parses string to `FloatVal`. Raises `ParseException` on invalid input.
- **`Number.numVal: NumVal`** -- **Deprecated.** Wraps any `Number` in `NumVal`. Use `intVal` or `floatVal` instead.
- **`String.numVal: NumVal`** -- **Deprecated.** Parses string to `NumVal`. Use `String.intVal` or `String.floatVal` instead.
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
- **`Set<*>.setVal: SetVal`** -- Converts set elements via `toVal`.
- **`Map<*, *>.mapVal: MapVal`** -- Converts keys via `toString()`, values via `toVal`.
- **`IntRange.rangeVal: RangeVal`** -- Converts `IntRange` to `RangeVal`.

### Null-Safe Defaults

- **`ListVal?.orEmpty(): ListVal`** -- Returns receiver or empty `ListVal`.
- **`MapVal?.orEmpty(): MapVal`** -- Returns receiver or empty `MapVal`.
- **`SetVal?.orEmpty(): SetVal`** -- Returns receiver or empty `SetVal`.

### Comparison

- **`IPrimitiveVal.compareTo(other: IPrimitiveVal): Int`** -- Compares same-type primitives. `IntVal` compares as `Long`; `FloatVal` compares as `Double`; `NumVal` compares as `Double` (deprecated); `StrVal` compares lexicographically; `BoolVal` compares `false < true`; `NullVal` equals `NullVal`. Raises `IllegalArgumentException` on cross-type comparison.

### Regex

- **`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Escapes special characters, replaces `Unsure` core strings with regex patterns (`.*`, `\d+`, `(true|false)`).
- **`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Returns the regex pattern for the `Unsure` variant.

### Range Checks (on `Number`)

- **`Number.isInLongRange: Boolean`** -- `true` if representable as `Long`.
- **`Number.isInIntRange: Boolean`** -- `true` if representable as `Int`.
- **`Number.isInShortRange: Boolean`** -- `true` if representable as `Short`.
- **`Number.isInByteRange: Boolean`** -- `true` if representable as `Byte`.

### String Interop

- **`String.startsWith(other: StrVal): Boolean`** -- Checks prefix against `StrVal.core`.

## Gotchas

- `String.intVal` and `String.floatVal` throw `ParseException`, not `NumberFormatException`.
- `String.numVal` is deprecated; throws `ParseException`.
- `numVal` extensions are deprecated. Use `intVal` for integers and `floatVal` for floating-point.
- `Any?.toVal` delegates to type-specific extensions. Unsupported types raise `IllegalArgumentException`.
- `Map<*, *>.mapVal` calls `toString()` on keys -- non-string keys lose type information.
- `IPrimitiveVal.compareTo` does not support cross-type comparison (e.g., `IntVal` vs `StrVal`).
