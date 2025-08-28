package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class DropBenchmark {

    private var empty_list = listOf<Byte>()
    private var small_list = listOf<Byte>(42, 24, 43, 4, 5, 11, 99)
    private var big_list = List<Byte>(2_000_000, { (it*it % 253).toByte() })
    private var empty_set = setOf<Byte>()
    private var small_set = small_list.toSet()
    private var big_set = big_list.toSet()

    @Benchmark
    fun empty_list_zero() = empty_list.drop(0)
    @Benchmark
    fun small_list_zero() = small_list.drop(0)
    @Benchmark
    fun big_list_zero() = big_list.drop(0)
    @Benchmark
    fun empty_set_zero() = empty_set.drop(0)
    @Benchmark
    fun small_set_zero() = small_set.drop(0)
    @Benchmark
    fun big_set_zero() = big_set.drop(0)

    @Benchmark
    fun small_list_one() = small_list.drop(1)
    @Benchmark
    fun big_list_one() = big_list.drop(1)
    @Benchmark
    fun small_set_one() = small_set.drop(1)
    @Benchmark
    fun big_set_one() = big_set.drop(1)

    @Benchmark
    fun small_list_three() = small_list.drop(3)
    @Benchmark
    fun big_list_three() = big_list.drop(3)
    @Benchmark
    fun small_set_three() = small_set.drop(3)
    @Benchmark
    fun big_set_three() = big_set.drop(3)

    @Benchmark
    fun big_list_hundred() = big_list.drop(100)
    @Benchmark
    fun big_set_hundred() = big_set.drop(100)
}