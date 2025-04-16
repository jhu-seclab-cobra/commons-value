package edu.jhu.cobra.commons.value.collection

import edu.jhu.cobra.commons.value.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ICollectionValTest {
    @Test
    fun testListValAsICollectionVal() {
        val listVal: ICollectionVal = ListVal(StrVal("Item1"), StrVal("Item2"))
        assertTrue(listVal is ListVal)
        assertEquals(2, listVal.core.size)
    }

    @Test
    fun testSetValAsICollectionVal() {
        val setVal: ICollectionVal = SetVal(NumVal(1), NumVal(2))
        assertTrue(setVal is SetVal)
        assertEquals(2, setVal.core.size)
    }

    @Test
    fun testMapValAsICollectionVal() {
        val mapVal: ICollectionVal = MapVal("key1" to StrVal("value1"), "key2" to NumVal(42))
        assertTrue(mapVal is MapVal)
        assertEquals(2, mapVal.core.size)
    }

    @Test
    fun testRangeValAsICollectionVal() {
        val rangeVal: ICollectionVal = RangeVal(1, 10)
        assertTrue(rangeVal is RangeVal)
        assertEquals(2, rangeVal.core.size)
    }

    @Test
    fun testCollectionValPolymorphism() {
        val collections: List<ICollectionVal> = listOf(
            ListVal(StrVal("Item1")),
            SetVal(NumVal(1)),
            MapVal("key" to StrVal("value")),
            RangeVal(1, 10)
        )

        assertEquals(4, collections.size)
        assertTrue(collections[0] is ListVal)
        assertTrue(collections[1] is SetVal)
        assertTrue(collections[2] is MapVal)
        assertTrue(collections[3] is RangeVal)
    }

    @Test
    fun testCollectionValCoreProperty() {
        val listVal: ICollectionVal = ListVal(StrVal("Item1"))
        val setVal: ICollectionVal = SetVal(NumVal(1))
        val mapVal: ICollectionVal = MapVal("key" to StrVal("value"))
        val rangeVal: ICollectionVal = RangeVal(1, 10)

        assertTrue(listVal.core is ArrayList<*>)
        assertTrue(setVal.core is HashSet<*>)
        assertTrue(mapVal.core is HashMap<*, *>)
        assertTrue(rangeVal.core is ArrayList<*>)
    }

    @Test
    fun testCollectionValToString() {
        val listVal: ICollectionVal = ListVal(StrVal("Item1"))
        val setVal: ICollectionVal = SetVal(NumVal(1))
        val mapVal: ICollectionVal = MapVal("key" to StrVal("value"))
        val rangeVal: ICollectionVal = RangeVal(1, 10)

        assertTrue(listVal.toString().startsWith("["))
        assertTrue(setVal.toString().startsWith("{"))
        assertTrue(mapVal.toString().startsWith("{"))
        assertTrue(rangeVal.toString().contains(":"))
    }

    @Test
    fun testCollectionValInheritance() {
        val listVal: ICollectionVal = ListVal(StrVal("Item1"))
        val setVal: ICollectionVal = SetVal(NumVal(1))
        val mapVal: ICollectionVal = MapVal("key" to StrVal("value"))
        val rangeVal: ICollectionVal = RangeVal(1, 10)

        assertTrue(listVal is IValue)
        assertTrue(setVal is IValue)
        assertTrue(mapVal is IValue)
        assertTrue(rangeVal is IValue)
    }
} 