# commons-value — Structural Copying

## Problem

Input: a supported acyclic [Value](glossary/glossary-values.md#value) graph under [model invariants](model.md#invariants).
Output: an equal Value sharing no mutable container with the input; immutable leaves may retain identity.
Repeated references are copied at every occurrence rather than preserved as shared mutable identity.

## Steps

1. Begin at depth zero.
2. Reject depth greater than 1000 before producing the copied occurrence.
3. Return a Primitive Value or immutable integer range unchanged.
4. Allocate an empty container of the same form, preserving iteration order.
5. Copy each contained Value at depth plus one and insert the result under its original position or map key.
6. Return the completed container.

## Invariants

- Every copied mutable container is distinct from its source container.
- A shared child is traversed once per occurrence, not once per object identity.
- No memo table merges repeated references.
- Depth increases at each mutable-containment step, so a cycle cannot complete a copy.

## Termination

A finite acyclic graph has finitely many expanded occurrences.
For a cycle, depth eventually exceeds the guard and raises IllegalArgumentException.

## Complexity

Let n be the number of occurrences after expanding repeated references, d the maximum mutable-container depth, and c the copied container storage.
The traversal takes O(n) operations and O(c + d) space, excluding content-dependent hashing in sets and maps.
The number of occurrences may exceed the number of distinct source objects.

## Implementation Mapping

The accepted depth check in step 2 applies to every occurrence. The implementation checks mutable-container depth but returns a primitive leaf without that check, while serializers check every encoded or decoded Value occurrence.
The uniform model bound and this boundary differ at leaves; [the collection design](design-collection.md#open-gaps) records that implementation gap without changing the model's accepted bound.
