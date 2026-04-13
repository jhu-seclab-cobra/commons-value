# commons-value

> Common value -- the intermediate representation that gives analysis modules a shared language.

Type-safe IR value system with serialization for the Cobra static analysis engine.

[![Release](https://img.shields.io/badge/release-v0.1.0-blue.svg)](https://github.com/jhu-seclab-cobra/commons-value/releases/tag/v0.1.0)
[![](https://jitpack.io/v/jhu-seclab-cobra/commons-value.svg)](https://jitpack.io/#jhu-seclab-cobra/commons-value)
[![codecov](https://codecov.io/gh/jhu-seclab-cobra/commons-value/branch/main/graph/badge.svg)](https://codecov.io/gh/jhu-seclab-cobra/commons-value)
[![license](https://img.shields.io/github/license/jhu-seclab-cobra/commons-value)](./LICENSE)
![Kotlin JVM](https://img.shields.io/badge/Kotlin%20JVM-2.0.21%20%7C%20JVM%201.8%2B-blue?logo=kotlin)

## Install

Java 8+. Add the JitPack repository and dependency:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.jhu-seclab-cobra:commons-value:v0.1.0")
}
```

## Usage

```kotlin
import edu.jhu.cobra.commons.value.primitive.*
import edu.jhu.cobra.commons.value.collection.*
import edu.jhu.cobra.commons.value.serializer.*

val map = MapVal("name" to StrVal("Alice"), "age" to NumVal(30))
val list = ListVal(StrVal("a"), NumVal(1), BoolVal.T)
val range = RangeVal(1, 100)

val serializer = DftByteArraySerializerImpl()
val bytes = serializer.serialize(map)
val restored = serializer.deserialize(bytes) // MapVal
```

## API

**Primitives** (`IPrimitiveVal`): `StrVal`, `NumVal`, `BoolVal`, `NullVal`, `Unsure`

**Collections** (`ICollectionVal`): `ListVal`, `SetVal`, `MapVal` (String keys), `RangeVal`

**Serializers** (`IValSerializer<Material>`): `DftByteArraySerializerImpl` (ByteArray), `DftByteBufferSerializerImpl` (ByteBuffer), `DftCharBufferSerializerImpl` (CharBuffer)

Full type specifications and method details in [docs/design.md](./docs/design.md).

## Documentation

- [docs/idea.md](./docs/idea.md) -- concepts, terminology, and system role
- [docs/design.md](./docs/design.md) -- overview, primitive types, and validation rules
- [docs/design-collection.md](./docs/design-collection.md) -- collection types
- [docs/design-serializer.md](./docs/design-serializer.md) -- serializers and type tags
- [docs/design-utils.md](./docs/design-utils.md) -- extension functions and exceptions

## License

[GPL-2.0](./LICENSE)
