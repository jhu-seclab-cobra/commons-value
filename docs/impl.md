# commons-value Implementation

## APIs

**[Apache Commons Lang3]** `import org.apache.commons.lang3.math.NumberUtils` -- `NumberUtils.createNumber(str)` for string-to-Number parsing in serializer. Throws `NumberFormatException` on invalid input.

## Libraries

- `org.apache.commons:commons-lang3:3.13.0` -- string-to-Number parsing. Add to `build.gradle.kts` dependencies.

## Developer Instructions

- `IntVal` is Long-backed. `FloatVal` is Double-backed.
- `String.intVal` uses `toLongOrNull()` for parsing. `String.floatVal` uses `toDoubleOrNull()`. Only `String.asNumber()` and `DftByteArraySerializerImpl` use `NumberUtils.createNumber()`.
- IR types do not model any specific language's type system. `IntVal` is Long-width, `FloatVal` is Double-width. Language-specific semantics (type juggling, casting, overflow behavior, narrower integer types) belong in the consuming analysis module's adapter layer.
