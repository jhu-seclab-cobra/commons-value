# commons-value — Performance Measurement

## Entry Point

Run the configured performance suite from the repository root:

```sh
./gradlew :jhu-seclab-cobra-commons-value:performanceTest
```

The task selects tests tagged performance and runs with its declared heap settings.
The suite covers primitive, mixed and collection serialization, deserialization, serialized sizes and Value construction.

## Measurement Contract

- Consume every measured result so that JVM optimization cannot remove the work.
- Compare revisions with identical input seeds, JDK, heap settings and warm-up; report the spread across runs.
- Report allocation with an allocation profiler; a heap delta after GC is not allocation per operation.
- Keep measurements and revision identities outside product documentation.
- [Serializer Open Gaps](design-serializer.md#open-gaps) states the limitations of the existing benchmark and recursive implementation.
