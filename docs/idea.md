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
- **Not Owned:** Source language type systems, language-specific type juggling/casting, business logic, storage engines, network transport protocols, analysis module-specific data models

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
- **Definition:** The root sealed interface representing any data item in the IR. Each value holds an internal representation accessed through a uniform property. Values split into two families: primitive and collection. The sealed hierarchy enables compile-time exhaustive checking across all consumers.
- **Scope:** Includes all concrete value types. Excludes source language type semantics and business logic.
- **Relationships:** Parent of Primitive Value and Collection Value. Consumed by all cross-module interfaces. Input/output type for Serializer.

- **Name:** Primitive Value
- **Definition:** An atomic, indivisible data item in the IR. Five concrete forms exist: string, numeric, boolean, null, and uncertain. Primitive values are leaf nodes and do not contain other values.
- **Scope:** Includes string, numeric, boolean, null, and uncertain representations. Excludes composite or nested structures.
- **Relationships:** Subtype of Value. Contains String Value, Numeric Value, Boolean Value, Null Value, and Uncertain Value.

- **Name:** Collection Value
- **Definition:** An aggregate structure containing multiple values. Four concrete forms exist: ordered list, unique set, string-keyed map, and numeric range. Collections support nesting and recursive structures.
- **Scope:** Includes list, set, map, and range representations. Excludes language-specific collection semantics (e.g., PHP associative arrays).
- **Relationships:** Subtype of Value. Contains List Value, Set Value, Map Value, and Range Value. Elements are Values, enabling arbitrary nesting.

- **Name:** Uncertain Value
- **Definition:** A placeholder representing values where the type is known but the actual content is undetermined. Used in pattern matching and template scenarios to represent "any string", "any number", "any boolean", or "any primitive." Converts to regex patterns for fuzzy value matching.
- **Scope:** Includes typed uncertainty markers for primitives. Excludes collection-level uncertainty.
- **Relationships:** Subtype of Primitive Value. Used by pattern matching consumers. Converts to regex via the conversion mechanism.

- **Name:** Value Conversion
- **Definition:** The bidirectional mechanism between platform-native types and IR value types. Extension properties convert platform types to IR values. A universal converter provides automatic type-inferred conversion. The internal representation property restores platform types from IR values.
- **Scope:** Includes platform-to-IR and IR-to-platform conversions for supported types. Excludes source-language-specific type coercion rules.
- **Relationships:** Bridges platform types and Value. Used by Language Adapter for initial mapping.

- **Name:** Serializer
- **Definition:** The serialization and deserialization abstraction for values. Parameterized by a material type, it encodes any IR value into a target format and reconstructs values from that format. Serializers are stateless singletons, independent of value types, and use type tags for accurate reconstruction.
- **Scope:** Includes encoding/decoding for all value types in binary and text formats. Excludes transport protocols and storage engines.
- **Relationships:** Consumes Value and Type Tag. Produces and consumes serialized materials.

- **Name:** Type Tag
- **Definition:** An enum identifying each value type (including numeric subtypes) during serialization. Each tag has a unique byte and string representation, ensuring the serializer can reconstruct the exact original value type.
- **Scope:** Includes tags for all concrete value types and numeric subtypes. Excludes runtime type information beyond serialization.
- **Relationships:** Used by Serializer for type discrimination. Maps one-to-one with concrete Value subtypes.

- **Name:** Language Adapter
- **Definition:** The layer within each analysis module that maps between source language type semantics and the commons-value IR. Each language has different type rules (e.g., different integer widths, different collection structures). The adapter maps these to the appropriate IR types. The adapter layer is not part of commons-value itself but is the primary consumer interface.
- **Scope:** Includes source-language-to-IR mapping logic. Excludes IR type definitions and serialization.
- **Relationships:** Consumes Value Conversion to produce IR values. Owned by each analysis module (e.g., cobraphp-core), not by commons-value.

## 3. Contracts & Flow

**Data Contracts**

- **With Language Adapters:** Provides the Value sealed hierarchy as the IR target. Adapters map source language types to IR values using Value Conversion. Language-specific type semantics (type juggling, implicit casting, overflow behavior) are the adapter's responsibility.
- **With Cross-Module Consumers:** Provides Value as the unified data exchange type. Consumers read, compare, and transform values through the sealed type hierarchy without knowledge of the originating source language.
- **With Storage/Transport Layer:** Provides Serializer to encode values into binary or text materials for persistence or network transmission.

**Internal Processing Flow**

1. **Value Creation** — Wrap platform data into IR values via constructors or conversion extensions
2. **Value Manipulation** — Read, modify, and compare values through type-specific operations
3. **Value Serialization** — Serializer branches on the sealed type, writing type tags and data content
4. **Value Deserialization** — Serializer reads type tags and reconstructs the corresponding value instance
5. **Value Extraction** — Restore platform types from IR values via the internal representation property

## 4. Scenarios

- **Typical:** An analysis module receives parsed source data, uses the language adapter to convert it into IR values (numeric, string, and map values), assembles a map value representing a data flow node, serializes it for graph property storage, and deserializes it when a downstream module queries the node
- **Boundary:** Null values represent missing or undefined analysis data; numeric value truncation downcasts to the smallest integer type without precision loss; empty collections maintain correct types through serialization round-trips
- **Interaction:** A pattern matching module uses uncertain values as typed placeholders in analysis templates, converting them to regex patterns for value matching against graph properties; different language adapters map their respective integer types to the same IR numeric value, enabling shared analysis logic across languages
