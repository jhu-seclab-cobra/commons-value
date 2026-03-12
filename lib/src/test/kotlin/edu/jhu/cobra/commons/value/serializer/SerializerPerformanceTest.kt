package edu.jhu.cobra.commons.value.serializer

import edu.jhu.cobra.commons.value.BoolVal
import edu.jhu.cobra.commons.value.ListVal
import edu.jhu.cobra.commons.value.MapVal
import edu.jhu.cobra.commons.value.NumVal
import edu.jhu.cobra.commons.value.SetVal
import edu.jhu.cobra.commons.value.StrVal
import edu.jhu.cobra.commons.value.numVal
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("performance")
class SerializerPerformanceTest {

    private val primitiveDataSet = List(100_000) { randomIValue(it % 5) }
    private val collectionDataSet = List(10_000) { randomIValue(5 + it % 4) }
    private val mixedDataSet = List(100_000) { randomIValue() }
    private val valueCreationCount = 600_000

    private val warmupRuns = 5
    private val measureRuns = 7

    @Test
    fun `ByteArraySerializer primitive data round-trip`() {
        benchmarkSerializer("ByteArray-primitive", DftByteArraySerializerImpl, primitiveDataSet)
    }

    @Test
    fun `ByteArraySerializer mixed data round-trip`() {
        benchmarkSerializer("ByteArray-mixed", DftByteArraySerializerImpl, mixedDataSet)
    }

    @Test
    fun `ByteArraySerializer collection data round-trip`() {
        benchmarkSerializer("ByteArray-collection", DftByteArraySerializerImpl, collectionDataSet)
    }

    @Test
    fun `ByteBufferSerializer primitive data round-trip`() {
        benchmarkSerializer("ByteBuffer-primitive", DftByteBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `ByteBufferSerializer mixed data round-trip`() {
        benchmarkSerializer("ByteBuffer-mixed", DftByteBufferSerializerImpl, mixedDataSet)
    }

    @Test
    fun `ByteBufferSerializer collection data round-trip`() {
        benchmarkSerializer("ByteBuffer-collection", DftByteBufferSerializerImpl, collectionDataSet)
    }

    @Test
    fun `CharBufferSerializer primitive data round-trip`() {
        benchmarkSerializer("CharBuffer-primitive", DftCharBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `CharBufferSerializer mixed data round-trip`() {
        benchmarkSerializer("CharBuffer-mixed", DftCharBufferSerializerImpl, mixedDataSet)
    }

    @Test
    fun `CharBufferSerializer collection data round-trip`() {
        benchmarkSerializer("CharBuffer-collection", DftCharBufferSerializerImpl, collectionDataSet)
    }

    @Test
    fun `ByteArraySerializer memory allocation`() {
        measureMemory("ByteArray-memory", DftByteArraySerializerImpl, primitiveDataSet)
    }

    @Test
    fun `ByteBufferSerializer memory allocation`() {
        measureMemory("ByteBuffer-memory", DftByteBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `CharBufferSerializer memory allocation`() {
        measureMemory("CharBuffer-memory", DftCharBufferSerializerImpl, primitiveDataSet)
    }

    @Test
    fun `collection serialization memory allocation`() {
        listOf(
            "ByteArray-collection-memory" to DftByteArraySerializerImpl,
            "ByteBuffer-collection-memory" to DftByteBufferSerializerImpl,
            "CharBuffer-collection-memory" to DftCharBufferSerializerImpl,
        ).forEach { (label, serializer) -> measureMemory(label, serializer, collectionDataSet) }
    }

    @Test
    fun `serialized size comparison`() {
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
    fun `value creation throughput`() {
        repeat(warmupRuns) {
            repeat(valueCreationCount) { i ->
                NumVal(i)
                StrVal("test$i")
                BoolVal(i % 2 == 0)
                ListVal(NumVal(1), NumVal(2), NumVal(3))
                SetVal(NumVal(1), NumVal(2), NumVal(3))
                MapVal("k" to NumVal(i))
            }
        }
        val times = (1..measureRuns).map { _ ->
            val start = System.nanoTime()
            repeat(valueCreationCount) { i ->
                NumVal(i)
                StrVal("test$i")
                BoolVal(i % 2 == 0)
                ListVal(NumVal(1), NumVal(2), NumVal(3))
                SetVal(NumVal(1), NumVal(2), NumVal(3))
                MapVal("k" to NumVal(i))
            }
            (System.nanoTime() - start) / 1_000_000.0
        }
        val totalOps = valueCreationCount.toLong() * 6
        printStats("ValueCreation", times, totalOps)
    }

    private fun <T : Any> benchmarkSerializer(
        label: String,
        serializer: IValSerializer<T>,
        dataSet: List<edu.jhu.cobra.commons.value.IValue>,
    ) {
        // warmup
        repeat(warmupRuns) {
            dataSet.forEach { value ->
                val s = serializer.serialize(value)
                val d = serializer.deserialize(s)
                assertEquals(value, d)
            }
        }
        // measure serialize
        val serTimes = (1..measureRuns).map { _ ->
            val start = System.nanoTime()
            dataSet.forEach { serializer.serialize(it) }
            (System.nanoTime() - start) / 1_000_000.0
        }
        printStats("$label-serialize", serTimes, dataSet.size.toLong())

        // pre-serialize for deserialization benchmark
        val serialized = dataSet.map { serializer.serialize(it) }
        // measure deserialize
        val deserTimes = (1..measureRuns).map { _ ->
            // rewind ByteBuffers/CharBuffers for re-read
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
        dataSet: List<edu.jhu.cobra.commons.value.IValue>,
    ) {
        // warmup
        dataSet.forEach { serializer.deserialize(serializer.serialize(it)) }

        val runtime = Runtime.getRuntime()
        runtime.gc(); Thread.sleep(50)
        val beforeSer = runtime.totalMemory() - runtime.freeMemory()
        dataSet.forEach { serializer.serialize(it) }
        val afterSer = runtime.totalMemory() - runtime.freeMemory()
        val serBytes = (afterSer - beforeSer).coerceAtLeast(0) / dataSet.size

        val serialized = dataSet.map { serializer.serialize(it) }
        runtime.gc(); Thread.sleep(50)
        val beforeDeser = runtime.totalMemory() - runtime.freeMemory()
        serialized.forEach { serializer.deserialize(it) }
        val afterDeser = runtime.totalMemory() - runtime.freeMemory()
        val deserBytes = (afterDeser - beforeDeser).coerceAtLeast(0) / dataSet.size

        println("[$label] serialize=$serBytes bytes/op, deserialize=$deserBytes bytes/op")
    }

    private fun printStats(label: String, timesMs: List<Double>, ops: Long) {
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
