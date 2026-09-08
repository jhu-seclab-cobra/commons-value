package edu.jhu.cobra.commons.value.serializer

/**
 * Type tags that discriminate value kinds in serialized material.
 *
 * The byte values and string labels are fixed by the wire format: material written by any
 * release decodes only while these values stay unchanged.
 *
 * @property byte The byte representation of the type, used by the binary serializers.
 * @property str The string label for the type, used by the text serializer.
 */
public enum class Type(
    public val byte: Byte,
    public val str: String,
) {
    NULL(10, "Null"),
    STR(20, "Str"),
    BOOL(30, "Bool"),
    BOOL_TRUE(31, "True"),
    BOOL_FALSE(32, "False"),
    UNSURE_ANY(40, "UnANY"),
    UNSURE_STR(41, "UnSTR"),
    UNSURE_NUM(42, "UnNUM"),
    UNSURE_BOOL(43, "UnBOOL"),
    INT(57, "IntV"),
    FLOAT(58, "FloatV"),
    RANGE(60, "Range"),
    LIST(70, "List"),
    SET(71, "Set"),
    MAP(80, "Map"),
}
