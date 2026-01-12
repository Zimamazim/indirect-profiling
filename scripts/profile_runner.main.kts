@file:DependsOn("../processing/build/classes/kotlin/main")

import profilelib.walkPath
import java.io.File
import kotlin.time.measureTime

val jvmRootPath = "../targets/serialization-twitterBM/jvm/"
val nativeRootPath = "../targets/serialization-twitterBM/native/"
var outputStorage = "../jfrStorage/serialization-twitterBM/higher_cycles"
ProcessBuilder("mkdir", "-p", outputStorage).start().waitFor()

fun measureNative(cycles: Int, sampleInterval: Int, iteration: Int) {
    val file = "$outputStorage/platform=native,cycles=$cycles,sampleInterval=$sampleInterval,iteration=$iteration.jfr"
    ProcessBuilder("$nativeRootPath/build/bin/linuxX64/debugExecutable/native.kexe", cycles.toString()) //FIXME Don't use debug
        .also { it.environment().apply {
            put("LD_PRELOAD", "/opt/async-profiler/lib/libasyncProfiler.so")
            put("ASPROF_COMMAND", "start,interval=$sampleInterval,file=/tmp/profile.jfr")
        }}
        .start()
        .waitFor()
    ProcessBuilder("mv", "/tmp/profile.jfr", file).start().waitFor()
}

fun measureJmh(cycles: Int, sampleInterval: Int, iteration: Int, warmup: Int) {
    ProcessBuilder("mkdir", "/tmp/profileoutput/").start().waitFor()
    ProcessBuilder(
        "java",
        "-jar", "${jvmRootPath}/build/libs/jvm-jmh.jar",
        "-bm", "ss",
        "-bs", cycles.toString(),
        "-wbs", cycles.toString(),
        "-wi", warmup.toString(),
        "-i", "1",
        "-f", "1",
        "-prof", "async:libPath=/opt/async-profiler/lib/libasyncProfiler.so;output=jfr;dir=/tmp/profileoutput/;interval=$sampleInterval"
    )
        .start()
        .waitFor()
    val outputJFR = walkPath("/tmp/profileoutput/").filter { it.endsWith(".jfr") }.single()
    ProcessBuilder("mv", outputJFR, "$outputStorage/platform=jvm,cycles=$cycles,sampleInterval=$sampleInterval,iteration=$iteration,warmup=$warmup.jfr").start().waitFor()
    ProcessBuilder("rm", "-rf", "/tmp/profileoutput/").start().waitFor()
}

ProcessBuilder("gradle", "linuxX64Binaries")
    .directory(File(nativeRootPath))
    .inheritIO()
    .start()
    .waitFor()

ProcessBuilder("gradle", "jmhJar")
    .directory(File(jvmRootPath))
    .inheritIO()
    .start()
    .waitFor()


val cycles = 1000000
val sampleInterval = 100
val warmup = 3
for (iteration in 1..100) {
    print("platform: Native, cycles: $cycles, sampleInterval: $sampleInterval, iteration: $iteration ...")
    val timeNative = measureTime {
        measureNative(cycles, sampleInterval, iteration)
    }
    println(" done in $timeNative")
    print("platform: JVM, cycles: $cycles, sampleInterval: $sampleInterval, iteration: $iteration, warmup: $warmup ...")
    val timeJVM = measureTime {
        measureJmh(cycles, sampleInterval, iteration, warmup)
    }
    println(" done in $timeJVM")
}
