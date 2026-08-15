package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.IValue

/**
 * Signals that serialized material is malformed and cannot be deserialized into an [IValue].
 *
 * Raised by every [IValSerializer.deserialize] implementation for any defect in the material:
 * unknown type tags, truncated payloads, invalid size or count prefixes, unparsable numbers,
 * and empty or exhausted material. Extends [IllegalArgumentException] so existing callers
 * catching that type continue to work.
 *
 * @param message Description of the format defect, including decoding context.
 * @param cause The underlying decoding failure, when one exists.
 */
public class ValFormatException(
    message: String,
    cause: Throwable? = null,
) : IllegalArgumentException(message, cause)

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
public interface IValSerializer<Material : Any> {
    /**
     * Serializes an [IValue] instance into the specified material format.
     *
     * This method converts a structured value into a format suitable for storage, transmission, or processing
     * outside the immediate system.
     *
     * @param value The [IValue] instance to serialize.
     * @return The serialized material as an instance of [Material].
     */
    public fun serialize(value: IValue): Material

    /**
     * Deserializes material of type [Material] into an [IValue] instance.
     *
     * This method reconstructs an [IValue] from a material format, enabling the system to regain a
     * structured representation of the value for internal processing or manipulation.
     *
     * @param material The material from which the value is to be deserialized.
     * @return The deserialized [IValue] instance.
     * @throws ValFormatException If the material is malformed.
     */
    public fun deserialize(material: Material): IValue
}
