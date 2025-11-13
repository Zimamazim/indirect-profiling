package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class StringFirstBenchmark {

    private var small = "abc"
    private var big = "a".repeat(100000) + "b".repeat(100000) + "c".repeat(100000)

    @Benchmark
    fun small_noop() = small
    @Benchmark
    fun big_noop() = big
    @Benchmark
    fun small() = small.first()
    @Benchmark
    fun big() = big.first()
    @Benchmark
    fun small_predicate_begin() = small.first { it == 'a' }
    @Benchmark
    fun big_predicate_begin() = big.first { it == 'a' }
    @Benchmark
    fun small_predicate_iter() = small.first { it == 'c' }
    @Benchmark
    fun big_predicate_iter() = big.first { it == 'c' }
}
