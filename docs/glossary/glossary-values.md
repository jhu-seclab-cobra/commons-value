# Glossary — Values

The value family: the closed set of data items the library represents, serializes and converts.

---

## Values

### Value
- **Definition:** A data item of the closed value family, which splits into primitive values and collection values so that a consumer can analyze every case exhaustively.
- **Example:** `StrVal("a")` → a value of the string form.
- **Source:** `IValue.kt` `IValue`
- **Reference:** none

### Primitive Value
- **Is a:** Value
- **Definition:** An atomic value in one of six forms: string, integer, floating-point, boolean, null or uncertain.
- **Example:** `IntVal(1)` → a primitive value of the integer form.
- **Source:** `IPrimitiveVal.kt` `IPrimitiveVal`; `StrVal.kt` `StrVal`; `IntVal.kt` `IntVal`; `FloatVal.kt` `FloatVal`; `BoolVal.kt` `BoolVal`; `NullVal.kt` `NullVal`; `Unsure.kt` `Unsure`
- **Reference:** none

### Collection Value
- **Is a:** Value
- **Definition:** A value that aggregates other values in one of four forms, an ordered list, a set, a string-keyed map or a numeric range, whose elements may themselves be collection values.
- **Example:** `ListVal(StrVal("a"), IntVal(1))` → a collection value of the list form holding two primitive values.
- **Source:** `ICollectionVal.kt` `ICollectionVal`; `ListVal.kt` `ListVal`; `SetVal.kt` `SetVal`; `MapVal.kt` `MapVal`; `RangeVal.kt` `RangeVal`
- **Reference:** none

### Uncertain Value
- **Is a:** Primitive Value
- **Definition:** A primitive value whose form is known but whose content is not: any string, any number, any boolean, or any primitive value.
- **Example:** `Unsure.STR` → the uncertain value standing for any string.
- **Source:** `Unsure.kt` `Unsure`
- **Reference:** none
