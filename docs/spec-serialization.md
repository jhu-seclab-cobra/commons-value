# commons-value — Serialization

## Problem

Input: a supported acyclic [Value](glossary/glossary-values.md#value) graph for encoding, or one complete serialized material for decoding.
Output: material preserving Value form and content, or the reconstructed Value under [model invariants](model.md#invariants).
Material errors raise ValFormatException; unsupported nesting or a map key holding an unpaired surrogate at encoding raise IllegalArgumentException.

## Steps

1. Start at depth zero and reject a Value occurrence deeper than 1000.
2. For encoding, select the material-specific Type Tag and encode a primitive payload or range bounds according to [the format design](design-serializer.md).
3. Validate map keys as well-formed UTF-16. A string value holding an unpaired unit U+DC80..U+DCFF encodes its bytes under the string-bytes tag; any other string value encodes under the string tag, as UTF-8 in byte formats.
4. Encode container children at depth plus one in iteration order and place their counts or length-prefixed windows in the material-specific container layout.
5. For decoding, read the Type Tag and reject unknown tags or truncated payloads.
6. Validate every count, size and nested expected Value form before constructing that container or member; string and key decoding rejects invalid UTF-8; string-bytes decoding rejects a payload whose bytes are all well-formed UTF-8.
7. Decode each child within its window or count boundary, advancing depth and verifying required delimiters.
8. Require the complete top-level material and every length-prefixed child window to be consumed exactly.
9. Map decoding failures to ValFormatException with their cause.

## Invariants

- Each material is interpreted by the serializer that owns its format; coinciding primitive encodings imply no general interchangeability.
- Each child belongs to exactly one encoded occurrence, even when the source graph shares object identities.
- An invalid size cannot address material outside the current window.
- The byte-array format uses a BOOL tag with payload; buffer formats use the tag-only boolean variants.

## Termination

Encoding traverses finitely many expanded occurrences or fails at the depth guard.
Decoding consumes bounded material and counts, or rejects malformed input; recursive decoding increases depth.

## Complexity

Let b be the bytes or UTF-16 code units of material, n the expanded Value-occurrence count, and d the depth.
Nested material construction has a conservative O(d × (b + n)) time and storage bound because intermediate child material may be copied by ancestors.
Decoding has O(d × (b + n)) as a conservative traversal/copy bound, plus container hashing costs and returned storage; numeric parsing costs depend on numeric text length.
These bounds count expanded occurrences rather than distinct source objects.
