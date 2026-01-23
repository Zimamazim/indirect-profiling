import kotlin.concurrent.Volatile
import kotlin.time.measureTime
import kotlin.time.DurationUnit
import kotlin.time.Duration

@Volatile
private var blackhole: Any? = null
@Volatile
private var string = "abc".repeat(100000)

fun main() {
    repeat(10_000_000) { blackhole = string.substring(1000, 5000) }
    val before = currentThreadCpuTime()
    val wallTime = measureTime {
        repeat(100_000_000) {
            blackhole = string.substring(1000, 5000)
        }
    }
    val after = currentThreadCpuTime()

    val user = after.user - before.user
    val sys = after.sys - before.sys
    val wall = wallTime.toDouble(DurationUnit.SECONDS)

    println("Pavel: ${ sys / (user + sys) }")
    println("user: ${ 1 - user / wall }")
    println("sys + user: ${ 1 - (sys + user) / wall }")
}
