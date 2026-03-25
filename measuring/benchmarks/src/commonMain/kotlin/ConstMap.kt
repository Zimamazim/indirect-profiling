package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Param
import kotlinx.benchmark.Setup

@State(Scope.Benchmark)
class ConstMapBenchmark {

    @Param("10", "50", "100", "500", "1000", "5000", "10000", "50000", "100000")
    public var size: Int = -1
    private var mapF = emptyMap<String, Int>()
    private var mapB = emptyMap<String, Int>()

    @Setup
    fun prepare() {
        mapF = buildMap {
            for (i in this@MapBenchmark.size .. 0) {
                put(i.toString(), i)
            }
        }
        mapB = buildMap {
            for (i in this@MapBenchmark.size downTo 0) {
                put(i.toString(), i)
            }
        }
    }

    @Benchmark fun getF2() = mapF["2"]
    @Benchmark fun getF5() = mapF["5"]
    @Benchmark fun getF8() = mapF["8"]
    @Benchmark fun controlF() = mapF
    @Benchmark fun getB2() = mapB["2"]
    @Benchmark fun getB5() = mapB["5"]
    @Benchmark fun getB8() = mapB["8"]
    @Benchmark fun controlB() = mapB
}
