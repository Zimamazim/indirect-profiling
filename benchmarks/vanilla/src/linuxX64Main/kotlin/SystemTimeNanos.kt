package jmh_is_stupid_and_cant_use_default_package

import kotlin.system.getTimeNanos

@Suppress("DEPRECATION_ERROR")
actual fun nanos(): Long = getTimeNanos()
