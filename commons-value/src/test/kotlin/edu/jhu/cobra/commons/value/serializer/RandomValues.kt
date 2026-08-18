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

// Fixed seed so each call site gets a deterministic, order-independent sequence.
const val SEED = 20260804L

inline fun <reified T : IValue> random(rng: Random = Random(SEED)): T =
    when (T::class) {
        NullVal::class -> randomIValue(0, rng) as T
        StrVal::class -> randomIValue(1, rng) as T
        BoolVal::class -> randomIValue(2, rng) as T
        Unsure::class -> randomIValue(3, rng) as T
        ListVal::class -> randomIValue(4, rng) as T
        SetVal::class -> randomIValue(5, rng) as T
        MapVal::class -> randomIValue(6, rng) as T
        RangeVal::class -> randomIValue(7, rng) as T
        IntVal::class -> randomIValue(8, rng) as T
        FloatVal::class -> randomIValue(9, rng) as T
        else -> randomIValue(rng = rng) as T
    }

fun randomIValue(
    typeNum: Int = -1,
    rng: Random = Random(SEED),
): IValue =
    when (typeNum) {
        0 -> NullVal
        1 -> StrVal(randomString(5, 30, rng))
        2 -> BoolVal(rng.nextBoolean())
        3 -> Unsure.entries[rng.nextInt(0, 4)]
        4 -> randomList(rng.nextInt(1, 10), rng).listVal
        5 -> randomList(rng.nextInt(1, 10), rng).setVal
        6 -> randomMap(rng.nextInt(1, 10), rng).mapVal
        7 -> RangeVal(rng.nextLong(), rng.nextLong())
        8 -> IntVal(rng.nextLong())
        9 -> FloatVal(rng.nextDouble())
        else -> randomIValue(rng.nextInt(0, 10), rng)
    }

private val PRIMITIVE_INDICES = intArrayOf(0, 1, 2, 3, 8, 9)

private fun randomPrimitiveIndex(rng: Random): Int = PRIMITIVE_INDICES[rng.nextInt(PRIMITIVE_INDICES.size)]

private fun randomMap(
    size: Int,
    rng: Random,
): Map<String, IValue> = (1..size).associate { randomString(3, 15, rng) to randomIValue(randomPrimitiveIndex(rng), rng) }

private fun randomList(
    size: Int,
    rng: Random,
): List<IValue> = List(size) { randomIValue(randomPrimitiveIndex(rng), rng) }

private fun randomString(
    minLength: Int,
    maxLength: Int,
    rng: Random,
): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val length = rng.nextInt(minLength, maxLength + 1)
    return (1..length).asSequence().map { chars.random(rng) }.joinToString("")
}
