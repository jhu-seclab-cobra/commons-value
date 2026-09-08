package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.StrVal
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.nio.BufferUnderflowException
import java.nio.charset.MalformedInputException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/*
 * Tests for the wire-validation helpers specified in design-serializer.md (Wire Format Helpers).
 *
 * - `should return receiver from requireWellFormedUtf16 for well-formed string` — Empty, ASCII, BMP, and
 *   paired-surrogate strings pass through unchanged.
 * - `should throw IllegalArgumentException from requireWellFormedUtf16 for unpaired surrogate` — Lone high,
 *   lone low, high followed by non-low, and low-before-high are rejected.
 * - `should return block result from decodeMaterial on success` — A successful body's value is returned.
 * - `should rethrow ValFormatException unchanged from decodeMaterial` — An existing ValFormatException is not
 *   wrapped.
 * - `should wrap BufferUnderflowException as ValFormatException` — Truncated material is normalized with cause.
 * - `should wrap NumberFormatException as ValFormatException` — Unparsable number is normalized with cause.
 * - `should wrap CharacterCodingException as ValFormatException` — Malformed UTF-8 is normalized with cause.
 * - `should wrap IllegalArgumentException as ValFormatException preserving message` — Validation failure keeps
 *   its message and cause.
 * - `should propagate unrelated exception from decodeMaterial` — A non-decoding failure is not converted.
 * - `should validate size prefix at boundaries` — 0 and remaining pass; -1 and remaining+1 fail.
 * - `should return value from requireDecodedType when type matches` — IntVal decoded as IntVal is returned.
 * - `should throw IllegalArgumentException from requireDecodedType on type mismatch` — StrVal decoded as IntVal
 *   is rejected.
 */
internal class WireValidationTest {
    // --- String.requireWellFormedUtf16 ---

    @ParameterizedTest
    @ValueSource(strings = ["", "ascii", "你好", "😀", "a😀b"])
    fun `should return receiver from requireWellFormedUtf16 for well-formed string`(text: String) {
        assertSame(text, text.requireWellFormedUtf16())
    }

    @ParameterizedTest
    @ValueSource(strings = ["\uD83D", "\uDE00", "\uD83Dx", "\uDE00\uD83D"])
    fun `should throw IllegalArgumentException from requireWellFormedUtf16 for unpaired surrogate`(text: String) {
        assertFailsWith<IllegalArgumentException> { text.requireWellFormedUtf16() }
    }

    // --- decodeMaterial ---

    @Test
    fun `should return block result from decodeMaterial on success`() {
        assertEquals(42, decodeMaterial { 42 })
    }

    @Test
    fun `should rethrow ValFormatException unchanged from decodeMaterial`() {
        val original = ValFormatException("already normalized")
        val thrown = assertFailsWith<ValFormatException> { decodeMaterial { throw original } }
        assertSame(original, thrown)
    }

    @Test
    fun `should wrap BufferUnderflowException as ValFormatException`() {
        val cause = BufferUnderflowException()
        val thrown = assertFailsWith<ValFormatException> { decodeMaterial { throw cause } }
        assertSame(cause, thrown.cause)
    }

    @Test
    fun `should wrap NumberFormatException as ValFormatException`() {
        val cause = NumberFormatException("x")
        val thrown = assertFailsWith<ValFormatException> { decodeMaterial { throw cause } }
        assertSame(cause, thrown.cause)
    }

    @Test
    fun `should wrap CharacterCodingException as ValFormatException`() {
        val cause = MalformedInputException(1)
        val thrown = assertFailsWith<ValFormatException> { decodeMaterial { throw cause } }
        assertSame(cause, thrown.cause)
    }

    @Test
    fun `should wrap IllegalArgumentException as ValFormatException preserving message`() {
        val cause = IllegalArgumentException("Invalid element size -1: expected 0..4")
        val thrown = assertFailsWith<ValFormatException> { decodeMaterial { throw cause } }
        assertEquals(cause.message, thrown.message)
        assertSame(cause, thrown.cause)
    }

    @Test
    fun `should propagate unrelated exception from decodeMaterial`() {
        assertFailsWith<IllegalStateException> { decodeMaterial { error("not a decoding failure") } }
    }

    // --- checkSizePrefix ---

    @ParameterizedTest
    @CsvSource("0, 4, true", "4, 4, true", "-1, 4, false", "5, 4, false", "0, 0, true", "1, 0, false")
    fun `should validate size prefix at boundaries`(
        size: Int,
        remaining: Int,
        valid: Boolean,
    ) {
        if (valid) {
            assertEquals(size, checkSizePrefix(size, remaining, "size"))
        } else {
            assertFailsWith<IllegalArgumentException> { checkSizePrefix(size, remaining, "size") }
        }
    }

    // --- requireDecodedType ---

    @Test
    fun `should return value from requireDecodedType when type matches`() {
        val value = IntVal(3L)
        assertSame(value, requireDecodedType<IntVal>(value, "range start"))
    }

    @Test
    fun `should throw IllegalArgumentException from requireDecodedType on type mismatch`() {
        assertFailsWith<IllegalArgumentException> { requireDecodedType<IntVal>(StrVal("x"), "range start") }
    }
}
