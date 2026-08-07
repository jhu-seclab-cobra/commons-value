# COBRA.COMMONS.VALUE

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
import edu.jhu.cobra.commons.value.*
import edu.jhu.cobra.commons.value.serializer.*

val map = MapVal("name" to StrVal("Alice"), "age" to IntVal(30L))
val list = ListVal(StrVal("a"), IntVal(1L), BoolVal.T)
val range = RangeVal(1, 100)

val serializer = DftByteArraySerializerImpl
val bytes = serializer.serialize(map)
val restored = serializer.deserialize(bytes) // MapVal
```

## API

**Primitives** (`IPrimitiveVal`): `StrVal`, `IntVal`, `FloatVal`, `BoolVal`, `NullVal`, `Unsure` (`NumVal` is deprecated)

**Collections** (`ICollectionVal`): `ListVal`, `SetVal`, `MapVal` (String keys), `RangeVal`

**Serializers** (`IValSerializer<Material : Any>`): `DftByteArraySerializerImpl` (ByteArray), `DftByteBufferSerializerImpl` (ByteBuffer), `DftCharBufferSerializerImpl` (CharBuffer)

Full type specifications and method details in [docs/design-primitive.md](./docs/design-primitive.md).

## Documentation

- [docs/concept.md](./docs/concept.md) -- concepts, terminology, and system role
- [docs/design-primitive.md](./docs/design-primitive.md) -- primitive type specifications (IntVal, FloatVal, StrVal, BoolVal, NullVal, Unsure)
- [docs/design-collection.md](./docs/design-collection.md) -- collection type specifications (ListVal, SetVal, MapVal, RangeVal)
- [docs/design-serializer.md](./docs/design-serializer.md) -- serializer interface and implementations

## For Agents

Agent-consumable documentation index at [docs/llms.txt](./docs/llms.txt) (llmstxt.org format).

## Citation

If you use this repository in your research, please cite our paper:

```bibtex
@inproceedings{xu2026cobra,
  title     = {CoBrA: Context-, Branch-sensitive Static Analysis for Detecting Taint-style Vulnerabilities in PHP Web Applications},
  author    = {Xu, Yichao and Kang, Mingqing and Thimmaiah, Neil and Gjomemo, Rigel and Venkatakrishnan, V. N. and Cao, Yinzhi},
  booktitle = {Proceedings of the 48th IEEE/ACM International Conference on Software Engineering (ICSE)},
  year      = {2026},
  address   = {Rio de Janeiro, Brazil}
}
```

## License

[GPL-2.0](./LICENSE)
