package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.*
import java.io.File
import java.math.BigDecimal
import java.text.ParseException
import kotlin.io.path.Path
import kotlin.test.*

class PrimitiveUtilsTest {

    @Test
    fun testIsInLongRange() {
        // Test exact boundaries
        assertTrue(BigDecimal(Long.MAX_VALUE).isInLongRange)
        assertTrue(BigDecimal(Long.MIN_VALUE).isInLongRange)

        // Test values within range
        assertTrue(BigDecimal("0").isInLongRange)
        assertTrue(BigDecimal("9223372036854775807").isInLongRange) // Long.MAX_VALUE
        assertTrue(BigDecimal("-9223372036854775808").isInLongRange) // Long.MIN_VALUE

        // Test values outside range
        assertFalse(BigDecimal("9223372036854775808").isInLongRange) // Long.MAX_VALUE + 1
        assertFalse(BigDecimal("-9223372036854775809").isInLongRange) // Long.MIN_VALUE - 1
    }

    @Test
    fun testIsInIntRange() {
        // Test exact boundaries
        assertTrue(Int.MAX_VALUE.toLong().isInIntRange)
        assertTrue(Int.MIN_VALUE.toLong().isInIntRange)

        // Test values within range
        assertTrue(0L.isInIntRange)
        assertTrue(2147483647L.isInIntRange) // Int.MAX_VALUE
        assertTrue((-2147483648L).isInIntRange) // Int.MIN_VALUE

        // Test values outside range
        assertFalse(2147483648L.isInIntRange) // Int.MAX_VALUE + 1
        assertFalse((-2147483649L).isInIntRange) // Int.MIN_VALUE - 1
    }

    @Test
    fun testIsInShortRange() {
        // Test exact boundaries
        assertTrue(Short.MAX_VALUE.toInt().isInShortRange)
        assertTrue(Short.MIN_VALUE.toInt().isInShortRange)

        // Test values within range
        assertTrue(0.isInShortRange)
        assertTrue(32767.isInShortRange) // Short.MAX_VALUE
        assertTrue((-32768).isInShortRange) // Short.MIN_VALUE

        // Test values outside range
        assertFalse(32768.isInShortRange) // Short.MAX_VALUE + 1
        assertFalse((-32769).isInShortRange) // Short.MIN_VALUE - 1
    }

    @Test
    fun testIsInByteRange() {
        // Test exact boundaries
        assertTrue(Byte.MAX_VALUE.toInt().isInByteRange)
        assertTrue(Byte.MIN_VALUE.toInt().isInByteRange)

        // Test values within range
        assertTrue(0.isInByteRange)
        assertTrue(127.isInByteRange) // Byte.MAX_VALUE
        assertTrue((-128).isInByteRange) // Byte.MIN_VALUE

        // Test values outside range
        assertFalse(128.isInByteRange) // Byte.MAX_VALUE + 1
        assertFalse((-129).isInByteRange) // Byte.MIN_VALUE - 1
    }

    @Test
    fun testNumberToNumValConversion() {
        // Test integer conversion
        val intNum = 42
        assertEquals(42, intNum.numVal.core)

        // Test double conversion
        val doubleNum = 3.14
        assertEquals(3.14, doubleNum.numVal.core)

        // Test different number types
        assertEquals(42L, 42L.numVal.core)
        assertEquals(42.0f, 42.0f.numVal.core)
    }

    @Test
    fun testStringToNumValConversion() {
        // Test integer string
        assertEquals(42, "42".numVal.core)

        // Test decimal string
        assertEquals(3.14, "3.14".numVal.core)

        // Test large number string
        assertEquals(9999999999L, "9999999999".numVal.core)

        // Test negative numbers
        assertEquals(-42, "-42".numVal.core)
        assertEquals(-3.14, "-3.14".numVal.core)
    }

    @Test
    fun testStringToNumValConversionWithInvalidInput() {
        // Test invalid number format
        assertFailsWith<ParseException> {
            "not a number".numVal
        }
    }

    @Test
    fun testStringToStrValConversion() {
        // Test basic string conversion
        val str = "Hello"
        assertEquals("Hello", str.strVal.core)

        // Test empty string
        assertEquals("", "".strVal.core)

        // Test string with special characters
        assertEquals("Hello, World!", "Hello, World!".strVal.core)
    }

    @Test
    fun testCharToStrValConversion() {
        // Test basic character conversion
        val char = 'A'
        assertEquals("A", char.strVal.core)

        // Test special characters
        assertEquals(" ", ' '.strVal.core)
        assertEquals("\n", '\n'.strVal.core)
    }

    @Test
    fun testPathToStrValConversion() {
        // Test path conversion
        val path = Path("/home/user/file.txt")
        assertEquals("/home/user/file.txt", path.strVal.core)

        // Test relative path
        val relativePath = Path("./file.txt")
        assertEquals("./file.txt", relativePath.strVal.core)
    }

    @Test
    fun testFileToStrValConversion() {
        // Test file conversion
        val file = File("/home/user/file.txt")
        assertEquals("/home/user/file.txt", file.strVal.core)

        // Test relative file path
        val relativeFile = File("./file.txt")
        assertEquals("./file.txt", relativeFile.strVal.core)
    }

    @Test
    fun testStringStartsWithStrVal() {
        // Test matching prefix
        val prefix = StrVal("Hello")
        assertTrue("Hello, World!".startsWith(prefix))

        // Test non-matching prefix
        assertFalse("Hi, World!".startsWith(prefix))

        // Test empty string and empty prefix
        assertTrue("Hello".startsWith(StrVal("")))
        assertFalse("".startsWith(StrVal("Hello")))
    }

    @Test
    fun testStrValToRegexConversion() {
        // Test basic pattern
        val basic = StrVal("Hello.*").toRegex()
        assertTrue(basic.matches("Hello World"))
        assertFalse(basic.matches("Hi World"))

        // Test special character escaping
        val special = StrVal("Hello.World").toRegex()
        assertTrue(special.matches("Hello.World"))
        assertFalse(special.matches("HelloAWorld"))

        // Test COBRA patterns
        val anyPattern = StrVal("ANY").toRegex()
        assertTrue(anyPattern.matches("anything"))

        val numPattern = StrVal("NUM").toRegex()
        assertTrue(numPattern.matches("123"))
        assertFalse(numPattern.matches("abc"))

        val boolPattern = StrVal("BOOL").toRegex()
        assertTrue(boolPattern.matches("true"))
        assertTrue(boolPattern.matches("false"))
        assertFalse(boolPattern.matches("other"))

        // Test case sensitivity
        val caseInsensitive = StrVal("Hello").toRegex(doCaseIgnore = true)
        assertTrue(caseInsensitive.matches("HELLO"))
        assertTrue(caseInsensitive.matches("hello"))
    }
} 