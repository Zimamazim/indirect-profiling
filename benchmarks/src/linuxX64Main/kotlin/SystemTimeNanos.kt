package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlin.system.getTimeNanos
//
//actual inline fun readTime(): Long = getTimeNanos()
@Suppress("DEPRECATION_ERROR")
actual fun nanos(): Long = getTimeNanos()