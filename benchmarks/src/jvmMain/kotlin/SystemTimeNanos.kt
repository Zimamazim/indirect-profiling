package org.jetbrains.kotlinx.stdlibbenchmarks

//import java.
//import kotlin.system.getTimeNanos
//
//@Suppress("DEPRECATION_ERROR")
//actual inline fun readTime(): Long = getTimeNanos()
actual fun nanos(): Long = System.nanoTime()