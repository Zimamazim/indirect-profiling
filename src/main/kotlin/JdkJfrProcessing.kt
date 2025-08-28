package jfr_processor.profilelib

import jdk.jfr.consumer.RecordedFrame
import jdk.jfr.consumer.RecordingFile
import kotlin.collections.all
import kotlin.io.path.Path

var executionSamplesWithoutStackTrace = 0

//fun getExecutionSamplesWithoutStackTrace(): Int = executionSamplesWithoutStackTrace

fun lazy_samples(filename: String): Sequence<List<RecordedFrame>> = sequence {
    RecordingFile(Path(filename)).use {
        while (it.hasMoreEvents()) {
            it.readEvent()!!
                .takeIf { it.eventType.name == "jdk.ExecutionSample" }
                ?.let {
                    it.stackTrace
                        ?.also { assert(!it.isTruncated()) }
                        ?.let { yield(it.frames) }
                        ?: { executionSamplesWithoutStackTrace += 1 }
                }
        }
    }
}


fun <T> truncate_stack_trace(stack_trace: Iterable<T>, detector: (T) -> Boolean): Iterable<T>? =
    stack_trace.indexOfFirst(detector)
        .takeIf { it != -1 }
        ?.let { stack_trace.take(it + 1) }

fun jvm_get_name(frame: RecordedFrame): String? =
    frame.method.type.name + "." + frame.method.name
fun native_get_name(frame: RecordedFrame): String? = frame.method.name
    .takeIf { it.startsWith("kfun:") }
    ?.removePrefix("kfun:")
    ?.run { when {
        endsWith("#internal") -> removeSuffix("#internal")
        else -> replace("#", ".").substringBefore("(")
    }}

fun jvm_is_sample_platform_independent(sample: Iterable<RecordedFrame>, funToSrcMap: FunToSrcMap): Boolean =
    sample.all {
        jvm_get_name(it)
            ?.let { funToSrcMap[it] }
            ?.all { it.contains("/common/") }
            ?: true
    }

fun native_is_sample_platform_independent(sample: Iterable<RecordedFrame>, debugMap: DebugMap): Boolean =
    sample.all {
        debugMap[it.method.name]?.dir?.contains("/common/") ?: true
    }

fun Sequence<Iterable<RecordedFrame>>.toFreq(dest: FreqMap, keyMapper: (RecordedFrame) -> String?) {
    this.flatten().mapNotNull(keyMapper).forEach { dest[it] = dest.getOrDefault(it, 0) + 1 }
}

//fun get_freqs(project_root: String, profile_root: String): Pair<Map<String, Int>, Map<String, Int>> {
//    val jvm_fun_to_src_map = ktor_get_fun_to_src_map(project_root)
//    val jvm_freq = mutableMapOf<String, Int>()
//    println("JVM functions to sources map loaded")
//
//    val native_debug_map = ktor_get_native_debug_map(project_root)
//    val native_freq = mutableMapOf<String, Int>()
//    println("Native functions to sources map loaded")
//
//    get_valid_tests(profile_root)
//        .forEach { testname ->
//            println(testname)
//            lazy_samples_new(profile_root + "jvm-" + testname + ".jfr")
//                .mapNotNull { truncate_stack_trace(it) { jvm_get_name(it) == testname } }
//                .filter {
//
//                }
//                .flatten()
//                .mapNotNull { jvm_get_name(it) }
//                .forEach { jvm_freq[it] = jvm_freq.getOrDefault(it, 0) + 1 }
//
//            lazy_samples_new(profile_root + "linuxX64-" + testname + ".jfr")
//                .mapNotNull { truncate_stack_trace(it) { native_get_name(it)?.contains(testname) ?: false } }
//                .filter {
//
//                }
//                .flatten()
//                .mapNotNull { native_get_name(it) }
//                .forEach { native_freq[it] = native_freq.getOrDefault(it, 0) + 1 }
//        }
//    return Pair(jvm_freq, native_freq)
//}