package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.FloatVal
import edu.jhu.cobra.commons.value.IValue
import edu.jhu.cobra.commons.value.IntVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Performance benchmarks for the three [IValSerializer] implementations.
 *
 * - `should round-trip ByteArraySerializer primitive data` — Throughput for primitives via ByteArray.
 * - `should round-trip ByteArraySerializer mixed data` — Throughput for mixed values via ByteArray.
 * - `should round-trip ByteArraySerializer collection data` — Throughput for collections via ByteArray.
 * - `should round-trip ByteBufferSerializer primitive data` — Throughput for primitives via ByteBuffer.
 * - `should round-trip ByteBufferSerializer mixed data` — Throughput for mixed values via ByteBuffer.
 * - `should round-trip ByteBufferSerializer collection data` — Throughput for collections via ByteBuffer.
 * - `should round-trip CharBufferSerializer primitive data` — Throughput for primitives via CharBuffer.
 * - `should round-trip CharBufferSerializer mixed data` — Throughput for mixed values via CharBuffer.
 * - `should round-trip CharBufferSerializer collection data` — Throughput for collections via CharBuffer.
 * - `should measure ByteArraySerializer memory allocation` — Memory per-op for ByteArray primitives.
 * - `should measure ByteBufferSerializer memory allocation` — Memory per-op for ByteBuffer primitives.
 * - `should measure CharBufferSerializer memory allocation` — Memory per-op for CharBuffer primitives.
 * - `should measure collection serialization memory allocation` — Memory per-op for collections across all serializers.
 * - `should compare serialized sizes across serializers` — Output size comparison across all three serializers.
 * - `should measure value creation throughput` — Throughput for creating value type instances.
 */
@Tag("performance")
internal class SerializerPerformanceTest {
    companion object {
        // Indices into randomIValue: 0=Null, 1=Str, 2=Bool, 3=Unsure, 4=List, 5=Set, 6=Map, 7=Range, 8=Int, 9=Float
        private val PRIMITIVE_TYPES = intArrayOf(0, 1, 2, 3, 8, 9)
        private val COLLECTION_TYPES = intArrayOf(4, 5, 6, 7)
    }

    private val primitiveDataSet = List(100_000) { randomIValue(PRIMITIVE_TYPES[it % PRIMITIVE_TYPES.size]) }
    private val collectionDataSet = List(10_000) { randomIValue(COLLECTION_TYPES[it % COLLECTION_TYPES.size]) }
    private val mixedDataSet = List(100_000) { randomIValue() }
    private val valueCreationCount = 600_000

    private val warmupRuns = 5
    private val measureRuns = 7

    @Test
    fun `should round-trip ByteArraySerializer primitive data`() {
        benchmarkSerializer("ByteArray-primitive", DftByteArraySerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should round-trip ByteArraySerializer mixed data`() {
        benchmarkSerializer("ByteArray-mixed", DftByteArraySerializerImpl, mixedDataSet)
    }

    @Test
    fun `should round-trip ByteArraySerializer collection data`() {
        benchmarkSerializer("ByteArray-collection", DftByteArraySerializerImpl, collectionDataSet)
    }

    @Test
    fun `should round-trip ByteBufferSerializer primitive data`() {
        benchmarkSerializer("ByteBuffer-primitive", DftByteBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should round-trip ByteBufferSerializer mixed data`() {
        benchmarkSerializer("ByteBuffer-mixed", DftByteBufferSerializerImpl, mixedDataSet)
    }

    @Test
    fun `should round-trip ByteBufferSerializer collection data`() {
        benchmarkSerializer("ByteBuffer-collection", DftByteBufferSerializerImpl, collectionDataSet)
    }

    @Test
    fun `should round-trip CharBufferSerializer primitive data`() {
        benchmarkSerializer("CharBuffer-primitive", DftCharBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should round-trip CharBufferSerializer mixed data`() {
        benchmarkSerializer("CharBuffer-mixed", DftCharBufferSerializerImpl, mixedDataSet)
    }

    @Test
    fun `should round-trip CharBufferSerializer collection data`() {
        benchmarkSerializer("CharBuffer-collection", DftCharBufferSerializerImpl, collectionDataSet)
    }

    @Test
    fun `should measure ByteArraySerializer memory allocation`() {
        measureMemory("ByteArray-memory", DftByteArraySerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should measure ByteBufferSerializer memory allocation`() {
        measureMemory("ByteBuffer-memory", DftByteBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should measure CharBufferSerializer memory allocation`() {
        measureMemory("CharBuffer-memory", DftCharBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `should measure collection serialization memory allocation`() {
        listOf(
            "ByteArray-collection-memory" to DftByteArraySerializerImpl,
            "ByteBuffer-collection-memory" to DftByteBufferSerializerImpl,
            "CharBuffer-collection-memory" to DftCharBufferSerializerImpl,
        ).forEach { (label, serializer) -> measureMemory(label, serializer, collectionDataSet) }
    }

    @Test
    fun `should compare serialized sizes across serializers`() {
        val sampleData = List(1000) { randomIValue() }
        val baSize = sampleData.sumOf { DftByteArraySerializerImpl.serialize(it).size }
        val bbSize = sampleData.sumOf { DftByteBufferSerializerImpl.serialize(it).limit() }
        val cbSize = sampleData.sumOf { DftCharBufferSerializerImpl.serialize(it).length * 2 }
        println("=== Serialized Size (1000 values) ===")
        println("  ByteArray:  $baSize bytes")
        println("  ByteBuffer: $bbSize bytes")
        println("  CharBuffer: $cbSize bytes (UTF-16)")
    }

    @Test
    fun `should measure value creation throughput`() {
        repeat(warmupRuns) {
            repeat(valueCreationCount) { i ->
                IntVal(i.toLong())
                StrVal("test$i")
                BoolVal(i % 2 == 0)
                ListVal(IntVal(1L), FloatVal(2.0), IntVal(3L))
                SetVal(IntVal(1L), FloatVal(2.0), IntVal(3L))
                MapVal("k" to IntVal(i.toLong()))
            }
        }
        val times =
            (1..measureRuns).map {
                val start = System.nanoTime()
                repeat(valueCreationCount) { i ->
                    IntVal(i.toLong())
                    StrVal("test$i")
                    BoolVal(i % 2 == 0)
                    ListVal(IntVal(1L), FloatVal(2.0), IntVal(3L))
                    SetVal(IntVal(1L), FloatVal(2.0), IntVal(3L))
                    MapVal("k" to IntVal(i.toLong()))
                }
                (System.nanoTime() - start) / 1_000_000.0
            }
        val totalOps = valueCreationCount.toLong() * 6
        printStats("ValueCreation", times, totalOps)
    }

    private fun <T : Any> benchmarkSerializer(
        label: String,
        serializer: IValSerializer<T>,
        dataSet: List<IValue>,
    ) {
        repeat(warmupRuns) {
            dataSet.forEach { value ->
                val s = serializer.serialize(value)
                val d = serializer.deserialize(s)
                assertEquals(value, d)
            }
        }
        val serTimes =
            (1..measureRuns).map {
                val start = System.nanoTime()
                dataSet.forEach { serializer.serialize(it) }
                (System.nanoTime() - start) / 1_000_000.0
            }
        printStats("$label-serialize", serTimes, dataSet.size.toLong())

        val serialized = dataSet.map { serializer.serialize(it) }
        val deserTimes =
            (1..measureRuns).map {
                serialized.forEach { if (it is java.nio.Buffer) it.rewind() }
                val start = System.nanoTime()
                serialized.forEach { serializer.deserialize(it) }
                (System.nanoTime() - start) / 1_000_000.0
            }
        printStats("$label-deserialize", deserTimes, dataSet.size.toLong())
    }

    private fun <T : Any> measureMemory(
        label: String,
        serializer: IValSerializer<T>,
        dataSet: List<IValue>,
    ) {
        dataSet.forEach { serializer.deserialize(serializer.serialize(it)) }

        val runtime = Runtime.getRuntime()
        runtime.gc()
        Thread.sleep(50)
        val beforeSer = runtime.totalMemory() - runtime.freeMemory()
        dataSet.forEach { serializer.serialize(it) }
        val afterSer = runtime.totalMemory() - runtime.freeMemory()
        val serBytes = (afterSer - beforeSer).coerceAtLeast(0) / dataSet.size

        val serialized = dataSet.map { serializer.serialize(it) }
        runtime.gc()
        Thread.sleep(50)
        val beforeDeser = runtime.totalMemory() - runtime.freeMemory()
        serialized.forEach { serializer.deserialize(it) }
        val afterDeser = runtime.totalMemory() - runtime.freeMemory()
        val deserBytes = (afterDeser - beforeDeser).coerceAtLeast(0) / dataSet.size

        println("[$label] serialize=$serBytes bytes/op, deserialize=$deserBytes bytes/op")
    }

    private fun printStats(
        label: String,
        timesMs: List<Double>,
        ops: Long,
    ) {
        val avg = timesMs.average()
        val min = timesMs.min()
        val max = timesMs.max()
        val throughput = (ops / (avg / 1_000.0)).toLong()
        println(
            "[$label] avg=%.2f ms, min=%.2f ms, max=%.2f ms, throughput=%,d ops/s"
                .format(avg, min, max, throughput),
        )
    }
}
