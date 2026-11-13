# commons-value Concept

Domain semantics: [model.md](model.md). Software structure: [design-primitive.md](design-primitive.md).

## 1. Context

**Problem Statement**
Static analysis engines operate across multiple modules (parsers, graph builders, constraint solvers, serializers) that must exchange typed data. Passing raw platform types loses type safety and forces each module to handle serialization independently. Modules exchange Value instances and serialize them through the shared serializer interfaces.

**System Role**
commons-value is the Value type system for the Cobra static analysis engine, providing a closed type hierarchy for language-neutral value content and supported interchange formats.

**Data Flow**
- **Inputs:** Language adapter outputs (mapped from source language types), raw platform types from internal modules
- **Outputs:** Value types, serialized binary/text materials
- **Connections:** Language adapters → commons-value Value → cross-module consumers (graph storage, constraint solvers, reporters) / storage layer / transport layer

**Scope Boundaries**
- **Owned:** Value type definitions, inter-value-type conversions, platform-type-to-Value conversions, value serialization and deserialization
- **Not Owned:** Source language type systems, language-specific type juggling/casting, language adapter layers (owned by each analysis module), business logic, graph structural schemas, storage engines, network transport protocols

## 2. Concepts

**Conceptual Diagram**
```
Source Language                  commons-value Value                    Cross-Module Consumers
(PHP, Java, ...)               ┌──────────────────┐
                               │      Value        │
  Language       ┌────────────►│  (closed hierarchy)├──────────►  Graph property storage
  Adapter        │             │                    │              Constraint solvers
  Layer    ──────┘             │  Primitives:       │              Pattern matching
  (per module)                 │    string, integer, │              Serialization layer
                               │    float, boolean,  │
                               │    null, uncertain  │              Reporter modules
                               │  Collections:      │
                               │    list, set, map, │
                               │    range           │
                               └────────┬───────────┘
                                        │
                                        ▼
                                   serializers
                               (binary and text
                                  materials)
```

**Core Concepts**

- **Name:** [Value](glossary/glossary-values.md#value)
- **Role:** supplies the common input and output type exchanged by consumers.
- **Scope:** All concrete value types. Excludes source language type semantics.
- **Relationships:** Parent of Primitive Value and Collection Value. Input/output type for serializer.

- **Name:** [Primitive Value](glossary/glossary-values.md#primitive-value)
- **Role:** lets consumers distinguish scalar forms exhaustively.
- **Scope:** Leaf nodes only. Excludes composite or nested structures.
- **Relationships:** Subtype of Value. Contains string, integer, floating-point, boolean, null, and Uncertain Value instances.

- **Name:** [Collection Value](glossary/glossary-values.md#collection-value)
- **Role:** groups nested values under collection-specific operations.
- **Scope:** Includes list, set, map, and range. Excludes language-specific collection semantics.
- **Relationships:** Subtype of Value. Elements are Values, enabling arbitrary nesting.

- **Name:** [Uncertain Value](glossary/glossary-values.md#uncertain-value)
- **Role:** lets pattern-matching consumers represent unknown primitive content.
- **Scope:** Typed uncertainty markers for primitives only. Excludes collection-level uncertainty.
- **Relationships:** Subtype of Primitive Value. Used by pattern matching consumers.

- **Name:** conversion
- **Role:** maps supported native inputs to the Value family.
- **Scope:** Platform-to-Value conversions for supported types. Excludes source-language-specific coercion rules.
- **Relationships:** Bridges platform types and Value.

- **Name:** serializer
- **Role:** encodes and reconstructs Values for storage or transport.
- **Scope:** All value types in binary and text formats. Excludes transport protocols and storage engines.
- **Relationships:** Consumes Value and Type Tag. Produces serialized materials.

- **Name:** [Type Tag](glossary/glossary-values.md#type-tag)
- **Role:** lets a decoder distinguish the serialized Value form.
- **Scope:** Tags for all concrete value kinds. Excludes runtime type information beyond serialization.
- **Relationships:** Used by serializer for type discrimination. Each tag identifies one value kind.

## 3. Contracts & Flow

**Data Contracts**

- **With Language Adapters:** Provides the Value sealed hierarchy as Value family. Adapters map source language types to Value instances using conversion. Language-specific semantics (type juggling, implicit casting, overflow) are the adapter's responsibility.
- **With Cross-Module Consumers:** Provides Value for language-neutral content and interchange. Graph structural records belong to graph or consumer schemas; their adapters convert to Value at compatibility and serialization boundaries.
- **With Storage/Transport Layer:** Provides serializer to encode values into binary or text materials for persistence or transmission.

**Internal Processing Flow**

Consumers construct and exchange Value instances, then pass them to a serializer for storage or transport; [serializer design](design-serializer.md) owns the API.

## 4. Scenarios

- **Typical:** An analysis module receives parsed source data, uses the language adapter to convert it into Value instances, assembles a map value representing a data flow node, serializes it for graph property storage, and deserializes it when a downstream module queries the node.
- **Boundary:** Null values represent missing or undefined analysis data. Integer and floating-point values are fixed-width; floating-point to integer conversion truncates the fractional part. Empty collections maintain correct types through serialization round-trips.
- **Interaction:** A pattern matching module uses Uncertain Value instances as typed placeholders in analysis templates, converting them to regex patterns for matching against graph properties. Different language adapters map their respective integer types to the same numeric Value, enabling shared analysis logic across languages.
