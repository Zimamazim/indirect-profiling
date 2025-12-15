package profilelib

import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import jdk.jfr.consumer.RecordedFrame


fun get_valid_tests(root: String): Sequence<String> =
    Files.walk(Path(root))
        .map { it.last().toString() }
        .filter { it.endsWith(".jfr") }
        .map { it.removeSuffix(".jfr").substringAfter("-") }
        .toList()
        .toSet()
        .filter {
            fun is_log_ok(fileName: String): Boolean =
                File(root + fileName + ".log")
                    .readText()
                    .split("\n")
                    .first()
                    .let { it == "0" }
                    .also { if (!it) println("ERROR: " + fileName) }
            is_log_ok("jvm-" + it) && is_log_ok("linuxX64-" + it)
        }
        .asSequence()

data class FunctionStats(
    val jvm_count: Int,
    val native_count: Int,
    val jvm_src: List<Pair<String, Int>>,
    val native_src: List<Pair<String, Int>>,
)

typealias FreqMap = MutableMap<String, Int>

fun match_and_compare_freqs(jvm_freq: FreqMap, native_freq: FreqMap, filterer: (Pair<String, FunctionStats>) -> Boolean) =
    (jvm_freq.map { Triple("JVM", it.key, it.value) } + native_freq.map { Triple("Native", it.key, it.value) })
        .filter { it.second.startsWith("kotlin.") }
        .filterNot { it.second.contains(".jvm.") }
        .groupBy { it.second.substringBefore("__at__").substringAfterLast(".") }
        .mapValues {
            fun process(list: List<Triple<String, String, Int>>): Pair<List<Pair<String, Int>>, Int> =
                list.map { it.second to it.third }
                    .let { it to it.sumOf { it.second } }

            val (jvm, native) = it.value.partition { it.first == "JVM" }
            val (jvm_src, jvm_count) = process(jvm)
            val (native_src, native_count) = process(native)
            FunctionStats(jvm_count, native_count, jvm_src, native_src)
        }
        .toList()
        .filter(filterer)
        .sortedByDescending { it.second.native_count.toFloat() / it.second.jvm_count }
        .forEach {
            println(it.first)
            println(it.second.native_count.toFloat() / it.second.jvm_count)
            println("JVM: ${it.second.jvm_count}")
            it.second.jvm_src.forEach { println(it) }
            println()
            println("Native: ${it.second.native_count}")
            it.second.native_src.forEach { println(it) }
            println()
            println()
        }

typealias Data =  Map<Map<String, String>, Map<String, Int>>

fun load_data(dataPath: String): Data =
    walkPath(dataPath)
        .filter { it.endsWith(".jfr") }
        .map { path ->
            val params = path
                .removeSuffix(".jfr")
                .substringAfterLast("/")
                .split(",")
                .map { it.split("=").let { (k, v) -> k to v} }
                .toMap()
            val freq = mutableMapOf<String, Int>()
            println("'" + path + "'")
            lazy_samples(path).toFreq(freq, when (params["platform"]) {
                "jvm" -> ::jvm_get_name
                "native" -> fun(frame: RecordedFrame): String = frame.method.name
                    .let { if (it.startsWith("kfun:"))
                        it.removePrefix("kfun:").replace("#", ".").substringBefore("(")
                    else
                        "weirdo:" + it
                    }
                else -> throw NotImplementedError("Unknown platform: ${params["platform"]}")
            })
            params to freq.toMap()
        }
        .toMap()


