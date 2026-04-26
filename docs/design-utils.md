# commons-value Design -- Extension Functions

> **Target design.** Current implementation uses `NumVal`; migration to `IntVal`/`FloatVal` is tracked in `performance.md` P6-1.

Part of [commons-value design](design-primitive.md). Specifies extension functions and exception types.

---

## Function Specifications

### PrimitiveUtils (extension functions)

**`Long.intVal: IntVal`** -- Wraps Long as IntVal.

**`Double.floatVal: FloatVal`** -- Wraps Double as FloatVal.

**`String.intVal: IntVal`** -- Parses string to integer, returns IntVal. Uses `toLongOrNull()`. Throws `ParseException` on invalid input.

**`String.floatVal: FloatVal`** -- Parses string to decimal, returns FloatVal. Uses `toDoubleOrNull()`. Throws `ParseException` on invalid input.

**`String.strVal: StrVal`** -- Wraps String as StrVal.

**`Char.strVal: StrVal`** -- Wraps Char (as String) as StrVal.

**`Path.strVal: StrVal`** -- Wraps Path string representation as StrVal.

**`File.strVal: StrVal`** -- Wraps File path as StrVal.

**`Boolean.boolVal: BoolVal`** -- Returns `BoolVal.T` for true, `BoolVal.F` for false.

**`Any?.primitiveVal: IPrimitiveVal`** -- Converts null/Long/Double/String/Boolean/IPrimitiveVal to corresponding IPrimitiveVal. Long and Int map to IntVal. Float and Double map to FloatVal. Throws `IllegalArgumentException` for unsupported types.

**`IPrimitiveVal.compareTo(other: IPrimitiveVal): Int`** -- Compares two primitives of same type. IntVal by Long, FloatVal by Double, StrVal by String, BoolVal by Boolean, NullVal always equal. Throws `IllegalArgumentException` for cross-type comparison.

**`StrVal.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Escapes special regex chars, replaces Unsure placeholders with regex patterns (ANY/STR -> `.*`, NUM -> `\d+`, BOOL -> `(true|false)`).

**`Unsure.toRegex(doCaseIgnore: Boolean = false): Regex`** -- Returns regex pattern corresponding to Unsure type.

**`String.startsWith(other: StrVal): Boolean`** -- Checks if string starts with StrVal's content.

### CollectionUtils (extension properties)

**`Collection<*>.listVal: ListVal`** — Converts collection to ListVal via `toVal` on each element.

**`Collection<*>.setVal: SetVal`** — Converts collection to SetVal via `toVal` on each element.

**`Set<*>.setVal: SetVal`** — Converts set to SetVal via `toVal` on each element.

**`Map<*, *>.mapVal: MapVal`** — Converts map to MapVal; keys toString'd, values via `toVal`.

**`IntRange.rangeVal: RangeVal`** — Converts IntRange to RangeVal.

**`ListVal?.orEmpty(): ListVal`** — Returns self or empty ListVal if null.

**`SetVal?.orEmpty(): SetVal`** — Returns self or empty SetVal if null.

**`MapVal?.orEmpty(): MapVal`** — Returns self or empty MapVal if null.

### Utils (top-level extension)

**`Any?.toVal: IValue`** -- Universal converter: null -> NullVal, Long/Int -> IntVal, Double/Float -> FloatVal, String -> StrVal, Boolean -> BoolVal, List -> ListVal, Map -> MapVal, IntRange -> RangeVal, Set -> SetVal, IValue -> identity. Throws `IllegalArgumentException` for unsupported types.

### SerializerUtils (extension functions)

**`String.asNumber(): Number`** — Parses string to Number via Apache Commons NumberUtils. Throws `NumberFormatException`.

**`String.asHexInt(): Int`** — Parses hex string to Int. Throws `NumberFormatException`.

**`Int.asHexString(): String`** — Converts Int to hex string.

**`DataInput.asByteArray(size: Int): ByteArray`** — Reads bytes from DataInput; size=0 returns empty, size>0 reads exactly that many, size<0 reads until EOF.

**`DataInput.asByteSequence(available: Int): Sequence<Byte>`** — Lazy byte sequence from DataInput; available=0 returns empty, available>0 yields that many, available<0 reads until EOF.

---

## Exception / Error Types

| Exception | When Raised |
|-----------|------------|
| `IllegalArgumentException` | `Any?.toVal` / `Any?.primitiveVal` called on unsupported type; `IPrimitiveVal.compareTo` with incompatible types; serializer encounters unknown IValue subtype; deserializer encounters unknown type tag; empty ByteBuffer deserialization |
| `IndexOutOfBoundsException` | `ListVal.get`/`set`/`subList` with out-of-range index; `StrVal.get` with out-of-range index |
| `ParseException` | `String.intVal` / `String.floatVal` when string is not a valid number |
| `NumberFormatException` | `String.asNumber()` / `String.asHexInt()` on invalid input |
| `BufferUnderflowException` | ByteBuffer/CharBuffer read operations when insufficient data remains |
