# Glossary — Values

The value family: the closed set of data items the library represents, serializes and converts.

---

## Values

### Value
- **Definition:** A data item of the closed value family, which splits into Primitive Value instances and Collection Value instances so that a consumer can analyze every case exhaustively.
- **Example:** `StrVal("a")` → a value of the string form.
- **Source:** `IValue.kt` `IValue`

### Primitive Value
- **Is a:** Value
- **Definition:** A Value with atomic content in one of six forms: string, integer, floating-point, boolean, null or uncertain.
- **Example:** `IntVal(1)` → a Primitive Value of the integer form.
- **Source:** `IPrimitiveVal.kt` `IPrimitiveVal`; `StrVal.kt` `StrVal`; `IntVal.kt` `IntVal`; `FloatVal.kt` `FloatVal`; `BoolVal.kt` `BoolVal`; `NullVal.kt` `NullVal`; `Unsure.kt` `Unsure`

| Member | Definition | Reference | Literature | Tools |
|--------|------------|-----------|------------|-------|
| `StrVal` | A byte string; its text holds each well-formed UTF-8 sequence as a character, each other byte as one unit U+DC80..U+DCFF, and each unpaired surrogate outside U+DC80..U+DCFF its constructor received unchanged. | none | none | none |
| `IntVal` | A signed 64-bit integer. | none | none | none |
| `FloatVal` | An IEEE 754 binary64 floating-point number. | none | none | none |
| `BoolVal` | A boolean. | none | none | none |
| `NullVal` | The null scalar. | none | none | none |
| `Unsure` | A primitive placeholder with unknown content. | none | none | none |

### Collection Value
- **Is a:** Value
- **Definition:** A Value that aggregates other Value instances in one of four forms, an ordered list, a set, a string-keyed map or an integer range, whose elements may themselves be Collection Value instances.
- **Example:** `ListVal(StrVal("a"), IntVal(1))` → a Collection Value of the list form holding two Primitive Value instances.
- **Source:** `ICollectionVal.kt` `ICollectionVal`; `ListVal.kt` `ListVal`; `SetVal.kt` `SetVal`; `MapVal.kt` `MapVal`; `RangeVal.kt` `RangeVal`

| Member | Definition | Reference | Literature | Tools |
|--------|------------|-----------|------------|-------|
| `ListVal` | An ordered mutable sequence of Value instances. | none | none | none |
| `SetVal` | A mutable set of Value instances. | none | none | none |
| `MapVal` | A mutable string-keyed mapping to Value instances. | none | none | none |
| `RangeVal` | An inclusive integer interval. | none | none | none |

### Uncertain Value
- **Is a:** Primitive Value
- **Definition:** A Primitive Value whose form is known but whose content is not: any string, any number, any boolean, or any Primitive Value.
- **Example:** `Unsure.STR` → the Uncertain Value standing for any string.
- **Source:** `Unsure.kt` `Unsure`

| Member | Definition | Reference | Literature | Tools |
|--------|------------|-----------|------------|-------|
| `Unsure.ANY` | Any Primitive Value. | none | none | none |
| `Unsure.STR` | Any string. | none | none | none |
| `Unsure.NUM` | Any integer or floating-point number. | none | none | none |
| `Unsure.BOOL` | Any boolean. | none | none | none |

## Serialization

### Type Tag
- **Definition:** A serialization discriminator identifying one concrete Value form by its binary or text encoding.
- **Example:** `NullVal` → `Type.NULL`, byte 10.
- **Source:** `serializer/Type.kt` `Type`

| Member | Definition | Reference | Literature | Tools |
|--------|------------|-----------|------------|-------|
| `Type.NULL` | A tag for null. | none | none | none |
| `Type.STR` | A tag for a string whose bytes are well-formed UTF-8. | none | none | none |
| `Type.STR_BYTES` | A tag for a string holding a byte outside UTF-8, encoded by its bytes. | none | none | none |
| `Type.BOOL` | A tag for boolean with a byte payload. | none | none | none |
| `Type.BOOL_TRUE` | A tag for true without payload. | none | none | none |
| `Type.BOOL_FALSE` | A tag for false without payload. | none | none | none |
| `Type.UNSURE_ANY` | A tag for unrestricted Uncertain Value. | none | none | none |
| `Type.UNSURE_STR` | A tag for string Uncertain Value. | none | none | none |
| `Type.UNSURE_NUM` | A tag for numeric Uncertain Value. | none | none | none |
| `Type.UNSURE_BOOL` | A tag for boolean Uncertain Value. | none | none | none |
| `Type.INT` | A tag for integer. | none | none | none |
| `Type.FLOAT` | A tag for floating-point number. | none | none | none |
| `Type.RANGE` | A tag for integer range. | none | none | none |
| `Type.LIST` | A tag for list. | none | none | none |
| `Type.SET` | A tag for set. | none | none | none |
| `Type.MAP` | A tag for map. | none | none | none |
