# COBRA.COMMONS.VALUE 

A Kotlin library that provides a flexible union value system with serialization support for the COBRA platform. The library offers a type-safe way to handle different value types through a unified interface.

## Features

- Type-safe union value system
- Flexible value representation through the `IValue` interface
- Built-in serialization support with multiple implementations
- Support for primitive types (BoolVal, StrVal, NumVal, NullVal)
- Support for collection types (ListVal, MapVal, SetVal, RangeVal)
- Extensible architecture for custom value types

## Requirements

- Java 8 or higher

## Installation

1. Add JitPack repository to your project:
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

2. Add the dependency:
```kotlin
implementation("com.github.COBRA-Static-Analysis:commons-value:v0.1.1")
```

## Usage

### Basic Value Creation

```kotlin
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.primitive.StrVal
import edu.jhu.cobra.commons.value.collection.ListVal

// Create a string value
val stringValue: IValue = StrVal("Hello, World!")

// Create a list value
val listValue: IValue = ListVal(listOf(1, 2, 3))
```

### Value Serialization

```kotlin
import edu.jhu.cobra.commons.value.serializer.IValSerializer
import edu.jhu.cobra.commons.value.serializer.DftByteArraySerializerImpl

// Create a serializer instance
val serializer: IValSerializer = DftByteArraySerializerImpl()

// Serialize a value
val serializedData = serializer.serialize(stringValue)

// Deserialize a value
val deserializedValue = serializer.deserialize(serializedData)
```

### Custom Value Types

```kotlin
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.primitive.IPrimitiveVal

// Define a custom primitive value type
data class CustomPrimitiveVal(
    override val core: Any?,
    val additionalProperty: String
) : IPrimitiveVal

// Use the custom value
val customValue = CustomPrimitiveVal(42, "extra info")
```

## Project Structure

- `edu.jhu.cobra.commons.value.IValue`: Core interface for all value types
- `edu.jhu.cobra.commons.value.primitive`: Primitive value implementations
  - `IPrimitiveVal`: Interface for primitive values
  - `BoolVal`: Boolean value implementation
  - `StrVal`: String value implementation
  - `NumVal`: Numeric value implementation
  - `NullVal`: Null value implementation
- `edu.jhu.cobra.commons.value.collection`: Collection value implementations
  - `ICollectionVal`: Interface for collection values
  - `ListVal`: List value implementation
  - `MapVal`: Map value implementation
  - `SetVal`: Set value implementation
  - `RangeVal`: Range value implementation
- `edu.jhu.cobra.commons.value.serializer`: Serialization utilities
  - `IValSerializer`: Serialization interface
  - `DftByteArraySerializerImpl`: Default byte array serializer
  - `DftByteBufferSerializerImpl`: Default byte buffer serializer
  - `DftCharBufferSerializerImpl`: Default character buffer serializer

## License

[GNU2.0](./LICENSE)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. 
