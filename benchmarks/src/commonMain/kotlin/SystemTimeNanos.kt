package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlinx.benchmark.*

expect fun nanos(): Long

@State(Scope.Benchmark)
class SystemTimeNanosBenchmark {
    @Benchmark
    fun read() = nanos()
}