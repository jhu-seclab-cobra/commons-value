# commons-value Domain Model

## Entities

| Entity | Semantic role | Exists when |
|--------|---------------|-------------|
| [Value](glossary/glossary-values.md#value) | Supplies the common type exchanged between modules. | Only as one of its concrete primitive or collection kinds. |
| [Primitive Value](glossary/glossary-values.md#primitive-value) | Lets consumers distinguish scalar content from nested containers. | Created from a platform primitive, parsed from text, or decoded from material. |
| [Collection Value](glossary/glossary-values.md#collection-value) | Groups nested values under collection-specific operations. | Created empty, from existing values, or decoded from material; a range exists for any two integer bounds. |
| [Uncertain Value](glossary/glossary-values.md#uncertain-value) | Supplies unknown primitive content for consumer pattern matching. | Selected by placeholder identifier, inferred from an example primitive, or chosen by kind. |
| [Type Tag](glossary/glossary-values.md#type-tag) | Selects the interpretation of an encoded payload. | Fixed set declared by the serialization format; one or more per value kind. |

## Relations

| Relation | Direction | Cardinality | Meaning |
|----------|-----------|-------------|---------|
| contains | Collection → Value | one-to-many | Collection holds values (recursive nesting allowed) |
| converts-to | Platform type → Value | many-to-one | One-way mapping from platform-native types to Value instances |
| serializes-as | Value → Material | one-to-one | Value encodes to binary or text format via Type Tag |

## Invariants

- The value hierarchy is closed: every value is either a Primitive Value or a Collection Value.
- The primitive family has exactly six kinds: string, integer, floating-point, boolean, null, uncertain.
- The collection family has exactly four kinds: ordered list, unique set, string-keyed map, integer range.
- Boolean values have exactly two instances (true, false). The null value has exactly one instance.
- Integer values are 64-bit signed integers. Floating-point values are 64-bit IEEE 754 floats. No other numeric widths exist.
- Map keys are strings only. Map values are any value (recursive nesting).
- Every serialized value begins with a Type Tag.
- Type Tag values are unique: no two tags share a binary or text encoding.
- Every Type Tag identifies exactly one value kind; a kind may own several tags (boolean encodings differ by material format).
- Serialization round-trip preserves exact value kind: serialize then deserialize yields a value equal to the original.
- Two integer values with equal content are equal. Two floating-point values with equal content are equal.
- Primitive Value instances form a total order. Kinds rank null < boolean < numeric < string < uncertain; the two numeric kinds order as one family by exact numeric value, with NaN above every number. Order-equivalence does not imply equality: equal-valued members of different numeric kinds compare as equivalent yet remain distinct values.
- Supported Value graphs are acyclic and may share descendants; copying and serialization visit each occurrence and do not preserve sharing identity.
- Supported expanded Value occurrences have depth at most 1000, counting the root as zero; serialization, deserialization and structural copy reject deeper inputs. The copy implementation difference is recorded in [collection Open Gaps](design-collection.md#open-gaps).
- Serializable string content is well-formed UTF-16: serialization rejects strings holding unpaired surrogates.
- Deserialization consumes its material exactly: a well-formed encoding followed by trailing content is malformed.

Rationale: [concept.md](concept.md). Implementation mapping: [design-primitive.md](design-primitive.md).
