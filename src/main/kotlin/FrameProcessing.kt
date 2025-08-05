package jfr_processor.profilelib

import kotlinx.serialization.json.JsonElement
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

data class Frame(val name: String, val package_: String?, val class_: String,
                 val linkage_name: String? = null) {}

typealias StackTrace = List<Frame>
typealias Samples = List<StackTrace>

data class JvmNativePair<T>(val jvm: T, val native: T)

fun get_name(frame: Frame): String = frame.class_ + "." + frame.name

fun list_functions(samples: Samples): Set<String> =
    sequence { samples.asSequence().forEach { yieldAll(it) } }
        .filter { it.class_.startsWith("kotlin/") }
        .map { get_name(it) }
        .toSet()

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

fun get_samples(root: String): Sequence<JvmNativePair<Samples>> =
    get_valid_tests(root)
        .map { filename ->
            println("Processing $filename")
            fun process(platform: String, process_frame_function: (JsonElement) -> Frame?): Samples =
                load_jfr_as_json(root + platform + "-" + filename + ".jfr")
                    .let { process_jfr_data(it, process_frame_function) }
            JvmNativePair(process("jvm", ::jvm_process_frame), process("linuxX64", ::native_process_frame))
        }