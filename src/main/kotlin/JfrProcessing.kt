package jfr_processor.profilelib

import jfr_processor.profilelib.jfr

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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



