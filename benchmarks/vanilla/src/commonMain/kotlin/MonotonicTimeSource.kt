package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.*
import kotlin.time.TimeSource.Monotonic

@State(Scope.Benchmark)
class MonotonicTimeSourceBenchmark {
    @Benchmark
    fun read() = Monotonic.markNow().elapsedNow().inWholeNanoseconds
}
