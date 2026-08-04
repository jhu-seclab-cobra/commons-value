package edu.jhu.cobra.commons.value.serializer

/**
 * Enum class representing different data types with their associated byte values and string labels.
 *
 * @property byte The byte representation of the type.
 * @property str The string label for the type.
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
