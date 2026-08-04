package edu.jhu.cobra.commons.value

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Black-box tests for [ICollectionVal] sealed interface derived from design-collection.md.
 *
 * - `should recognize ListVal as ICollectionVal`
 * - `should recognize SetVal as ICollectionVal`
 * - `should recognize MapVal as ICollectionVal`
 * - `should recognize RangeVal as ICollectionVal`
 * - `should recognize all four types as IValue`
 * - `should exhaustively match all subtypes in when expression`
 */
internal class ICollectionValTest {
    @Test
    fun `should recognize ListVal as ICollectionVal`() {
        val value: ICollectionVal = ListVal(StrVal("a"))
        assertTrue(value is ListVal)
    }

    @Test
    fun `should recognize SetVal as ICollectionVal`() {
        val value: ICollectionVal = SetVal(IntVal(1L))
        assertTrue(value is SetVal)
    }

    @Test
    fun `should recognize MapVal as ICollectionVal`() {
        val value: ICollectionVal = MapVal("k" to StrVal("v"))
        assertTrue(value is MapVal)
    }

    @Test
    fun `should recognize RangeVal as ICollectionVal`() {
        val value: ICollectionVal = RangeVal(1, 10)
        assertTrue(value is RangeVal)
    }

    @Test
    fun `should recognize all four types as IValue`() {
        val values: List<IValue> =
            listOf(
                ListVal(),
                SetVal(),
                MapVal(),
                RangeVal(0, 0),
            )
        values.forEach { assertTrue(it is ICollectionVal) }
        values.forEach { assertTrue(it is IValue) }
    }

    @Test
    fun `should exhaustively match all subtypes in when expression`() {
        val cases: List<ICollectionVal> =
            listOf(
                ListVal(StrVal("a")),
                SetVal(IntVal(1L)),
                MapVal("k" to StrVal("v")),
                RangeVal(1, 5),
            )
        val labels =
            cases.map { value ->
                when (value) {
                    is ListVal -> "list"
                    is SetVal -> "set"
                    is MapVal -> "map"
                    is RangeVal -> "range"
                }
            }
        assertTrue(labels.contains("list"))
        assertTrue(labels.contains("set"))
        assertTrue(labels.contains("map"))
        assertTrue(labels.contains("range"))
    }
}
