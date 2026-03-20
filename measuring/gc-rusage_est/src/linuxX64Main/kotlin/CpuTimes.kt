import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.linux.getrusage
import platform.linux.rusage
import platform.posix.timeval
import kotlin.native.runtime.NativeRuntimeApi

data class CpuTime(val user: Double, val sys: Double) {}

@OptIn(ExperimentalForeignApi::class, NativeRuntimeApi::class)
fun currentThreadCpuTime(): CpuTime = memScoped {
    val ru = alloc<rusage>()
    val RUSAGE_THREAD = 1
    val result = getrusage(RUSAGE_THREAD, ru.ptr)
    if (result != 0) {
        error("getrusage failed")
    }

    fun timeval.toDouble(): Double = tv_sec.toDouble() + tv_usec.toDouble() / 1_000_000
    CpuTime(
        ru.ru_utime.toDouble(),
        ru.ru_stime.toDouble(),
    )
}

