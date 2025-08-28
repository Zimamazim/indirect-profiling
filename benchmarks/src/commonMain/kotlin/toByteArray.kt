package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class ToByteArrayBenchmark {

    private var empty_list = listOf<Byte>()
    private var small_list = listOf<Byte>(42, 24, 43, 4, 5, 11, 99)
    private var big_list = List<Byte>(2_000_000, { (it*it % 253).toByte() })
    private var empty_set = setOf<Byte>()
    private var small_set = small_list.toSet()
    private var big_set = big_list.toSet()

    @Benchmark
    fun empty_list() = empty_list.toByteArray()
    @Benchmark
    fun small_list() = small_list.toByteArray()
    @Benchmark
    fun big_list() = big_list.toByteArray()
    @Benchmark
    fun empty_set() = empty_set.toByteArray()
    @Benchmark
    fun small_set() = small_set.toByteArray()
    @Benchmark
    fun big_set() = big_set.toByteArray()
}