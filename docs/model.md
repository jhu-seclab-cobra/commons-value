# commons-value Domain Model

> This document describes the target IR design. Current implementation uses NumVal; migration to IntVal/FloatVal is tracked in performance.md P6-1.

## Entities

| Entity | Definition |
|--------|-----------|
| Primitive Value | Atomic, indivisible data item. Five concrete forms: string, integer, floating-point, boolean, null, plus uncertain placeholder. |
| Collection Value | Aggregate structure containing multiple values. Four forms: ordered list, unique set, string-keyed map, numeric range. |
| Uncertain Value | Typed placeholder for undetermined content. Four levels: any-primitive, any-string, any-number, any-boolean. |
| Type Tag | Serialization discriminator identifying each value type for binary/text encoding. |

## Relations

| Relation | Direction | Meaning |
|----------|-----------|---------|
| contains | Collection → Value | Collection holds values (recursive nesting allowed) |
| converts-to | Platform type → IR Value | One-way mapping from JVM native types to IR values |
| serializes-as | IR Value → Material | Value encodes to binary or text format via type tag |

## Invariants

- Value hierarchy is sealed: every IValue is either IPrimitiveVal or ICollectionVal.
- IPrimitiveVal hierarchy is sealed: exactly seven subtypes (StrVal, IntVal, FloatVal, BoolVal, NullVal, Unsure, NumVal [deprecated]).
- ICollectionVal hierarchy is sealed: exactly four subtypes (ListVal, SetVal, MapVal, RangeVal).
- BoolVal has exactly two instances (T, F). NullVal has exactly one instance.
- IntVal stores Long (64-bit integer). FloatVal stores Double (IEEE 754 64-bit float). No other numeric representations.
- MapVal keys are String only. Values are any IValue (recursive nesting).
- Serialization round-trip preserves exact value type: serialize then deserialize yields a value equal to the original.
- Two IntVal instances with the same Long value are equal. Two FloatVal instances with the same Double value are equal.
