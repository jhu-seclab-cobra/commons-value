# commons-value Domain Model

## Entities

| Entity | Semantic role | Exists when |
|--------|---------------|-------------|
| Value | Root of the closed value family; every data item exchanged between modules is a value. | Only as one of its concrete primitive or collection kinds. |
| Primitive Value | Atomic, indivisible data item. Six kinds: string, integer, floating-point, boolean, null, plus uncertain placeholder. | Created from a platform primitive, parsed from text, or decoded from material. |
| Collection Value | Aggregate structure containing multiple values. Four kinds: ordered list, unique set, string-keyed map, numeric range. | Created empty, from existing values, or decoded from material; a range exists for any two integer bounds. |
| Uncertain Value | Typed placeholder for undetermined content. Four levels: any-primitive, any-string, any-number, any-boolean. | Selected by placeholder identifier, inferred from an example primitive, or chosen by kind. |
| Type Tag | Serialization discriminator identifying each value kind for binary/text encoding. | Fixed set declared by the serialization format; one or more per value kind. |

## Relations

| Relation | Direction | Cardinality | Meaning |
|----------|-----------|-------------|---------|
| contains | Collection → Value | one-to-many | Collection holds values (recursive nesting allowed) |
| converts-to | Platform type → IR Value | many-to-one | One-way mapping from platform-native types to IR values |
| serializes-as | IR Value → Material | one-to-one | Value encodes to binary or text format via type tag |

## Invariants

- The value hierarchy is closed: every value is either a primitive value or a collection value.
- The primitive family has exactly six kinds: string, integer, floating-point, boolean, null, uncertain.
- The collection family has exactly four kinds: ordered list, unique set, string-keyed map, numeric range.
- Boolean values have exactly two instances (true, false). The null value has exactly one instance.
- Integer values are 64-bit signed integers. Floating-point values are 64-bit IEEE 754 floats. No other numeric widths exist.
- Map keys are strings only. Map values are any value (recursive nesting).
- Every serialized value begins with a type tag.
- Type tags are unique: no two tags share a binary or text encoding.
- Every type tag identifies exactly one value kind; a kind may own several tags (boolean encodings differ by material format).
- Serialization round-trip preserves exact value kind: serialize then deserialize yields a value equal to the original.
- Two integer values with equal content are equal. Two floating-point values with equal content are equal.
- Primitive values form a total order. Kinds rank null < boolean < numeric < string < uncertain; the two numeric kinds order as one family by exact numeric value, with NaN above every number. Order-equivalence does not imply equality: equal-valued members of different numeric kinds compare as equivalent yet remain distinct values.
- Value graphs are trees: no collection value contains itself, directly or transitively.
- A value tree nests at most 1000 levels; serialization, deserialization, and structural copy reject deeper trees.
- Serializable string content is well-formed UTF-16: serialization rejects strings holding unpaired surrogates.
- Deserialization consumes its material exactly: a well-formed encoding followed by trailing content is malformed.

Rationale: [concept.md](concept.md). Implementation mapping: [design-primitive.md](design-primitive.md).
