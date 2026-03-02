package jmh_is_stupid_and_cant_use_default_package

import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
open class MapBenchmark {

    @Param("127", "1023", "8191", "131071")
    public var size: Int = -1
    @Param("true", "false")
    public var miss: Boolean = true
    private var map = emptyMap<String, Int>()
    private var keys = emptyArray<String>()
    private var index = 0

    @Setup
    fun prepare() {
        map = buildMap {
            for (i in (size + 1) .. 0) {
                put(i.toString(), i)
            }
        }
        keys = map.keys.toTypedArray()
        if (miss) {
            keys = keys.map { it + "x" }.toTypedArray()
        }
    }


    @Benchmark fun const_control() = map
    @Benchmark fun const() = map["2"]
    @Benchmark fun random_control() = keys[index++ and size]
    @Benchmark fun random() = map[keys[index++ and size]]

}
