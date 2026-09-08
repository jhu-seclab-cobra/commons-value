package edu.jhu.cobra.commons.value

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/*
 * Tests for the IValue.deepCopy contract and the nesting-depth bound specified in
 * design-primitive.md (Abstract section) and design-serializer.md (MAX_NESTING_DEPTH).
 *
 * - `should return same instance from deepCopy for immutable value` — Every primitive and RangeVal is its own copy.
 * - `should return equal but distinct instance from deepCopy for mutable collection` — ListVal, SetVal, MapVal
 *   copies are equal to and not the same object as the original.
 * - `should not share nested mutable state after deepCopy` — Mutating a nested map inside the copy leaves the
 *   original list unchanged.
 * - `should accept depth within MAX_NESTING_DEPTH` — Depths 0, 1, and MAX_NESTING_DEPTH pass and are returned.
 * - `should reject depth beyond MAX_NESTING_DEPTH` — MAX_NESTING_DEPTH + 1 raises IllegalArgumentException.
 * - `should copy nested collection at MAX_NESTING_DEPTH` — A list nested exactly MAX_NESTING_DEPTH levels copies.
 * - `should reject nested collection beyond MAX_NESTING_DEPTH` — One extra level raises IllegalArgumentException.
 */
internal class IValueTest {
    companion object {
        @JvmStatic
        fun immutableValues(): Stream<Arguments> =
            Stream.of(
                Arguments.of(NullVal),
                Arguments.of(IntVal(7L)),
                Arguments.of(FloatVal(1.5)),
                Arguments.of(StrVal("text")),
                Arguments.of(BoolVal.T),
                Arguments.of(Unsure.NUM),
                Arguments.of(RangeVal(1, 3)),
            )

        @JvmStatic
        fun mutableCollections(): Stream<Arguments> =
            Stream.of(
                Arguments.of(ListVal(IntVal(1L), StrVal("a"))),
                Arguments.of(SetVal(IntVal(1L), StrVal("a"))),
                Arguments.of(MapVal("k" to IntVal(1L))),
            )

        private fun nestedList(levels: Int): IValue {
            var value: IValue = ListVal()
            repeat(levels) { value = ListVal(value) }
            return value
        }
    }

    @ParameterizedTest
    @MethodSource("immutableValues")
    fun `should return same instance from deepCopy for immutable value`(value: IValue) {
        assertSame(value, value.deepCopy())
    }

    @ParameterizedTest
    @MethodSource("mutableCollections")
    fun `should return equal but distinct instance from deepCopy for mutable collection`(value: IValue) {
        val copy = value.deepCopy()
        assertEquals(value, copy)
        assertNotSame(value, copy)
    }

    @Test
    fun `should not share nested mutable state after deepCopy`() {
        val original = ListVal(MapVal("k" to IntVal(1L)))
        val copy = original.deepCopy()
        (copy[0] as MapVal)["k"] = IntVal(2L)
        assertEquals(IntVal(1L), (original[0] as MapVal)["k"])
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, MAX_NESTING_DEPTH])
    fun `should accept depth within MAX_NESTING_DEPTH`(depth: Int) {
        assertEquals(depth, checkNestingDepth(depth))
    }

    @Test
    fun `should reject depth beyond MAX_NESTING_DEPTH`() {
        assertFailsWith<IllegalArgumentException> { checkNestingDepth(MAX_NESTING_DEPTH + 1) }
    }

    @Test
    fun `should copy nested collection at MAX_NESTING_DEPTH`() {
        val value = nestedList(MAX_NESTING_DEPTH)
        assertEquals(value, value.deepCopy())
    }

    @Test
    fun `should reject nested collection beyond MAX_NESTING_DEPTH`() {
        val value = nestedList(MAX_NESTING_DEPTH + 1)
        assertFailsWith<IllegalArgumentException> { value.deepCopy() }
    }
}
