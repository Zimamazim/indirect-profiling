package profilelib

import org.jetbrains.kotlin.com.intellij.util.io.*
import org.jetbrains.kotlin.incremental.storage.IntCollectionExternalizer
import org.jetbrains.kotlin.incremental.storage.LookupSymbolKeyDescriptor
import org.jetbrains.kotlin.incremental.storage.StringExternalizer
import org.jetbrains.kotlin.incremental.storage.toDescriptor
import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutput
import java.lang.classfile.ClassFile
import java.nio.file.Files
import kotlin.io.path.Path

class ListExternalizer<T>(private val elementExternalizer: DataExternalizer<T>) : DataExternalizer<List<T>> {
    override fun save(output: DataOutput, value: List<T>) {
        value.forEach { elementExternalizer.save(output, it) }
    }

    override fun read(input: DataInput): List<T> {
        val stream = input as DataInputStream
        return buildList {
            while (stream.available() > 0) {
                add(elementExternalizer.read(stream))
            }
        }
    }
}

class StringDescriptor : KeyDescriptor<String> {
    override fun save(output: DataOutput, string: String) = StringExternalizer.save(output, string)
    override fun read(input: DataInput) = StringExternalizer.read(input)
    override fun getHashCode(string: String) = string.hashCode()
    override fun isEqual(val1: String, val2: String) = val1 == val2
}

fun <K, V> load_map(path: String, keyDescriptor: KeyDescriptor<K>, valueExternalizer: DataExternalizer<V>):
        Map<K, V> = buildMap {
    PersistentHashMap(Path(path), keyDescriptor, valueExternalizer).use { map ->
        map.processKeys {
            put(it, map[it]!!)
            true
        }
    }
}

class ExplainableNullable<T> private constructor(val value: T?, val reason: String?) {

    constructor(value: T) : this(value, null)

    companion object {
        fun <V> initNull(reason: String): ExplainableNullable<V> =
            ExplainableNullable(null, reason)
    }
}

fun load_string_map(path: String): Map<String, String> =
    load_map(path, StringExternalizer.toDescriptor(), StringExternalizer)

fun load_string_list_map(path: String): Map<String, List<String>> =
    load_map(path, StringExternalizer.toDescriptor(), ListExternalizer(StringExternalizer))

fun <K, V> safe_map_merge(maps: Iterable<Map<K, V>>): Map<K,
        ExplainableNullable<V>> =
    safe_map_merge(maps.asSequence())

fun <K, V> safe_map_merge(maps: Sequence<Map<K, V>>): Map<K,
        ExplainableNullable<V>> =
    buildMap {
        maps.forEach { map ->
            map.forEach { (k, v) ->
                if (get(k) == null) put(k, ExplainableNullable(v))
                else if (get(k)?.value != null && get(k)!!.value != v) put(
                    k,
                    ExplainableNullable.initNull(
                        "Incompatible values for key $k: ${get(k)!!.value} and $v"
                    )
                )
            }
        }
    }

fun get_fun_to_src_map(path: String): Map<String, Set<String>> {
    val atomicfu = walkPath(path).filter { it.contains("/atomicfu/") }.toSet()
    val atomicfu_orig = walkPath(path).filter { it.contains("/atomicfu-orig/") }
        .map { it.replace("/atomicfu-orig/", "/atomicfu/") }
        .toSet()
    assert(atomicfu == atomicfu_orig)
    val file_to_class = walkPath(path)
        .filter { it.endsWith("source-to-output.tab") }
        .filterNot { it.contains("/compilePluginsBlocks/") }
        .map {
            load_string_list_map(it)
                .mapValues {
                    it.value.filter { it.endsWith(".class") }
                }
        }
        .let { safe_map_merge(it).mapValues { it.value.value!! } }
    val fun_to_class = file_to_class
        .values
        .flatten()
        .filter { it.endsWith(".class") }
        .toSet()
        .map { path + "/" + it }
        .map { path ->
            val classModel = ClassFile.of().parse(Files.readAllBytes(Path(path)))
            classModel.methods().associate {
                classModel.thisClass().name().stringValue() + "." + it.methodName() to path
            }
        }
        .let { safe_map_merge(it).mapValues { it.value.value!! } }
    return fun_to_class
        .mapValues { class_full_path ->
            file_to_class.filterValues { it.any { class_full_path.value.endsWith(it) } }.keys
        }
}


typealias FunToSrcMap = Map<String, Set<String>>

fun ktor_get_fun_to_src_map(project_root: String): FunToSrcMap =
    walkPath(project_root)
        .filter { it.endsWith("/build") }
        .map { get_fun_to_src_map(it) }
        .let { safe_map_merge(it) }
        .let { it.mapNotNull { pair -> pair.value.value?.let { pair.key.replace("/", ".") to it } } }
        .toMap()
