# commons-value Idea

## 1. Context

**Problem Statement**
Static analysis engines operate across multiple modules (parsers, graph builders, constraint solvers, serializers) that must exchange typed data. Passing raw platform types loses type safety and forces each module to handle serialization independently. A shared intermediate representation (IR) for values eliminates duplication, ensures type-safe cross-module data exchange, and centralizes serialization logic.

**System Role**
commons-value is the IR value type system for the Cobra static analysis engine, providing a sealed type hierarchy that all internal modules use to represent, exchange, and persist analysis data.

**Data Flow**
- **Inputs:** Language adapter outputs (mapped from source language types), raw platform types from internal modules
- **Outputs:** IR value types (IValue and subtypes), serialized binary/text materials
- **Connections:** Language adapters → commons-value IR → cross-module consumers (graph storage, constraint solvers, reporters) / storage layer / transport layer

**Scope Boundaries**
- **Owned:** IR value type definitions, inter-value-type conversions, platform-type-to-IR conversions, value serialization and deserialization
- **Not Owned:** Source language type systems, language-specific type juggling/casting, language adapter layers (owned by each analysis module), business logic, storage engines, network transport protocols

## 2. Concepts

**Conceptual Diagram**
```
Source Language                  commons-value IR                    Cross-Module Consumers
(PHP, Java, ...)               ┌──────────────────┐
                               │     IValue        │
  Language       ┌────────────►│  (sealed hierarchy)├──────────►  Graph property storage
  Adapter        │             │                    │              Constraint solvers
  Layer    ──────┘             │  Primitives:       │              Pattern matching
  (per module)                 │    Str, Num, Bool, │              Serialization layer
                               │    Null, Unsure    │              Reporter modules
                               │  Collections:      │
                               │    List, Set, Map, │
                               │    Range           │
                               └────────┬───────────┘
                                        │
                                        ▼
                                   Serializers
                               (ByteArray, ByteBuffer,
                                CharBuffer)
```

**Core Concepts**

- **Name:** Value (IValue)
- **Definition:** Root sealed interface representing any data item in the IR. Splits into primitive and collection families, enabling compile-time exhaustive checking.
- **Scope:** All concrete value types. Excludes source language type semantics.
- **Relationships:** Parent of Primitive Value and Collection Value. Input/output type for Serializer.

- **Name:** Primitive Value
- **Definition:** Atomic, indivisible data item. Five concrete forms: string, numeric, boolean, null, and uncertain.
- **Scope:** Leaf nodes only. Excludes composite or nested structures.
- **Relationships:** Subtype of Value. Contains String, Numeric, Boolean, Null, and Uncertain values.

- **Name:** Collection Value
- **Definition:** Aggregate structure containing multiple values. Four forms: ordered list, unique set, string-keyed map, and numeric range. Supports recursive nesting.
- **Scope:** Includes list, set, map, and range. Excludes language-specific collection semantics.
- **Relationships:** Subtype of Value. Elements are Values, enabling arbitrary nesting.

- **Name:** Uncertain Value
- **Definition:** Placeholder for values where type is known but content is undetermined. Represents "any string", "any number", "any boolean", or "any primitive." Converts to regex patterns for fuzzy matching.
- **Scope:** Typed uncertainty markers for primitives only. Excludes collection-level uncertainty.
- **Relationships:** Subtype of Primitive Value. Used by pattern matching consumers.

- **Name:** Value Conversion
- **Definition:** One-way mechanism from platform-native types to IR values via extension properties, plus a universal type-inferred converter.
- **Scope:** Platform-to-IR conversions for supported types. Excludes source-language-specific coercion rules.
- **Relationships:** Bridges platform types and Value.

- **Name:** Serializer
- **Definition:** Stateless encoding/decoding abstraction parameterized by material type. Uses type tags for accurate value reconstruction.
- **Scope:** All value types in binary and text formats. Excludes transport protocols and storage engines.
- **Relationships:** Consumes Value and Type Tag. Produces serialized materials.

- **Name:** Type Tag
- **Definition:** Enum identifying each value type during serialization. Each tag has a unique byte and string representation for exact type reconstruction.
- **Scope:** Tags for all concrete value types. Excludes runtime type information beyond serialization.
- **Relationships:** Used by Serializer for type discrimination. One-to-one with concrete Value subtypes.

## 3. Contracts & Flow

**Data Contracts**

- **With Language Adapters:** Provides the Value sealed hierarchy as IR target. Adapters map source language types to IR values using Value Conversion. Language-specific semantics (type juggling, implicit casting, overflow) are the adapter's responsibility.
- **With Cross-Module Consumers:** Provides Value as the unified data exchange type. Consumers operate through the sealed hierarchy without knowledge of originating source language.
- **With Storage/Transport Layer:** Provides Serializer to encode values into binary or text materials for persistence or transmission.

**Internal Processing Flow**

1. **Value Creation** — Wrap platform data into IR values via constructors or conversion extensions
2. **Value Manipulation** — Read, modify, and compare values through type-specific operations
3. **Value Serialization** — Serializer branches on sealed type, writing type tags and data
4. **Value Deserialization** — Serializer reads type tags and reconstructs value instances
5. **Value Extraction** — Restore platform types from IR values via internal representation property

## 4. Scenarios

- **Typical:** An analysis module receives parsed source data, uses the language adapter to convert it into IR values, assembles a map value representing a data flow node, serializes it for graph property storage, and deserializes it when a downstream module queries the node.
- **Boundary:** Null values represent missing or undefined analysis data. Numeric value truncation downcasts to the smallest integer type without precision loss. Empty collections maintain correct types through serialization round-trips.
- **Interaction:** A pattern matching module uses uncertain values as typed placeholders in analysis templates, converting them to regex patterns for matching against graph properties. Different language adapters map their respective integer types to the same IR numeric value, enabling shared analysis logic across languages.
