package org.jetbrains.kotlinx.stdlibbenchmarks

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

/*
Martin.Zimen@NVC00844:/home/Martin.Zimen/IdeaProjects/stdlib-benchmarks$ ./gradlew benchmark
Starting a Gradle Daemon, 1 incompatible and 8 stopped Daemons could not be reused, use --status for details

> Task :jvmBenchmarkGenerate
Analyzing 0 files from /home/Martin.Zimen/IdeaProjects/stdlib-benchmarks/build/processedResources/jvm/main
Analyzing 2 files from /home/Martin.Zimen/IdeaProjects/stdlib-benchmarks/build/classes/kotlin/jvm/main
Writing out Java source to /home/Martin.Zimen/IdeaProjects/stdlib-benchmarks/build/benchmarks/jvm/sources and resources to /home/Martin.Zimen/IdeaProjects/stdlib-benchmarks/build/benchmarks/jvm/resources

> Task :jvmBenchmark
Running 'main' benchmarks for 'jvm'

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.control

Warm-up 1: 4662509640.322 ops/s
Warm-up 2: 4626045637.970 ops/s
Warm-up 3: 2443078130.847 ops/s
Warm-up 4: 2460942211.518 ops/s
Warm-up 5: 2362675505.388 ops/s
Iteration 1: 2472812619.427 ops/s
Iteration 2: 2469481061.000 ops/s
Iteration 3: 2468775037.005 ops/s
Iteration 4: 2460771819.591 ops/s
Iteration 5: 2464845375.259 ops/s

  Success: 2467337182.456 ±(99.9%) 17850252.054 ops/s [Average]
  (min, avg, max) = (2460771819.591, 2467337182.456, 2472812619.427), stdev = 4635653.773
  CI (99.9%): [2449486930.402, 2485187434.510] (assumes normal distribution)

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.emptyCollectionCtor

Warm-up 1: 318642723.126 ops/s
Warm-up 2: 344978800.714 ops/s
Warm-up 3: 399713125.864 ops/s
Warm-up 4: 398699847.651 ops/s
Warm-up 5: 399058564.634 ops/s
Iteration 1: 398661994.783 ops/s
Iteration 2: 399011877.833 ops/s
Iteration 3: 396459209.906 ops/s
Iteration 4: 394207802.360 ops/s
Iteration 5: 389198680.666 ops/s

  Success: 395507913.110 ±(99.9%) 15477920.318 ops/s [Average]
  (min, avg, max) = (389198680.666, 395507913.110, 399011877.833), stdev = 4019566.754
  CI (99.9%): [380029992.791, 410985833.428] (assumes normal distribution)

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.emptyCollectionCtorWrong

Warm-up 1: 317328003.761 ops/s
Warm-up 2: 343870411.388 ops/s
Warm-up 3: 398572888.683 ops/s
Warm-up 4: 383238310.192 ops/s
Warm-up 5: 398604068.486 ops/s
Iteration 1: 393746586.740 ops/s
Iteration 2: 383941054.321 ops/s
Iteration 3: 385713128.910 ops/s
Iteration 4: 395832781.071 ops/s
Iteration 5: 393001975.382 ops/s

  Success: 390447105.285 ±(99.9%) 20299018.740 ops/s [Average]
  (min, avg, max) = (383941054.321, 390447105.285, 395832781.071), stdev = 5271590.704
  CI (99.9%): [370148086.545, 410746124.024] (assumes normal distribution)


jvm summary:
Benchmark                                                  Mode  Cnt           Score          Error  Units
AbstractCollectionCtorBenchmark.control                   thrpt    5  2467337182.456 ± 17850252.054  ops/s
AbstractCollectionCtorBenchmark.emptyCollectionCtor       thrpt    5   395507913.110 ± 15477920.318  ops/s
AbstractCollectionCtorBenchmark.emptyCollectionCtorWrong  thrpt    5   390447105.285 ± 20299018.740  ops/s


> Task :linuxX64Benchmark
Running 'main' benchmarks for 'linuxX64'

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.control
Warm-up #0: 35,890,843 ops/sec
Warm-up #1: 35,600,062 ops/sec
Warm-up #2: 35,936,853 ops/sec
Warm-up #3: 35,692,547 ops/sec
Warm-up #4: 34,185,830 ops/sec
Iteration #0: 1,125,788,028 ops/sec
Iteration #1: 1,048,084,587 ops/sec
Iteration #2: 1,124,170,309 ops/sec
Iteration #3: 1,074,401,425 ops/sec
Iteration #4: 1,152,448,663 ops/sec
  Success:   ~ 1,104,978,603 ops/sec ±3.4%

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.emptyCollectionCtorWrong
Warm-up #0: 31,566,508 ops/sec
Warm-up #1: 32,079,659 ops/sec
Warm-up #2: 31,125,707 ops/sec
Warm-up #3: 31,021,914 ops/sec
Warm-up #4: 31,635,137 ops/sec
Iteration #0: 288,488,236 ops/sec
Iteration #1: 290,334,279 ops/sec
Iteration #2: 292,349,672 ops/sec
Iteration #3: 294,026,327 ops/sec
Iteration #4: 296,927,703 ops/sec
  Success:   ~ 292,425,244 ops/sec ±0.98%

… org.jetbrains.kotlinx.stdlibbenchmarks.AbstractCollectionCtorBenchmark.emptyCollectionCtor
Warm-up #0: 31,070,393 ops/sec
Warm-up #1: 31,060,524 ops/sec
Warm-up #2: 30,941,792 ops/sec
Warm-up #3: 30,945,463 ops/sec
Warm-up #4: 30,248,140 ops/sec
Iteration #0: 191,404,577 ops/sec
Iteration #1: 190,778,720 ops/sec
Iteration #2: 191,241,812 ops/sec
Iteration #3: 191,353,597 ops/sec
Iteration #4: 185,196,534 ops/sec
  Success:   ~ 189,995,048 ops/sec ±1.2%

linuxX64 summary:
Benchmark                                                  Mode  Cnt           Score          Error    Units
AbstractCollectionCtorBenchmark.control                   thrpt    5  1104978603.117 ± 37257154.421  ops/sec
AbstractCollectionCtorBenchmark.emptyCollectionCtorWrong  thrpt    5   292425244.017 ±  2864333.105  ops/sec
AbstractCollectionCtorBenchmark.emptyCollectionCtor       thrpt    5   189995048.481 ±  2361239.939  ops/sec


 */