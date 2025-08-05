package jfr_processor.profilelib

import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.streams.asSequence

val jfr = "/home/Martin.Zimen/.jdks/openjdk-24.0.1/bin/jfr"

fun walkPath(path: String): Sequence<String> =
    Files.walk(Path(path))
        .map { it.toString() }
        .asSequence()