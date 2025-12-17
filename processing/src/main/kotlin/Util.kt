package profilelib

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.kandy.dsl.plot
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.streams.asSequence
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.statistics.kandy.layers.histogram

fun walkPath(path: String): Sequence<String> =
    Files.walk(Path(path))
        .map { it.toString() }
        .asSequence()

fun <T> plot_histogram(data: Map<String, List<T>>): Plot {
    val list = data.toList()
    val df = dataFrameOf(
        "sample" to list.flatMap { it.second },
        "group" to list.flatMap { group -> group.second.map { group.first } }
    )

    return df.groupBy("group").plot {
        histogram("sample")
    }
}