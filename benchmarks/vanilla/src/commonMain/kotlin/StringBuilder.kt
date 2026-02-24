package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Param
import kotlinx.benchmark.Setup

@State(Scope.Benchmark)
class StringBuilderBenchmark {

    private val builder = StringBuilder()
    private var small = "abc"
    private var medium = small.repeat(100)
    private var big = small.repeat(1000)

    @Benchmark fun appendSmall() = builder.append(small)
    @Benchmark fun appendMedium() = builder.append(medium)
    @Benchmark fun appendBig() = builder.append(big)
}
