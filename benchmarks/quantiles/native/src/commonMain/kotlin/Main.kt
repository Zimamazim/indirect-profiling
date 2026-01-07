package org.jetbrains.stdlibprofiling

import kotlin.concurrent.Volatile
import kotlinx.benchmark.Blackhole
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.DurationUnit
import platform.posix.freopen
import platform.posix.stdout

class SubstringBenchmark {

    @Volatile
    private var big = "abc".repeat(100000)

    fun big_some_4000_end() = big.substring(200_000 - 4000, 200_000)

}
@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    var benchmark = SubstringBenchmark()
    var blackhole = Blackhole()
    val measurements = 100000
    val times = ArrayList<Duration>(measurements)
    repeat(measurements) {
        times.add(measureTime {
            repeat(1000) {
                blackhole.consume(benchmark.big_some_4000_end())
            }
        })
    }

    freopen("result.csv", "w", stdout)
    times
        .map { it.toDouble(DurationUnit.SECONDS) }
        .joinToString(separator = ",") { it.toString() }
        .let { println(it) }
}