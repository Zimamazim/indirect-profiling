package profilelib

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val this_root = "/home/martinzimen/IdeaProjects/stdlib-profiling/"

@Serializable
data class DwarfData(
    val name: String,
    val dir: String?,
    val filename: String?,
    val line: Int?
)

typealias DebugMap = Map<String, DwarfData>

fun ktor_get_native_debug_map(project_root: String): DebugMap =
    walkPath(project_root)
        .filter { it.contains("/build/bin/linuxX64/") }
        .filter { it.endsWith(".kexe") }
        .map {
            ProcessBuilder(
                this_root + "processing/src/main/python/venv/bin/python",
                this_root + "processing/src/main/python/get_fun_debug_info_from_dwarf_as_json.py",
                it
            ).start()
                .inputStream
                .bufferedReader()
                .use { it.readText() }
                .let { Json.decodeFromString<Map<String, DwarfData>>(it) }
        }
        .let { safe_map_merge(it) }
        .also {
            it
                .filter { it.value.value == null }
                .count()
                .let { println("Ambiguos DWARF info for $it functions") }
        }
        .mapNotNull { pair -> pair.value.value?.let { pair.key to it } }
        .toMap()
