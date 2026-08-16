# commons-value Design -- Extension Functions

Part of [commons-value design](design-primitive.md). Specifies extension functions and exception types.

---

## Function Specifications

### NumericConversions (extension properties)

**`Long.intVal: IntVal`** -- Wraps Long as IntVal.

**`Int.intVal: IntVal`** -- Widens Int to Long, wraps as IntVal.

**`Short.intVal: IntVal`** -- Widens Short to Long, wraps as IntVal.

**`Byte.intVal: IntVal`** -- Widens Byte to Long, wraps as IntVal.

**`Double.floatVal: FloatVal`** -- Wraps Double as FloatVal.

**`Float.floatVal: FloatVal`** -- Widens Float to Double, wraps as FloatVal.

**`String.intVal: IntVal`** -- Parses string to integer, returns IntVal. Uses `toLongOrNull()`. Throws `NumberFormatException` on invalid input.

**`String.floatVal: FloatVal`** -- Parses string to decimal, returns FloatVal. Uses `toDoubleOrNull()`. Throws `NumberFormatException` on invalid input.

### NumericRangeChecks (extension properties)

**`Number.isInLongRange / isInIntRange / isInShortRange / isInByteRange: Boolean`** -- Whether the number's exact numeric value lies within the target type's range. Compares via `BigDecimal`, so fractional values are judged by numeric value (2.5 is in Int range; Int.MAX_VALUE + 0.5 is not). Non-finite doubles and floats (NaN, infinities) are `false`. Exact-width integer inputs short-circuit to `true`.

### PatternConversions (extension functions)

**`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Escapes special regex chars, replaces Unsure placeholders with regex patterns (ANY/STR -> `.*`, NUM -> `-?\d+(\.\d+)?([eE][+-]?\d+)?`, BOOL -> `(true|false)`).

**`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Returns regex pattern corresponding to Unsure type.

### PrimitiveConversions (extension functions)

**`String.strVal: StrVal`** -- Wraps String as StrVal.

**`Char.strVal: StrVal`** -- Wraps Char (as String) as StrVal.

**`Path.strVal: StrVal`** -- Wraps Path string representation as StrVal.

**`File.strVal: StrVal`** -- Wraps File path as StrVal.

**`Boolean.boolVal: BoolVal`** -- Returns `BoolVal.T` for true, `BoolVal.F` for false.

**`Any?.primitiveVal: IPrimitiveVal`** -- Converts null/Long/Double/String/Char/Boolean/IPrimitiveVal to corresponding IPrimitiveVal. Long and Int map to IntVal. Float and Double map to FloatVal. Char maps to StrVal. Throws `IllegalArgumentException` for unsupported types.

Primitive comparison is the `IPrimitiveVal.compareTo` member: [design-primitive.md](design-primitive.md).

**`String.startsWith(other: StrVal): Boolean`** -- Checks if string starts with StrVal's content.

### CollectionConversions (extension properties)

**`Collection<*>.listVal: ListVal`** — Converts collection to ListVal via `toVal` on each element.

**`Collection<*>.setVal: SetVal`** — Converts collection to SetVal via `toVal` on each element.

**`Map<*, *>.mapVal: MapVal`** — Converts map to MapVal; keys toString'd, values via `toVal`.

**`IntRange.rangeVal: RangeVal`** — Converts IntRange to RangeVal.

**`LongRange.rangeVal: RangeVal`** — Converts LongRange to RangeVal.

**`ListVal?.orEmpty(): ListVal`** — Returns self or empty ListVal if null.

**`SetVal?.orEmpty(): SetVal`** — Returns self or empty SetVal if null.

**`MapVal?.orEmpty(): MapVal`** — Returns self or empty MapVal if null.

### ValueConversions (top-level extension)

**`Any?.toVal: IValue`** -- Universal converter: null -> NullVal, Long/Int -> IntVal, Double/Float -> FloatVal, String/Char -> StrVal, Boolean -> BoolVal, List -> ListVal, Map -> MapVal, IntRange/LongRange -> RangeVal, Set -> SetVal, IValue -> identity. Throws `IllegalArgumentException` for unsupported types.

Wire-format helper functions (`asHexInt`, buffer readers): [design-serializer.md](design-serializer.md).

---

## Exception / Error Types

| Exception | When Raised |
|-----------|------------|
| `IllegalArgumentException` | `Any?.toVal` / `Any?.primitiveVal` called on unsupported type; serializer encounters unknown IValue subtype, nesting deeper than `MAX_NESTING_DEPTH`, or string content with unpaired surrogates |
| `ValFormatException` (extends `IllegalArgumentException`) | Any malformed material passed to `deserialize`: unknown tag, truncated payload, invalid size/count prefix, unparsable number, excessive nesting, empty material, trailing material ([design-serializer.md](design-serializer.md)) |
| `IndexOutOfBoundsException` | `ListVal.get`/`set`/`subList` with out-of-range index; `StrVal.get` with out-of-range index |
| `NumberFormatException` | `String.intVal` / `String.floatVal` / `String.asHexInt()` on invalid input |
| `BufferUnderflowException` | WireFormat ByteBuffer/CharBuffer read helpers when insufficient data remains (never leaked by `deserialize`) |
