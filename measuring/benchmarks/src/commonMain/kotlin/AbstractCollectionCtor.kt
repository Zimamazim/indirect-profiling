package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.*
import kotlin.collections.AbstractCollection

@State(Scope.Benchmark)
class AbstractCollectionCtorBenchmark {

    private var x = 42
    private val y = 42

    class EmptyCollection<T>(val x: Int) : AbstractCollection<T>() {
        override val size: Int get() = x
        override fun iterator(): Iterator<T> = emptyList<T>().iterator()
    }

    @Benchmark
    fun control() {
    }

    @Benchmark
    fun emptyCollectionCtor() = EmptyCollection<Int>(x)

    @Benchmark
    fun emptyCollectionCtorWrong() = EmptyCollection<Int>(y)
}
