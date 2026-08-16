package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Black-box tests for the regex pattern conversion extension functions derived from the design doc.
 *
 * - `should escape special regex chars in StrVal toRegex` — StrVal.toRegex escape
 * - `should replace Unsure STR placeholder with wildcard in StrVal toRegex` — StrVal.toRegex STR
 * - `should replace Unsure NUM placeholder with digit pattern in StrVal toRegex` — StrVal.toRegex NUM
 * - `should replace Unsure BOOL placeholder with bool pattern in StrVal toRegex` — StrVal.toRegex BOOL
 * - `should replace Unsure NUM toString rendering with digit pattern in StrVal toRegex` — StrVal.toRegex NUM toString form
 * - `should replace Unsure ANY toString rendering with wildcard in StrVal toRegex` — StrVal.toRegex ANY toString form
 * - `should produce case-insensitive regex from StrVal toRegex` — StrVal.toRegex ignoreCase
 * - `should match any string for Unsure ANY toRegex` — Unsure.toRegex ANY
 * - `should match any string for Unsure STR toRegex` — Unsure.toRegex STR
 * - `should match digits for Unsure NUM toRegex` — Unsure.toRegex NUM
 * - `should match signed decimal and exponent numerals for Unsure NUM toRegex` — NUM covers IntVal/FloatVal renderings
 * - `should match true or false for Unsure BOOL toRegex` — Unsure.toRegex BOOL
 * - `should produce case-insensitive regex from Unsure toRegex` — Unsure.toRegex ignoreCase
 */
internal class PatternConversionsTest {
    // --- StrVal.toRegex ---

    @Test
    fun `should escape special regex chars in StrVal toRegex`() {
        val regex = StrVal("Hello.World").toRegex()
        assertTrue(regex.matches("Hello.World"))
        assertFalse(regex.matches("HelloXWorld"))
    }

    @Test
    fun `should replace Unsure STR placeholder with wildcard in StrVal toRegex`() {
        val regex = StrVal("Hello${Unsure.STR.core}").toRegex()
        assertTrue(regex.matches("Hello World"))
        assertTrue(regex.matches("HelloAnything"))
    }

    @Test
    fun `should replace Unsure NUM placeholder with digit pattern in StrVal toRegex`() {
        val regex = StrVal("item${Unsure.NUM.core}").toRegex()
        assertTrue(regex.matches("item123"))
        assertFalse(regex.matches("itemabc"))
    }

    @Test
    fun `should replace Unsure BOOL placeholder with bool pattern in StrVal toRegex`() {
        val regex = StrVal("is${Unsure.BOOL.core}").toRegex()
        assertTrue(regex.matches("istrue"))
        assertTrue(regex.matches("isfalse"))
        assertFalse(regex.matches("ismaybe"))
    }

    @Test
    fun `should replace Unsure NUM toString rendering with digit pattern in StrVal toRegex`() {
        val regex = StrVal("item${Unsure.NUM}").toRegex()
        assertTrue(regex.matches("item123"))
        assertFalse(regex.matches("itemabc"))
        assertFalse(regex.matches("item${Unsure.NUM}"))
    }

    @Test
    fun `should replace Unsure ANY toString rendering with wildcard in StrVal toRegex`() {
        val regex = StrVal("prefix${Unsure.ANY}suffix").toRegex()
        assertTrue(regex.matches("prefixanythingsuffix"))
        assertFalse(regex.matches("prefixanything"))
    }

    @Test
    fun `should produce case-insensitive regex from StrVal toRegex`() {
        val regex = StrVal("Hello").toRegex(doCaseIgnore = true)
        assertTrue(regex.matches("hello"))
        assertTrue(regex.matches("HELLO"))
    }

    // --- Unsure.toRegex ---

    @Test
    fun `should match any string for Unsure ANY toRegex`() {
        assertTrue("anything".matches(Unsure.ANY.toRegex()))
    }

    @Test
    fun `should match any string for Unsure STR toRegex`() {
        assertTrue("anything".matches(Unsure.STR.toRegex()))
    }

    @Test
    fun `should match digits for Unsure NUM toRegex`() {
        assertTrue("123".matches(Unsure.NUM.toRegex()))
        assertFalse("abc".matches(Unsure.NUM.toRegex()))
    }

    @Test
    fun `should match signed decimal and exponent numerals for Unsure NUM toRegex`() {
        val regex = Unsure.NUM.toRegex()
        assertTrue("-5".matches(regex))
        assertTrue("3.14".matches(regex))
        assertTrue("1.0E10".matches(regex))
        assertFalse("1.".matches(regex))
        assertFalse("v2".matches(regex))
    }

    @Test
    fun `should match true or false for Unsure BOOL toRegex`() {
        assertTrue("true".matches(Unsure.BOOL.toRegex()))
        assertTrue("false".matches(Unsure.BOOL.toRegex()))
        assertFalse("other".matches(Unsure.BOOL.toRegex()))
    }

    @Test
    fun `should produce case-insensitive regex from Unsure toRegex`() {
        val regex = Unsure.BOOL.toRegex(doCaseIgnore = true)
        assertTrue("TRUE".matches(regex))
        assertTrue("False".matches(regex))
    }
}
