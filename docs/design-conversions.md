# commons-value Design -- Extension Functions

Part of [commons-value design](design-primitive.md). Specifies extension functions and exception types.

---

## Function Specifications

### NumericConversions (extension properties)

**`Long.intVal: IntVal`** -- Wraps Long as IntVal.

**`Double.floatVal: FloatVal`** -- Wraps Double as FloatVal.

**`String.intVal: IntVal`** -- Parses string to integer, returns IntVal. Uses `toLongOrNull()`. Throws `NumberFormatException` on invalid input.

**`String.floatVal: FloatVal`** -- Parses string to decimal, returns FloatVal. Uses `toDoubleOrNull()`. Throws `NumberFormatException` on invalid input.

### NumericRangeChecks (extension properties)

**`Number.isInLongRange / isInIntRange / isInShortRange / isInByteRange: Boolean`** -- Whether the number's exact numeric value lies within the target type's range. Compares via `BigDecimal`, so fractional values are judged by numeric value (2.5 is in Int range; Int.MAX_VALUE + 0.5 is not). Non-finite doubles and floats (NaN, infinities) are `false`. Exact-width integer inputs short-circuit to `true`.

### PatternConversions (extension functions)

**`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Escapes special regex chars, replaces Unsure placeholders with regex patterns (ANY/STR -> `.*`, NUM -> `\d+`, BOOL -> `(true|false)`).

**`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Returns regex pattern corresponding to Unsure type.

### PrimitiveConversions (extension functions)

**`String.strVal: StrVal`** -- Wraps String as StrVal.

**`Char.strVal: StrVal`** -- Wraps Char (as String) as StrVal.

**`Path.strVal: StrVal`** -- Wraps Path string representation as StrVal.

**`File.strVal: StrVal`** -- Wraps File path as StrVal.

**`Boolean.boolVal: BoolVal`** -- Returns `BoolVal.T` for true, `BoolVal.F` for false.

**`Any?.primitiveVal: IPrimitiveVal`** -- Converts null/Long/Double/String/Boolean/IPrimitiveVal to corresponding IPrimitiveVal. Long and Int map to IntVal. Float and Double map to FloatVal. Throws `IllegalArgumentException` for unsupported types.

**`IPrimitiveVal.compareTo(other: IPrimitiveVal): Int`** -- Compares two primitives of same type. IntVal by Long, FloatVal by Double, StrVal by String, BoolVal by Boolean, NullVal always equal. Throws `IllegalArgumentException` for cross-type comparison.

**`String.startsWith(other: StrVal): Boolean`** -- Checks if string starts with StrVal's content.

### CollectionConversions (extension properties)

**`Collection<*>.listVal: ListVal`** — Converts collection to ListVal via `toVal` on each element.

**`Collection<*>.setVal: SetVal`** — Converts collection to SetVal via `toVal` on each element.

**`Map<*, *>.mapVal: MapVal`** — Converts map to MapVal; keys toString'd, values via `toVal`.

**`IntRange.rangeVal: RangeVal`** — Converts IntRange to RangeVal.

**`ListVal?.orEmpty(): ListVal`** — Returns self or empty ListVal if null.

**`SetVal?.orEmpty(): SetVal`** — Returns self or empty SetVal if null.

**`MapVal?.orEmpty(): MapVal`** — Returns self or empty MapVal if null.

### ValueConversions (top-level extension)

**`Any?.toVal: IValue`** -- Universal converter: null -> NullVal, Long/Int -> IntVal, Double/Float -> FloatVal, String -> StrVal, Boolean -> BoolVal, List -> ListVal, Map -> MapVal, IntRange -> RangeVal, Set -> SetVal, IValue -> identity. Throws `IllegalArgumentException` for unsupported types.

### WireFormat (extension functions)

**`String.asHexInt(): Int`** — Parses hex string to Int. Throws `NumberFormatException`.

**`Int.asHexString(): String`** — Converts Int to hex string.

---

## Exception / Error Types

| Exception | When Raised |
|-----------|------------|
| `IllegalArgumentException` | `Any?.toVal` / `Any?.primitiveVal` called on unsupported type; `IPrimitiveVal.compareTo` with incompatible types; serializer encounters unknown IValue subtype; deserializer encounters unknown type tag; empty ByteBuffer deserialization |
| `IndexOutOfBoundsException` | `ListVal.get`/`set`/`subList` with out-of-range index; `StrVal.get` with out-of-range index |
| `NumberFormatException` | `String.intVal` / `String.floatVal` / `String.asHexInt()` on invalid input |
| `BufferUnderflowException` | ByteBuffer/CharBuffer read operations when insufficient data remains |
