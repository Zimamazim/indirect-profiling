package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.*

expect fun nanos(): Long

@State(Scope.Benchmark)
class SystemTimeNanosBenchmark {
    @Benchmark
    fun read() = nanos()
}
