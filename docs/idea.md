# commons-value Idea

## 1. Context

**Problem Statement**
Modules within the Cobra system need to exchange and persist various data types (strings, numbers, booleans, collections, maps, etc.), but Kotlin/JVM native types lack a unified abstraction layer. Using `Any` directly loses type safety, while defining value types independently per module leads to duplication and inconsistency. commons-value provides a unified, serializable value type system that solves type safety and serialization problems in cross-module data exchange.

**System Role**
commons-value is the foundational value type library for the Cobra system, providing upper modules with unified value representation and serialization capabilities.

**Data Flow**
- **Inputs:** Kotlin native types (String, Number, Boolean, List, Set, Map, IntRange, null)
- **Outputs:** Unified value types (IValue and its subtypes), serialized binary/text materials
- **Connections:** Kotlin native types → commons-value → upper business modules / storage layer / transport layer

**Scope Boundaries**
- **Owned:** Value type definitions, inter-value-type conversions, native-to-value-type conversions, value serialization and deserialization
- **Not Owned:** Business logic, storage engines, network transport protocols, upper module-specific data models

## 2. Concepts

**Conceptual Diagram**
```
                        IValue (sealed)
                       /              \
              IPrimitiveVal          ICollectionVal
             (sealed)                (sealed)
            /   |   |   \          /   |    |    \
        StrVal NumVal BoolVal   ListVal SetVal MapVal RangeVal
                |       |
             NullVal  Unsure

        IValSerializer<Material>
        /         |            \
  ByteArray  ByteBuffer   CharBuffer
  Serializer Serializer   Serializer
```

**Core Concepts**

- **Value (IValue)**
  The unified value abstraction serving as the root interface for all values in the system. Each Value holds a `core` property representing its internal data. Values split into two families: primitive values and collection values. All Value types form a sealed hierarchy, ensuring compile-time exhaustive checking.

- **Primitive Value (IPrimitiveVal)**
  Represents atomic, indivisible data. Contains five concrete types: string value (StrVal), numeric value (NumVal), boolean value (BoolVal), null value (NullVal), and uncertain value (Unsure). Primitive values are leaf nodes in the value hierarchy and do not contain other Values.

- **Collection Value (ICollectionVal)**
  Represents aggregate structures containing multiple Values. Contains four concrete types: list value (ListVal), set value (SetVal), map value (MapVal), and range value (RangeVal). Collection values can nest any IValue, supporting recursive structures.

- **Unsure**
  A placeholder representing values where the type is known but the actual value is undetermined. Used in pattern matching and template scenarios, it can represent "any string", "any number", "any boolean", or "any primitive value". Unsure can be converted to regex patterns for fuzzy value matching.

- **Value Conversion**
  The bidirectional conversion mechanism between Kotlin native types and Value types. Extension properties (e.g., `Number.numVal`, `String.strVal`) convert from native types to Values. The `Any?.toVal` extension provides unified automatic type-inferred conversion.

- **Serializer (IValSerializer)**
  The serialization and deserialization abstraction for values. Parameterized by a material type (Material), it supports encoding IValue into different formats. Serializers are independent of value types, identifying concrete types through type tags (Type enum).

- **Type Tag**
  An enum tag used to identify value types during serialization. Each value type (including numeric subtypes) has a unique byte and string tag, ensuring accurate type restoration during serialization and deserialization.

## 3. Contracts & Flow

**Data Contracts**

- **With Upper Modules:** Provides IValue as the unified value interface. Upper modules exchange data through IValue and its subtypes. Extension properties enable conversion between Kotlin native types and IValue.
- **With Storage/Transport Layer:** Provides IValSerializer to serialize IValue into ByteArray, ByteBuffer, or CharBuffer for persistence or network transmission.

**Internal Processing Flow**

1. **Value Creation** - Wrap native data into IValue via constructors or extension properties (`.numVal`, `.strVal`, `.boolVal`, `.toVal`)
2. **Value Manipulation** - Read, modify, and compare values through type-specific operators and methods
3. **Value Serialization** - Serializer branches on the sealed type, writing type tags and data content
4. **Value Deserialization** - Serializer reads type tags and reconstructs the corresponding IValue instance
5. **Value Conversion** - Restore IValue to Kotlin native types via the `core` property or type conversion methods

## 4. Scenarios

- **Typical:** A business module creates `NumVal(42)` and `StrVal("hello")`, assembles them into `MapVal("count" to NumVal(42), "name" to StrVal("hello"))`, serializes via `DftByteArraySerializerImpl` for storage, and deserializes back on read
- **Boundary:** `NullVal` represents missing values; `NumVal.truncate()` downcasts to the smallest integer type (Byte < Short < Int < Long) without precision loss; empty collections maintain correct types through serialization round-trips
- **Interaction:** Upper modules use `Unsure.STR` as a pattern placeholder, converting it to a regex via `toRegex()` for value matching; `Any?.toVal` automatically infers and converts Kotlin native types to the corresponding IValue subtypes
