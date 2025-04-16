package edu.jhu.cobra.commons.value.primitive

import edu.jhu.cobra.commons.value.*
import java.io.File
import java.math.BigInteger
import java.text.ParseException
import kotlin.io.path.Path
import kotlin.test.*

class PrimitiveUtilsTest {

    @Test
    fun testIsInLongRange() {
        assertTrue(Long.MAX_VALUE.isInLongRange)
        assertTrue(Long.MIN_VALUE.isInLongRange)
        assertTrue(0L.isInLongRange)
        assertTrue(BigInteger.valueOf(Long.MAX_VALUE).isInLongRange)
        assertFalse(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE).isInLongRange)
    }

    @Test
    fun testIsInIntRange() {
        assertTrue(Int.MAX_VALUE.isInIntRange)
        assertTrue(Int.MIN_VALUE.isInIntRange)
        assertTrue(0.isInIntRange)
        assertFalse(Long.MAX_VALUE.isInIntRange)
    }

    @Test
    fun testIsInShortRange() {
        assertTrue(Short.MAX_VALUE.isInShortRange)
        assertTrue(Short.MIN_VALUE.isInShortRange)
        assertTrue(0.isInShortRange)
        assertFalse(Int.MAX_VALUE.isInShortRange)
    }

    @Test
    fun testIsInByteRange() {
        assertTrue(Byte.MAX_VALUE.isInByteRange)
        assertTrue(Byte.MIN_VALUE.isInByteRange)
        assertTrue(0.isInByteRange)
        assertFalse(Short.MAX_VALUE.isInByteRange)
    }

    @Test
    fun testNumVal() {
        assertEquals(NumVal(42), 42.numVal)
        assertEquals(NumVal(3.14), 3.14.numVal)
        assertEquals(NumVal(Long.MAX_VALUE), Long.MAX_VALUE.numVal)
    }

    @Test
    fun testStrVal() {
        assertEquals(StrVal("test"), "test".strVal)
        assertEquals(StrVal(""), "".strVal)
    }

    @Test
    fun testBoolVal() {
        assertEquals(BoolVal.T, true.boolVal)
        assertEquals(BoolVal.F, false.boolVal)
    }

    @Test
    fun testPrimitiveVal() {
        assertEquals(NullVal, null.primitiveVal)
        assertEquals(NumVal(42), 42.primitiveVal)
        assertEquals(StrVal("test"), "test".primitiveVal)
        assertEquals(BoolVal.T, true.primitiveVal)

        val primitiveVal = NumVal(42)
        assertEquals(primitiveVal, primitiveVal.primitiveVal)
    }

    @Test
    fun testPrimitiveValInvalidType() {
        assertFailsWith<IllegalArgumentException> {
            Object().primitiveVal
        }
    }

    @Test
    fun testUnsureToRegex() {
        assertTrue("anything".matches(Unsure.ANY.toRegex()))
        assertTrue("123".matches(Unsure.NUM.toRegex()))
        assertFalse("abc".matches(Unsure.NUM.toRegex()))
        assertTrue("true".matches(Unsure.BOOL.toRegex()))
        assertTrue("false".matches(Unsure.BOOL.toRegex()))
        assertFalse("other".matches(Unsure.BOOL.toRegex()))
    }

    @Test
    fun testUnsureToRegexWithCaseIgnore() {
        val regex = Unsure.BOOL.toRegex(true)
        assertTrue("TRUE".matches(regex))
        assertTrue("False".matches(regex))
        assertFalse("other".matches(regex))
    }

    @Test
    fun testNumValComparison() {
        assertTrue(NumVal(1) < NumVal(2))
        assertTrue(NumVal(2) > NumVal(1))
        assertEquals(0, NumVal(1).compareTo(NumVal(1)))
        assertTrue(NumVal(1.5) > NumVal(1))
    }

    @Test
    fun testStrValComparison() {
        assertTrue(StrVal("a") < StrVal("b"))
        assertTrue(StrVal("b") > StrVal("a"))
        assertEquals(0, StrVal("a").compareTo(StrVal("a")))
    }

    @Test
    fun testBoolValComparison() {
        assertTrue(BoolVal.F < BoolVal.T)
        assertTrue(BoolVal.T > BoolVal.F)
        assertEquals(0, BoolVal.T.compareTo(BoolVal.T))
    }

    @Test
    fun testNullValComparison() {
        assertEquals(0, NullVal.compareTo(NullVal))
    }

    @Test
    fun testInvalidComparison() {
        assertFailsWith<IllegalArgumentException> {
            NumVal(1).compareTo(StrVal("1"))
        }
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
        val basic = (StrVal("Hello" + Unsure.STR.core)).toRegex()
        assertTrue(basic.matches("Hello World"))
        assertFalse(basic.matches("Hi World"))

        // Test special character escaping
        val special = StrVal("Hello.World").toRegex()
        assertTrue(special.matches("Hello.World"))
        assertFalse(special.matches("HelloAWorld"))

        // Test COBRA patterns
        val anyPattern = Unsure.STR.toRegex()
        assertTrue(anyPattern.matches("anything"))

        val numPattern = Unsure.NUM.toRegex()
        assertTrue(numPattern.matches("123"))
        assertFalse(numPattern.matches("abc"))

        val boolPattern = Unsure.BOOL.toRegex()
        assertTrue(boolPattern.matches("true"))
        assertTrue(boolPattern.matches("false"))
        assertFalse(boolPattern.matches("other"))

        // Test case sensitivity
        val caseInsensitive = StrVal("Hello").toRegex(doCaseIgnore = true)
        assertTrue(caseInsensitive.matches("HELLO"))
        assertTrue(caseInsensitive.matches("hello"))
    }
} 