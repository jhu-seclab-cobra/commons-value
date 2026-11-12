# commons-value

> Common value: a value shared across analysis modules.

Kotlin value types and serializers for representing primitive and collection values.

## Install

Requires JDK 21 and a Gradle Kotlin DSL project. Add JitPack and the dependency to the project build:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.jhu-seclab-cobra:commons-value:v0.1.1")
}
```

## Usage

```kotlin
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.serializer.DftByteArraySerializerImpl

val value = MapVal("name" to StrVal("Alice"), "age" to IntVal(30L))
val bytes = DftByteArraySerializerImpl.serialize(value)
val restored = DftByteArraySerializerImpl.deserialize(bytes)
```

## API

- `IValue` provides `core: Any?` and `deepCopy(): IValue`.
- `IPrimitiveVal` includes `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, and `Unsure`.
- `ICollectionVal` includes `ListVal`, `SetVal`, `MapVal`, and `RangeVal`.
- `IValSerializer<Material>` provides `serialize(value: IValue): Material` and
  `deserialize(material: Material): IValue`. Implementations use `ByteArray`, `ByteBuffer`, or `CharBuffer`.

See the [value](docs/design-primitive.md), [collection](docs/design-collection.md), and
[serializer](docs/design-serializer.md) specifications for the public types and operations.

## Documentation

- [Concepts](docs/concept.md): the value family and its role.
- [Conversions](docs/design-conversions.md): conversion operations.

## For Agents

Agent-consumable documentation index at [docs/llms.txt](docs/llms.txt) (llmstxt.org format).

## License

[GPL-2.0](LICENSE)
