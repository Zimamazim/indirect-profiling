package jfr_processor.profilelib

import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.streams.asSequence

fun walkPath(path: String): Sequence<String> =
    Files.walk(Path(path))
        .map { it.toString() }
        .asSequence()