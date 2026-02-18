package org.jetbrains.stdlibprofiling

import kotlin.concurrent.Volatile
import kotlinx.benchmark.Blackhole
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.DurationUnit
import platform.posix.freopen
import platform.posix.stdout
import kotlin.native.runtime.GC

class SubstringBenchmark {

    @Volatile
    private var small = "abc"
    @Volatile
    private var big = "abc".repeat(100000)

    fun small_all() = small.substring(0, 3)
    fun big_all() = big.substring(0, 300_000)

    fun small_none() = small.substring(2, 2)
    fun big_none() = big.substring(200, 200)
    fun big_none_end() = big.substring(200_000, 200_000)

    fun small_some() = small.substring(1, 2)
    fun big_some_4000() = big.substring(5000 - 4000, 5000)
    fun big_some_4000_end() = big.substring(200_000 - 4000, 200_000)
    fun big_some_40000() = big.substring(50_000 - 40000, 50_000)
    fun big_some_40000_end() = big.substring(200_000 - 40000, 200_000)
    fun big_some_100000() = big.substring(150_000, 250_000)

}


@kotlin.native.runtime.NativeRuntimeApi
@kotlin.ExperimentalStdlibApi
@OptIn(ExperimentalForeignApi::class)
fun measure(method: () -> String, name: String, iterations: Int = 100_000, warmup: Int = 1_000_000, cycles: Int = 100) {
    val params = mapOf(
        "name" to name,
        "iterations" to iterations,
        "warmup" to warmup,
        "cycles" to cycles,
        "GCprof" to true,
    )
    var epoch: Long? = null
    val blackhole = Blackhole()
    val times = ArrayList<Double?>(iterations)

    repeat(warmup) { blackhole.consume(method()) }
    repeat(iterations) {
        times.add(measureTime {
            repeat(cycles) {
                blackhole.consume(method())
            }
        }.toDouble(DurationUnit.SECONDS))
        epoch = GC.lastGCInfo?.epoch.also {
            if (it != epoch) times.add(null)
        }
    }
    freopen(
        params.toList().joinToString(separator = ",", prefix = "experiments/", postfix = ".csv") { (k, v) -> "$k=$v" },
        "w",
        stdout
    )
    times
        .joinToString(separator = ",") { it.toString() }
        .let { println(it) }
}

@kotlin.native.runtime.NativeRuntimeApi
@kotlin.ExperimentalStdlibApi
fun main(args: Array<String>) {
    val benchmark = SubstringBenchmark()

    //measure(benchmark::small_all, "small_all")
    //measure(benchmark::big_all, "big_all")

    //measure(benchmark::small_none, "small_none")
    //measure(benchmark::big_none, "big_none")
    //measure(benchmark::big_none_end, "big_none_end")

    //measure(benchmark::small_some, "small_some")
    measure(benchmark::big_some_4000, "big_some_4000")
    //measure(benchmark::big_some_4000_end, "big_some_4000_end")
    measure(benchmark::big_some_40000, "big_some_40000", cycles = 10)
    //measure(benchmark::big_some_40000_end, "big_some_40000_end", cycles = 10)
    //measure(benchmark::big_some_100000, "big_some_100000", cycles = 10)
}
