package edu.jhu.cobra.commons.value.serializer

import cobra.commons.value.*
import kotlin.random.Random

inline fun <reified T : IValue> random() = when (T::class) {
    NullVal::class -> randomIValue(0) as T
    StrVal::class -> randomIValue(1) as T
    BoolVal::class -> randomIValue(2) as T
    NumVal::class -> randomIValue(3) as T
    Unsure::class -> randomIValue(4) as T
    ListVal::class -> randomIValue(5) as T
    SetVal::class -> randomIValue(6) as T
    MapVal::class -> randomIValue(7) as T
    RangeVal::class -> randomIValue(8) as T
    else -> randomIValue() as T
}

// Function to generate random IValue
fun randomIValue(typeNum: Int = -1): IValue = when (typeNum) {
    0 -> NullVal
    1 -> StrVal(randomString(20, 200)) // String
    2 -> BoolVal(Random.nextBoolean()) // Boolean
    3 -> randomNumber().numVal // Number
    4 -> Unsure.entries[Random.nextInt(0, 4)] // Unsure
    5 -> randomList(Random.nextInt(1, 10)).listVal // List with depth control
    6 -> randomList(Random.nextInt(1, 10)).setVal // Set with depth control
    7 -> randomMap(Random.nextInt(1, 10)).mapVal // Map with depth control
    8 -> RangeVal(randomNumber().toInt(), randomNumber().toInt())
    else -> randomIValue(Random.nextInt(0, 9))
}

private fun randomNumber(): Number = when (Random.nextInt(6)) {
    0 -> Random.nextInt() // Integer
    1 -> Random.nextLong() // Long
    2 -> Random.nextFloat() // Float
    3 -> Random.nextDouble() // Double
    4 -> Random.nextInt().toShort() // Short
    5 -> Random.nextInt().toByte() // Byte
    else -> throw IllegalArgumentException("Unsupported number type")
}

private fun randomMap(size: Int): Map<String, IValue> =
    (1..size).associate { randomString(10, 100) to randomIValue(Random.nextInt(0, 5)) }

private fun randomList(size: Int): List<IValue> = List(size) { randomIValue(Random.nextInt(0, 5)) }

private fun randomString(minLength: Int, maxLength: Int): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val length = Random.nextInt(minLength, maxLength + 1)
    return (1..length).asSequence().map { chars.random() }.joinToString("")
}