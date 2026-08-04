# commons-value Design -- Serializers

Part of [commons-value design](design-primitive.md). Specifies the `IValSerializer` interface and implementations.

---

## Class / Type Specifications

### IValSerializer\<Material : Any\>

**Responsibility:** Interface defining serialization and deserialization of IValue to/from a material format. `Material` is upper-bounded by `Any` (non-nullable).

**Methods:**

| Method | Behavior | Input | Output | Errors |
|--------|----------|-------|--------|--------|
| `serialize(value: IValue)` | Encodes value into material format | `value: IValue` | `Material` | `IllegalArgumentException` on unknown type |
| `deserialize(material: Material)` | Decodes material into IValue | `material: Material` | `IValue` | `IllegalArgumentException` on unknown type |

---

### DftByteArraySerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteArray` using binary format with type-byte prefix.

**Serialization Format:** `[type_byte][payload_bytes]`. Collections use length-prefixed elements. Maps encode key bytes and value bytes with size prefixes.

---

### DftByteBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `ByteBuffer` using binary format with type-byte prefix and element count for collections.

**Serialization Format:** `[type_byte][count (for collections)][elements]`. Strings are length-prefixed. Booleans use distinct type bytes (BOOL_TRUE/BOOL_FALSE). IntVal serializes as 8-byte Long. FloatVal serializes as 8-byte Double. Ranges store two Longs directly.

---

### DftCharBufferSerializerImpl

**Responsibility:** Stateless singleton serializer converting IValue to/from `CharBuffer` using human-readable text format.

**Serialization Format:** `TypeStr:payload:`. Strings use hex length prefix. Collections use hex element count. Maps encode keys as serialized StrVal with `=` delimiter.

---

### Type

**Responsibility:** Enum mapping each value type to a unique byte tag and string label for serialization.

**State/Fields:**
- `byte: Byte` — Binary type tag.
- `str: String` — String type label.

**Entries:** NULL(10), STR(20), BOOL(30), BOOL_TRUE(31), BOOL_FALSE(32), UNSURE_ANY(40), UNSURE_STR(41), UNSURE_NUM(42), UNSURE_BOOL(43), INT(57, "IntV"), FLOAT(58, "FloatV"), RANGE(60), LIST(70), SET(71), MAP(80).

`Type` owns the wire-format tag vocabulary: every tag a serializer emits or decodes is declared here, and no serializer defines tags of its own. Boolean tags are split by implementation: the byte-array serializer emits BOOL with a payload byte; the byte-buffer and char-buffer serializers emit the payload-free BOOL_TRUE / BOOL_FALSE tags.

**Serialization notes:** IntVal serializes as 8-byte Long (tag INT). FloatVal serializes as 8-byte Double (tag FLOAT).
