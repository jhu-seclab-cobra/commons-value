# commons-value -- IR value type system for static analysis

| File | Content |
|------|---------|
| concept.md | Problem context, IR value concepts, and cross-module data exchange scenarios |
| model.md | Value type hierarchy, relations, invariants, and type constraints |
| design-primitive.md | StrVal, IntVal, FloatVal, BoolVal, NullVal, Unsure type and function specifications |
| design-collection.md | ListVal, SetVal, MapVal, RangeVal type and function specifications |
| design-serializer.md | IValSerializer interface, serializer implementations, and wire-format helpers |
| design-conversions.md | Conversion extension functions, range checks, and exception types |
| impl.md | Runtime dependency policy and developer instructions |
| performance.md | Serialization benchmarks, optimization state, and known bottlenecks |
| llms.txt | Agent-facing API index linking llms/ module docs |
