package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class StringSubstringBenchmark {

    private var small = "abc"
    private var big = "abc".repeat(100000)

    @Benchmark
    fun small_all() = small.substring(0, 3)
    @Benchmark
    fun big_all() = big.substring(0, 300_000)

    @Benchmark
    fun small_none() = small.substring(2, 2)
    @Benchmark
    fun big_none() = big.substring(200, 200)
    @Benchmark
    fun big_none_end() = big.substring(200_000, 200_000)

    @Benchmark
    fun small_some() = small.substring(1, 2)
    @Benchmark
    fun big_some_4000() = big.substring(5000 - 4000, 5000)
    @Benchmark
    fun big_some_4000_end() = big.substring(200_000 - 4000, 200_000)
    @Benchmark
    fun big_some_40000() = big.substring(50_000 - 40000, 50_000)
    @Benchmark
    fun big_some_40000_end() = big.substring(200_000 - 40000, 200_000)
    @Benchmark
    fun big_some_100000() = big.substring(150_000, 250_000)

    @Benchmark
    fun small_control() = small
    @Benchmark
    fun big_control() = big
}
