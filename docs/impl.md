# commons-value Implementation

## Libraries

- No runtime dependencies. Kotlin stdlib and the JDK cover all parsing and buffer handling.

## Developer Instructions

- `IntVal` is Long-backed. `FloatVal` is Double-backed.
- `String.intVal` uses `toLongOrNull()` for parsing. `String.floatVal` uses `toDoubleOrNull()`.
- IR types do not model any specific language's type system. `IntVal` is Long-width, `FloatVal` is Double-width. Language-specific semantics (type juggling, casting, overflow behavior, narrower integer types) belong in the consuming analysis module's adapter layer.
