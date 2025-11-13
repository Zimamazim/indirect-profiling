@file:DependsOn("../processing/build/classes/kotlin/main")

import profilelib.walkPath
import java.io.File
import kotlin.time.measureTime

val jvmRootPath = "../targets/serialization-twitterBM/jvm/"
val nativeRootPath = "../targets/serialization-twitterBM/native/"
val outputStorage = "../jfrStorage/serialization-twitterBM/sampleInterval_and_cycles_variance_measurement"
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

fun measureJmh(cycles: Int, sampleInterval: Int, iteration: Int) {
    ProcessBuilder("mkdir", "/tmp/profileoutput/").start().waitFor()
    ProcessBuilder(
        "java",
        "-jar", "${jvmRootPath}/build/libs/jvm-jmh.jar",
        "-Xmx48g",
        "-Xms48g",
        "-bm", "ss",
        "-bs", cycles.toString(),
        "-wbs", cycles.toString(),
        "-wi", "1",
        "-i", "1",
        "-f", "1",
        "-prof", "async:libPath=/opt/async-profiler/lib/libasyncProfiler.so;output=jfr;dir=/tmp/profileoutput/;interval=$sampleInterval"
    )
        .start()
        .waitFor()
    val outputJFR = walkPath("/tmp/profileoutput/").filter { it.endsWith(".jfr") }.single()
    ProcessBuilder("mv", outputJFR, "$outputStorage/platform=jvm,cycles=$cycles,sampleInterval=$sampleInterval,iteration=$iteration.jfr").start().waitFor()
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


for (iteration in 1..1000) {
    for (cycles in listOf(100000, 50000, 10000, 5000, 1000, 500, 100)) {
        for (sampleInterval in listOf(1, 5, 10, 50, 100)) {
            print("platform: Native, cycles: $cycles, sampleInterval: $sampleInterval, iteration: $iteration ...")
            val timeNative = measureTime {
                measureNative(cycles, sampleInterval, iteration)
            }
            println(" done in $timeNative")
            print("platform: JVM, cycles: $cycles, sampleInterval: $sampleInterval, iteration: $iteration ...")
            val timeJVM = measureTime {
                measureJmh(cycles, sampleInterval, iteration)
            }
            println(" done in $timeJVM")
        }
    }
}
