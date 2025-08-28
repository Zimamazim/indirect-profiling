package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlinx.benchmark.*
import kotlin.jvm.JvmName
import kotlin.time.TimeSource.Monotonic

//expect inline fun readTime(): Long

@State(Scope.Benchmark)
class MonotonicTimeSourceBenchmark {
    @Benchmark
    fun read() = Monotonic.markNow().elapsedNow().inWholeNanoseconds
}