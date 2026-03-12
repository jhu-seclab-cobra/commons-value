# COBRA.COMMONS.VALUE

[![Release](https://img.shields.io/badge/release-v0.1.0-blue.svg)](https://github.com/jhu-seclab-cobra/commons-value/releases/tag/v0.1.0)
[![](https://jitpack.io/v/jhu-seclab-cobra/commons-value.svg)](https://jitpack.io/#jhu-seclab-cobra/commons-value)
[![codecov](https://codecov.io/gh/jhu-seclab-cobra/commons-value/branch/main/graph/badge.svg)](https://codecov.io/gh/jhu-seclab-cobra/commons-value)
[![license](https://img.shields.io/github/license/jhu-seclab-cobra/commons-value)](./LICENSE)
![Kotlin JVM](https://img.shields.io/badge/Kotlin%20JVM-2.0.21%20%7C%20JVM%201.8%2B-blue?logo=kotlin)

A Kotlin library providing a sealed, type-safe union value system with binary and text serialization. All values implement the `IValue` sealed interface, split into `IPrimitiveVal` and `ICollectionVal`.

## Requirements

- Java 8+

## Installation

Add the JitPack repository and dependency:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.jhu-seclab-cobra:commons-value:v0.1.0")
}
```

## Value Types

### Primitives (`IPrimitiveVal`)

| Type | Wraps | Notes |
|------|-------|-------|
| `BoolVal` | `Boolean` | Singleton — `BoolVal.T` / `BoolVal.F` |
| `StrVal` | `String` | Supports negative indexing, `uppercase()`, `lowercase()`, `trim()`, `contains()` |
| `NumVal` | `Number` | Preserves original type (Byte/Short/Int/Long/Float/Double/BigDecimal/BigInteger) |
| `NullVal` | `null` | Singleton data object |
| `Unsure` | Uncertainty marker | Enum: `ANY`, `STR`, `NUM`, `BOOL` — represents unknown values of a given type |

### Collections (`ICollectionVal`)

| Type | Wraps | Notes |
|------|-------|-------|
| `ListVal` | `ArrayList<IValue>` | Ordered, allows duplicates. Supports `+`/`-` operators, `map`, `flatMap`, `subList` |
| `SetVal` | `LinkedHashSet<IValue>` | Insertion-ordered, unique elements |
| `MapVal` | `HashMap<String, IValue>` | **String keys only**. Supports `contains`, `keys()`, `values()` |
| `RangeVal` | Start/end `NumVal` pair | Both bounds inclusive. **Numeric only** — no string or date ranges |

All collections support nesting (e.g., a `ListVal` of `MapVal`s).

## Usage

### Creating Values Directly

```kotlin
import edu.jhu.cobra.commons.value.primitive.*
import edu.jhu.cobra.commons.value.collection.*

// Primitives
val s = StrVal("hello")
val n = NumVal(42)
val b = BoolVal.T
val nil = NullVal

// Collections
val list = ListVal(StrVal("a"), NumVal(1), BoolVal.F)
val set = SetVal(NumVal(1), NumVal(2), NumVal(3))
val map = MapVal("name" to StrVal("Alice"), "age" to NumVal(30))
val range = RangeVal(1, 100)
```

### Converting from Kotlin Types via Extensions

```kotlin
import edu.jhu.cobra.commons.value.primitive.*
import edu.jhu.cobra.commons.value.collection.*
import edu.jhu.cobra.commons.value.toVal

// Primitive extensions
val s = "hello".strVal          // StrVal("hello")
val n = 42.numVal               // NumVal(42)
val b = true.boolVal            // BoolVal.T
val p = "3.14".numVal           // NumVal(3.14) — parsed as Double

// Collection extensions
val list = listOf(1, "two", true).listVal   // ListVal(NumVal(1), StrVal("two"), BoolVal.T)
val set = setOf(1, 2, 3).setVal             // SetVal(NumVal(1), NumVal(2), NumVal(3))
val map = mapOf("k" to 1).mapVal            // MapVal("k" to NumVal(1))
val range = (1..10).rangeVal                // RangeVal(1, 10)

// Generic conversion — works for null, Number, String, Boolean, List, Map, Set, IntRange
val any = (null as Any?).toVal              // NullVal
```

### Working with StrVal

```kotlin
val s = StrVal("Hello, World!")

s.length                        // 13
s[0]                            // 'H'
s[-1]                           // '!' (negative indexing from end)
s.contains("World")             // true
s.startsWith("Hello")           // true
s.uppercase()                   // StrVal("HELLO, WORLD!")
s.substringBefore(",")          // "Hello"
```

### Working with NumVal

```kotlin
val n = NumVal(42)

n.isInt                         // true
n.isPrimitiveIntegerType        // true
n.toDouble()                    // 42.0
n.compareTo(50)                 // negative (42 < 50)

// Truncate to smallest fitting integer type
val big = NumVal(100L)
NumVal.truncate(big)            // NumVal(100) — Long truncated to Int

// Type checking
NumVal(3.14).isDouble           // true
NumVal(1.toByte()).isByte       // true
```

### Working with ListVal

```kotlin
val list = ListVal(StrVal("a"), StrVal("b"), StrVal("c"))

list[0]                          // StrVal("a")
list.size                        // 3
list.contains(StrVal("b"))       // true
list.indexOf(StrVal("c"))        // 2

// Operators — return new lists
val longer = list + StrVal("d")  // ListVal("a", "b", "c", "d")
val shorter = list - StrVal("a") // ListVal("b", "c")

// In-place mutation
list += StrVal("d")
list -= StrVal("a")

// Functional operations
list.map { it.toString() }
list.subList(0, 2)               // ListVal("a", "b") — exclusive end
```

### Working with SetVal

```kotlin
val set = SetVal(NumVal(1), NumVal(2), NumVal(3))

set.size                        // 3
set.contains(NumVal(2))         // true
set.add(NumVal(4))              // true (was not present)
set.add(NumVal(1))              // false (already present)
set.remove(NumVal(3))           // true

val merged = set + NumVal(5)    // new SetVal with 5 added
```

### Working with MapVal

```kotlin
val map = MapVal("x" to NumVal(1), "y" to NumVal(2))

map["x"]                        // NumVal(1)
map["z"]                        // null (key absent)
map.size                        // 2
"x" in map                      // true

map["z"] = NumVal(3)            // add entry
map.keys()                      // Set("x", "y", "z")
map.values()                    // Collection of NumVal(1), NumVal(2), NumVal(3)

map.forEach { (k, v) -> println("$k=$v") }
```

### Working with RangeVal

```kotlin
val r = RangeVal(1, 100)

r.start                         // NumVal(1)
r.endInclusive                  // NumVal(100)
50 in r                         // true
200 in r                        // false

// Range containment
val sub = RangeVal(10, 50)
sub in r                        // true

// Combine ranges (min start to max end)
val combined = r + RangeVal(80, 200) // RangeVal(1, 200)

// Positional checks
r before RangeVal(200, 300)     // true (r ends before 200 starts)
r after RangeVal(-10, -1)       // true (r starts after -1 ends)
```

### Comparing Primitives

```kotlin
import edu.jhu.cobra.commons.value.primitive.compareTo

NumVal(1) < NumVal(2)           // true (numeric comparison)
StrVal("a") < StrVal("b")      // true (lexicographic)
BoolVal.F < BoolVal.T           // true (false < true)
```

### Serialization

Three serializer implementations are available, all supporting the full `IValue` type set including nested collections:

| Serializer | Material | Use Case |
|-----------|----------|----------|
| `DftByteArraySerializerImpl` | `ByteArray` | Storage, network transfer |
| `DftByteBufferSerializerImpl` | `ByteBuffer` | NIO / memory-mapped I/O |
| `DftCharBufferSerializerImpl` | `CharBuffer` | Human-readable text, debugging |

```kotlin
import edu.jhu.cobra.commons.value.serializer.*

// Binary serialization
val serializer = DftByteArraySerializerImpl()
val bytes: ByteArray = serializer.serialize(StrVal("hello"))
val restored: IValue = serializer.deserialize(bytes)  // StrVal("hello")

// ByteBuffer serialization
val bufSerializer = DftByteBufferSerializerImpl()
val buf: ByteBuffer = bufSerializer.serialize(NumVal(42))
val num: IValue = bufSerializer.deserialize(buf)       // NumVal(42)

// Text serialization (human-readable)
val charSerializer = DftCharBufferSerializerImpl()
val text: CharBuffer = charSerializer.serialize(BoolVal.T)  // "True:"
val bool: IValue = charSerializer.deserialize(text)         // BoolVal.T

// Nested collections serialize correctly
val nested = ListVal(MapVal("k" to NumVal(1)), SetVal(StrVal("a")))
val data = serializer.serialize(nested)
val result = serializer.deserialize(data)  // fully restored
```

## Limitations

- **MapVal keys are String only** — other types are converted via `toString()`
- **RangeVal is numeric only** — no string, date, or custom comparable ranges
- **RangeVal comparison uses Double conversion** — may lose precision for very large Long values
- **NumVal with non-standard Number types** (e.g., BigDecimal) serializes via string representation — less efficient
- **Sealed type hierarchy** — you cannot add custom `IValue` implementations outside the library
- **Deeply nested collections** may cause stack overflow during serialization (no depth limit)
- **MapVal ordering is not guaranteed** — backed by `HashMap`

## License

[GPL-2.0](./LICENSE)
