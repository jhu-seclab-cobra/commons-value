package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Black-box tests for collection extension functions derived from design-collection.md
 * and CollectionConversions.kt specifications.
 *
 * Collection.listVal:
 * - `should convert mixed collection to ListVal`
 * - `should convert empty collection to empty ListVal`
 * - `should throw IllegalArgumentException for unconvertible element in listVal`
 *
 * Collection.setVal:
 * - `should convert collection to SetVal removing duplicates`
 * - `should convert empty collection to empty SetVal`
 * - `should throw IllegalArgumentException for unconvertible element in setVal`
 *
 * Map.mapVal:
 * - `should convert map to MapVal`
 * - `should convert empty map to empty MapVal`
 * - `should throw IllegalArgumentException for unconvertible value in mapVal`
 *
 * IntRange.rangeVal / LongRange.rangeVal:
 * - `should convert IntRange to RangeVal`
 * - `should convert LongRange to RangeVal`
 *
 * ListVal?.orEmpty:
 * - `should return empty ListVal when null`
 * - `should return same ListVal when non-null`
 *
 * SetVal?.orEmpty:
 * - `should return empty SetVal when null`
 * - `should return same SetVal when non-null`
 *
 * MapVal?.orEmpty:
 * - `should return empty MapVal when null`
 * - `should return same MapVal when non-null`
 *
 * Boundary:
 * - `should convert nested collections to nested IValue`
 */
internal class CollectionConversionsTest {
    // -- Collection.listVal --

    @Test
    fun `should convert mixed collection to ListVal`() {
        val collection = listOf(1, "text", true)
        val result = collection.listVal
        assertEquals(3, result.size)
        assertEquals(IntVal(1L), result[0])
        assertEquals(StrVal("text"), result[1])
        assertEquals(BoolVal.T, result[2])
    }

    @Test
    fun `should convert empty collection to empty ListVal`() {
        val result = emptyList<Any>().listVal
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException for unconvertible element in listVal`() {
        class Custom
        assertFailsWith<IllegalArgumentException> {
            listOf(Custom()).listVal
        }
    }

    // -- Collection.setVal --

    @Test
    fun `should convert collection to SetVal removing duplicates`() {
        val collection = listOf(1, 1, 2, "text")
        val result = collection.setVal
        assertEquals(3, result.size)
        assertTrue(result.contains(IntVal(1L)))
        assertTrue(result.contains(IntVal(2L)))
        assertTrue(result.contains(StrVal("text")))
    }

    @Test
    fun `should convert empty collection to empty SetVal`() {
        val result = emptyList<Any>().setVal
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException for unconvertible element in setVal`() {
        class Custom
        assertFailsWith<IllegalArgumentException> {
            listOf(Custom()).setVal
        }
    }

    // -- Map.mapVal --

    @Test
    fun `should convert map to MapVal`() {
        val map = mapOf("k1" to 42, "k2" to true)
        val result = map.mapVal
        assertEquals(2, result.size)
        assertEquals(IntVal(42L), result["k1"])
        assertEquals(BoolVal.T, result["k2"])
    }

    @Test
    fun `should convert empty map to empty MapVal`() {
        val result = emptyMap<String, Any>().mapVal
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw IllegalArgumentException for unconvertible value in mapVal`() {
        class Custom
        assertFailsWith<IllegalArgumentException> {
            mapOf("k" to Custom()).mapVal
        }
    }

    // -- IntRange.rangeVal --

    @Test
    fun `should convert IntRange to RangeVal`() {
        val range = 1..10
        val result = range.rangeVal
        assertEquals(1, result.first)
        assertEquals(10, result.last)
    }

    @Test
    fun `should convert LongRange to RangeVal`() {
        val range = 1L..Long.MAX_VALUE
        val result = range.rangeVal
        assertEquals(1L, result.first)
        assertEquals(Long.MAX_VALUE, result.last)
    }

    // -- ListVal?.orEmpty --

    @Test
    fun `should return empty ListVal when null`() {
        val nullList: ListVal? = null
        val result = nullList.orEmpty()
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return same ListVal when non-null`() {
        val list = ListVal(StrVal("a"))
        val result = list.orEmpty()
        assertEquals(1, result.size)
        assertEquals(StrVal("a"), result[0])
    }

    // -- SetVal?.orEmpty --

    @Test
    fun `should return empty SetVal when null`() {
        val nullSet: SetVal? = null
        val result = nullSet.orEmpty()
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return same SetVal when non-null`() {
        val set = SetVal(IntVal(1L))
        val result = set.orEmpty()
        assertEquals(1, result.size)
        assertTrue(result.contains(IntVal(1L)))
    }

    // -- MapVal?.orEmpty --

    @Test
    fun `should return empty MapVal when null`() {
        val nullMap: MapVal? = null
        val result = nullMap.orEmpty()
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return same MapVal when non-null`() {
        val map = MapVal("k" to StrVal("v"))
        val result = map.orEmpty()
        assertEquals(1, result.size)
        assertEquals(StrVal("v"), result["k"])
    }

    // -- Boundary --

    @Test
    fun `should convert nested collections to nested IValue`() {
        val nested = listOf(listOf(1, 2), listOf("a", "b"))
        val result = nested.listVal
        assertEquals(2, result.size)
        assertTrue(result[0] is ListVal)
        assertTrue(result[1] is ListVal)
        val inner0 = result[0] as ListVal
        val inner1 = result[1] as ListVal
        assertEquals(IntVal(1L), inner0[0])
        assertEquals(IntVal(2L), inner0[1])
        assertEquals(StrVal("a"), inner1[0])
        assertEquals(StrVal("b"), inner1[1])
    }
}
