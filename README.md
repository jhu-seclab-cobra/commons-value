# commons-value

[![codecov](https://codecov.io/gh/jhu-seclab-cobra/commons-value/branch/main/graph/badge.svg)](https://codecov.io/gh/jhu-seclab-cobra/commons-value)
![Kotlin JVM](https://img.shields.io/badge/Kotlin%20JVM-2.4.20%20%7C%20JVM%2021%2B-blue?logo=kotlin)
[![Release](https://img.shields.io/badge/release-v0.2.0-blue.svg)](https://github.com/jhu-seclab-cobra/commons-value/releases/tag/v0.2.0)
[![last commit](https://img.shields.io/github/last-commit/jhu-seclab-cobra/commons-value)](https://github.com/jhu-seclab-cobra/commons-value/commits/main)
[![JitPack](https://jitpack.io/v/jhu-seclab-cobra/commons-value.svg)](https://jitpack.io/#jhu-seclab-cobra/commons-value)
![Repo Size](https://img.shields.io/github/repo-size/jhu-seclab-cobra/commons-value)
[![license](https://img.shields.io/github/license/jhu-seclab-cobra/commons-value)](./LICENSE)

> Common value: a value shared across analysis modules.

Kotlin value types and serializers for representing Primitive Value and Collection Value instances.

## Install

Requires JDK 21 and a Gradle Kotlin DSL project. Add JitPack and the dependency to the project build:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.jhu-seclab-cobra:commons-value:v0.2.0")
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

## Citation

It requests that every project using the library cite the project's associated paper.

```bibtex
@inproceedings{xu2026cobra,
  title     = {CoBrA: Context-, Branch-sensitive Static Analysis for Detecting Taint-style Vulnerabilities in PHP Web Applications},
  author    = {Xu, Yichao and Kang, Mingqing and Thimmaiah, Neil and Gjomemo, Rigel and Venkatakrishnan, V. N. and Cao, Yinzhi},
  booktitle = {Proceedings of the 48th IEEE/ACM International Conference on Software Engineering (ICSE)},
  year      = {2026},
  address   = {Rio de Janeiro, Brazil}
}
```

## Documentation

- [Concepts](docs/concept.md): the Value family and its role.
- [Design](docs/design-primitive.md): Value types, hierarchy and public interfaces.
- [Conversions](docs/design-conversions.md): conversion operations.

## For Agents

Agent-consumable documentation index at [docs/llms.txt](docs/llms.txt) (llmstxt.org format).

## License

[GPL-2.0](LICENSE)
