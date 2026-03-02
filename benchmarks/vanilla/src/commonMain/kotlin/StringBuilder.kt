package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Param
import kotlinx.benchmark.Setup

@State(Scope.Benchmark)
class StringBuilderBenchmark {

    private var small = "abc"
    private var medium = small.repeat(100)
    private var big = small.repeat(1000)

    @Benchmark fun appendSmall() = StringBuilder().apply { repeat(1_000_000) { append(small) } }
    @Benchmark fun appendMedium() = StringBuilder().apply { repeat(10_000) { append(medium) } }
    @Benchmark fun appendBig() = StringBuilder().apply { repeat(1_000) { append(big) } }
    @Benchmark fun control() = StringBuilder()
}
