package jfr_processor.profilelib.jsonjfr

/* Original method, data structures and utility functions for processing JFR
   data. It is based on `jfr print --json` command. Superceded by using
   `jdk.jfr` library directly which is much faster, convenient but discovered
    later. This module is kept if some older notebooks need to be replicated.
 */

import jfr_processor.profilelib.get_valid_tests

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val jfr = "/home/Martin.Zimen/.jdks/openjdk-24.0.1/bin/jfr"

data class Frame(val name: String, val package_: String?, val class_: String,
                 val linkage_name: String? = null) {}

fun get_name(frame: Frame): String = frame.class_ + "." + frame.name

typealias StackTrace = List<Frame>
typealias Samples = List<StackTrace>

fun list_functions(samples: Samples): Set<String> =
    sequence { samples.asSequence().forEach { yieldAll(it) } }
        .filter { it.class_.startsWith("kotlin/") }
        .map { get_name(it) }
        .toSet()

data class JvmNativePair<T>(val jvm: T, val native: T)

fun get_samples(root: String): Sequence<JvmNativePair<Samples>> =
    get_valid_tests(root)
        .map { filename ->
            println("Processing $filename")
            fun process(platform: String, process_frame_function: (JsonElement) -> Frame?): Samples =
                load_jfr_as_json(root + platform + "-" + filename + ".jfr")
                    .let { process_jfr_data(it, process_frame_function) }
            JvmNativePair(process("jvm", ::jvm_process_frame), process("linuxX64", ::native_process_frame))
        }

operator fun JsonElement.get(key: String): JsonElement? =
    (this as? JsonObject)
        ?.let { it[key] }
        ?.let { if (it is JsonNull) null else it }

fun load_jfr_as_json(filename: String): JsonElement =
    ProcessBuilder(
        jfr,
        "print",
        "--json",
        "--stack-depth", 2_000_000_000.toString(),
        filename
    ).start()
        .inputStream
        .bufferedReader()
        .use { it.readText() }
        .let { Json.parseToJsonElement(it) }



fun native_process_frame(frame: JsonElement): Frame? {
    val frame_type = frame.jsonObject["type"]!!.jsonPrimitive.content
    when (frame_type) {
        "C++" -> return null
        "Kernel" -> return null
        "Native" -> {}
        else -> throw NotImplementedError("Unknown frame type: $frame_type")
    }
    val linkage_name = frame["method"]!!["name"]!!.jsonPrimitive.content
    return linkage_name
        .also { if (!it.startsWith("kfun:")) return null }
        .removePrefix("kfun:")
        .also {
            if (it.endsWith("#internal")) return it
                .removeSuffix("#internal")
                .split(".")
                .let {
                    Frame(
                        name = it.last(),
                        package_ = "",
                        class_ = it.dropLast(1).joinToString("."),
                        linkage_name = linkage_name,
                    )
                }
        }
        .split("#")
        .let {
            Frame(
                name = it[1].substringBefore("("),
                package_ = null,
                class_ = it[0],
                linkage_name = linkage_name,
            )
        }
}


fun jvm_process_frame(frame: JsonElement): Frame? {
    val frame_type = frame.jsonObject["type"]!!.jsonPrimitive.content
    when (frame_type) {
        "C++" -> return null
        "Native" -> return null
        "Kernel" -> return null
        "Interpreted" -> {}
        "JIT compiled" -> {}
        "Inlined" -> {}
        "C1 compiled" -> {}
        else -> throw NotImplementedError("Unknown frame type: $frame_type")
    }
    return frame.jsonObject["method"]!!.let {
        Frame(
            name = it
                .jsonObject["name"]!!
                .jsonPrimitive.content,
            package_ = it["type"]
                ?.get("package")
                ?.get("name")
                ?.jsonPrimitive
                ?.content,
            class_ = it
                .jsonObject["type"]!!
                .jsonObject["name"]!!
                .jsonPrimitive.content
                .replace("/", "."),
        )
    }
}

fun process_jfr_data(jfr_data: JsonElement, process_frame_function: (JsonElement) -> Frame?): List<List<Frame>> =
    jfr_data
        .jsonObject["recording"]!!
        .jsonObject["events"]!!
        .jsonArray
        .filter {
            it
                .jsonObject["type"]!!
                .jsonPrimitive.content == "jdk.ExecutionSample"
        }
        .map {
            it
                .jsonObject["values"]!!
                .jsonObject["stackTrace"]!!
                .jsonObject["frames"]!!
                .jsonArray
                .map { process_frame_function(it) }
                .filterNotNull()
        }