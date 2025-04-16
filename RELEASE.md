# Release Notes

## Version 0.1.0

[![](https://jitpack.io/v/jhu-seclab-cobra/commons-value.svg)](https://jitpack.io/#jhu-seclab-cobra/commons-value)
[![codecov](https://codecov.io/gh/jhu-seclab-cobra/commons-value/branch/main/graph/badge.svg)](https://codecov.io/gh/jhu-seclab-cobra/commons-value)
[![license](https://img.shields.io/github/license/jhu-seclab-cobra/commons-value)](./LICENSE)
[![last commit](https://img.shields.io/github/last-commit/jhu-seclab-cobra/commons-value)](https://github.com/jhu-seclab-cobra/commons-value/commits/main)
![Repo Size](https://img.shields.io/github/repo-size/jhu-seclab-cobra/commons-value)
![Kotlin JVM](https://img.shields.io/badge/Kotlin%20JVM-2.0.1%20%7C%20JVM%201.8%2B-blue?logo=kotlin)

Initial release of the `commons-value` library, providing a robust value type system for Java/Kotlin applications.

### Features

- Type-safe primitive value wrappers:
    - `BoolVal`: Boolean values (with predefined `T`/`F` instances)
    - `NumVal`: Numeric values (supports all Java number types)
    - `StrVal`: String values
    - `NullVal`: Null value representation
    - `Unsure`: Representation for uncertain values
- Collection value types:
    - `ListVal`: Type-safe list wrapper
    - `SetVal`: Type-safe set wrapper
    - `MapVal`: Type-safe map wrapper
    - `RangeVal`: Type-safe range wrapper
- Utility functions for easy conversion between Java types and value types
- Full Java interoperability (no Kotlin runtime required)
- Comprehensive documentation and type safety

## Installation

The library is available through JitPack. Add it to your project using either Gradle or Maven.

### Gradle (Kotlin DSL)

Add the JitPack repository to your build file:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.jhu-seclab-cobra:commons-value:0.1.0")
}
```

### Gradle (Groovy DSL)

Add the JitPack repository:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:

```groovy
dependencies {
    implementation 'com.github.jhu-seclab-cobra:commons-value:0.1.0'
}
```

### Maven

Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>com.github.jhu-seclab-cobra</groupId>
    <artifactId>commons-value</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Requirements

- Java 8 or later
- No additional dependencies required for Java projects
- Apache Commons Lang3 (automatically included)

## Usage Example

```java
// Java usage example
BoolVal boolVal = BoolVal.T;                    // true
IntVal intVal = new IntVal(42);                 // 42
StrVal strVal = new StrVal("Hello");            // "Hello"
NumVal numVal = new NumVal(3.14);               // 3.14

// Collection examples
ListVal listVal = new ListVal(Arrays.asList(1, 2, 3));
SetVal setVal = new SetVal(new HashSet<>(Arrays.asList("a", "b", "c")));
MapVal mapVal = new MapVal(Collections.singletonMap("key", "value"));
RangeVal rangeVal = new RangeVal(1, 10);

// Get the underlying values
boolean b = boolVal.getCore();                  // true
int i = intVal.getCore();                       // 42
String s = strVal.getCore();                    // "Hello"
Number n = numVal.getCore();                    // 3.14
```

```kotlin
// Optional Kotlin usage example
val boolVal = BoolVal.T                         // true
val intVal = IntVal(42)                         // 42
val strVal = StrVal("Hello")                    // "Hello"
val numVal = NumVal(3.14)                       // 3.14

// Collection examples with extension functions
val listVal = listOf(1, 2, 3).listVal
val setVal = setOf("a", "b", "c").setVal
val mapVal = mapOf("key" to "value").mapVal
val rangeVal = (1..10).rangeVal

// Get the underlying values
val b: Boolean = boolVal.core                   // true
val i: Int = intVal.core                        // 42
val s: String = strVal.core                     // "Hello"
val n: Number = numVal.core                     // 3.14
```

## Documentation

- [API Documentation](https://jitpack.io/com/github/jhu-seclab-cobra/commons-value/0.1.0/javadoc/)
- [Source Code](https://github.com/jhu-seclab-cobra/commons-value)

## License

Copyright (c) 2024 Johns Hopkins University Security Lab COBRA. All rights reserved.

This project is licensed under the terms of the license found in the LICENSE file in the root directory of this source
tree. 