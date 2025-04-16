package edu.jhu.cobra.commons.value.serializer

import cobra.commons.value.IValue

/**
 * Defines a mechanism for serializing and deserializing [IValue] instances to and from a specific material format.
 *
 * This interface is generic, allowing implementations to specify the material type that they work with,
 * enabling flexibility in the type of data formats used for serialization.
 *
 * @param Material The type of the material to which the values are serialized and from which they are deserialized.
 *          This type parameter allows for implementations that work with various data representations,
 *          such as JSON, XML, binary formats, or even more domain-specific material types.
 */
interface IValSerializer<Material : Any> {

    /**
     * Serializes an [IValue] instance into the specified material format.
     *
     * This method converts a structured value into a format suitable for storage, transmission, or processing
     * outside the immediate system.
     *
     * Example usage:
     * ```kotlin
     * val serializer: IValSerializer<String> = JsonValSerializer()
     * val serialized = serializer.serialize(value)
     * println(serialized) // Outputs the JSON string representation of the value
     * ```
     *
     * @param value The [IValue] instance to serialize.
     * @return The serialized material as an instance of [M].
     */
    fun serialize(value: IValue): Material

    /**
     * Deserializes material of type [Material] into an [IValue] instance.
     *
     * This method reconstructs an [IValue] from a material format, enabling the system to regain a
     * structured representation of the value for internal processing or manipulation.
     *
     * Example usage:
     * ```kotlin
     * val serializer: IValSerializer<String> = JsonValSerializer()
     * val value = serializer.deserialize(jsonString)
     * println(value) // Outputs the reconstructed IValue instance
     * ```
     *
     * @param material The material from which the value is to be deserialized.
     * @return The deserialized [IValue] instance.
     */
    fun deserialize(material: Material): IValue
}
