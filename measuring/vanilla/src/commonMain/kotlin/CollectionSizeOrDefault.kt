package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int {
    return if (this is Collection<*>) this.size else default
}

class MyIterable<out T>() : Iterable<T> {
    override fun iterator(): Iterator<T> = emptyList<T>().iterator()
}

@State(Scope.Benchmark)
class CollectionSizeOrDefaultBenchmark {

    private var nonCollection: Iterable<Int> = MyIterable()
    private var collection: Iterable<Int> = listOf(1, 2, 3)
    private var list: List<Int> = listOf(1, 2, 3)
    @Benchmark
    fun nonCollection() = nonCollection.collectionSizeOrDefault(10)
    @Benchmark
    fun collection() = collection.collectionSizeOrDefault(10)
    @Benchmark
    fun list() = list.collectionSizeOrDefault(10)
}
