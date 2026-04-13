# commons-value Implementation

## APIs

**[Apache Commons Lang3]** `import org.apache.commons.lang3.math.NumberUtils` -- `NumberUtils.createNumber(str)` for string-to-Number parsing in serializer. Throws `NumberFormatException` on invalid input.

## Libraries

- `org.apache.commons:commons-lang3:3.13.0` -- string-to-Number parsing. Add to `build.gradle.kts` dependencies.

## Developer Instructions

- `String.numVal` uses `toLongOrNull()`/`toDoubleOrNull()` for parsing, not `NumberUtils`. Only `String.asNumber()` and `DftByteArraySerializerImpl` use `NumberUtils.createNumber()`.
- IR types do not model any specific language's type system. Language-specific semantics (type juggling, casting, overflow behavior) belong in the consuming analysis module's adapter layer.
