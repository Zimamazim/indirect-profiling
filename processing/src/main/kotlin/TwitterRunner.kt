import profilelib.jvm_get_name
import profilelib.lazy_samples
import profilelib.toFreq
import profilelib.walkPath

import java.io.File
import kotlin.time.measureTime

val jmhRepoPath = "/home/martinzimen/IdeaProjects/stdlib-profiling-jmh/"
val rawRepoPath = "/home/martinzimen/IdeaProjects/stdlib-profiling-raw/"
val outputStorage = "/home/martinzimen/outputStorage/twitter/"

fun measureNative(cycles: Int, sampleInterval: Int, iteration: Int) {
    val file = "$outputStorage/platform=native,cycles=$cycles,sampleInterval=$sampleInterval,iteration=$iteration.jfr"
    ProcessBuilder("$rawRepoPath/build/bin/linuxX64/debugExecutable/stdlib-profiling-raw.kexe", cycles.toString()) //FIXME Don't use debug
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
        "-jar", "${jmhRepoPath}/build/libs/stdlib-profiling-jmh-1.0-SNAPSHOT-jmh.jar",
        "-bm", "ss",
        "-bs", cycles.toString(),
        "-wbs", cycles.toString(),
        "-wi", "3",
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

fun main() {

    ProcessBuilder("./gradlew", "linuxX64Binaries")
        .directory(File(rawRepoPath))
        .inheritIO()
        .start()
        .waitFor()

    ProcessBuilder("./gradlew", "jmhJar")
        .directory(File(jmhRepoPath))
        .inheritIO()
        .start()
        .waitFor()


//    val jvm_freq = mutableMapOf<String, Int>()
//    lazy_samples(outputJFR).toFreq(jvm_freq, ::jvm_get_name)
//    jvm_freq.forEach { (k, v) -> println("$k: $v") }

    //TODO Determine 100000 at 1 takes appx 1 minute for JVM
    //TODO Determine 100000 at 1 takes appx 4.5 minute for K/N debug
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
}
