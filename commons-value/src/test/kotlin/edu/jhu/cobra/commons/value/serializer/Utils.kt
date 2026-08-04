package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NullVal
import edu.jhu.cobra.commons.value.RangeVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.Unsure
import edu.jhu.cobra.commons.value.listVal
import edu.jhu.cobra.commons.value.mapVal
import edu.jhu.cobra.commons.value.setVal
import kotlin.random.Random

inline fun <reified T : IValue> random(): T =
    when (T::class) {
        NullVal::class -> randomIValue(0) as T
        StrVal::class -> randomIValue(1) as T
        BoolVal::class -> randomIValue(2) as T
        Unsure::class -> randomIValue(3) as T
        ListVal::class -> randomIValue(4) as T
        SetVal::class -> randomIValue(5) as T
        MapVal::class -> randomIValue(6) as T
        RangeVal::class -> randomIValue(7) as T
        IntVal::class -> randomIValue(8) as T
        FloatVal::class -> randomIValue(9) as T
        else -> randomIValue() as T
    }

fun randomIValue(typeNum: Int = -1): IValue =
    when (typeNum) {
        0 -> NullVal
        1 -> StrVal(randomString(5, 30))
        2 -> BoolVal(Random.nextBoolean())
        3 -> Unsure.entries[Random.nextInt(0, 4)]
        4 -> randomList(Random.nextInt(1, 10)).listVal
        5 -> randomList(Random.nextInt(1, 10)).setVal
        6 -> randomMap(Random.nextInt(1, 10)).mapVal
        7 -> RangeVal(Random.nextLong(), Random.nextLong())
        8 -> IntVal(Random.nextLong())
        9 -> FloatVal(Random.nextDouble())
        else -> randomIValue(Random.nextInt(0, 10))
    }

private val PRIMITIVE_INDICES = intArrayOf(0, 1, 2, 3, 8, 9)

private fun randomPrimitiveIndex(): Int = PRIMITIVE_INDICES[Random.nextInt(PRIMITIVE_INDICES.size)]

private fun randomMap(size: Int): Map<String, IValue> = (1..size).associate { randomString(3, 15) to randomIValue(randomPrimitiveIndex()) }

private fun randomList(size: Int): List<IValue> = List(size) { randomIValue(randomPrimitiveIndex()) }

private fun randomString(
    minLength: Int,
    maxLength: Int,
): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val length = Random.nextInt(minLength, maxLength + 1)
    return (1..length).asSequence().map { chars.random() }.joinToString("")
}
