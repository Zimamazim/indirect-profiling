package jfr_processor.profilelib

import kotlinx.serialization.json.*
import java.io.InputStream
import kotlin.io.path.createTempDirectory
import kotlin.streams.asSequence

class WrappedInputStream(private val input: InputStream) : InputStream() {
    init {
        assert(input.markSupported())
    }

    var started = false
    var finished = false
    override fun read(): Int =
        if (started) {
            if (finished) -1 else input.read()
                .also {
                    if (it == ']'.code) {
                        input.mark(Int.MAX_VALUE)
                        while (!finished) {
                            when (input.read()) {
                                ']'.code -> break
                                -1 -> finished = true
                                else -> continue
                            }
                        }
                        input.reset()
                    }
                }
        } else {
            started = true
            while (input.read() != '['.code) {
            }
            '['.code
        }

    override fun close() {
        input.close()
    }
}

fun lazy_process_disassemble(file: String): Sequence<List<Frame>> {
    val tempDir = createTempDirectory()
    java.lang.ProcessBuilder(
        jfr,
        "disassemble",
        "--output", tempDir.toString(),
        "--max-size", "1000000",
        file
    ).start().waitFor()
    return sequence {
        java.nio.file.Files.walk(tempDir)
            .asSequence()
            .filter { it.toString().endsWith(".jfr") }
            .map {
                val process_frame_function = when (file.substringAfterLast("/").substringBefore("-")) {
                    "jvm" -> ::jvm_process_frame
                    "linuxX64" -> ::native_process_frame
                    else -> throw NotImplementedError("Unknown platform: $file")
                }
                load_jfr_as_json(it.toString())
                    .let { process_jfr_data(it, process_frame_function) }
            }
            .forEach { yieldAll(it) }
    }
}

fun lazy_samples(filename: String):
        Sequence<List<JsonElement>> =
    ProcessBuilder(
        jfr,
        "print",
        "--json",
        "--stack-depth", 2_000_000_000.toString(),
        filename
    ).start()
        .inputStream
        .buffered()
        .let { WrappedInputStream(it) }
        .let { Json.decodeToSequence<JsonElement>(it) }
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
        }

fun remove_base(sample: StackTrace, test_name: String): StackTrace? =
    sample.indexOfLast { test_name in get_name(it) }.takeIf { it >= 0 }?.let { sample.take(it + 1) }

fun lazy_function_names(root: String, platform: String): Sequence<String> = sequence {
    get_valid_tests(root)
        .forEach { test ->
            println(test)
            lazy_samples(
                root + platform + "-" + test + ".jfr",

            )
                .map {
                    it.mapNotNull {
                        if (platform == "jvm") jvm_process_frame(it)
                        else native_process_frame(it)
                    }
                }
                .map { remove_base(it, test) }
                .filterNotNull()
                .forEach {
                    it.forEach { yield(get_name(it)) }
                }
        }
}