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

    (after.user - before.user)
        .also {
            it
                .let { it / (it + after.sys - before.sys) }
                .let { println("Pavel: $it") }
        }
        .let {
            it / wallTime.toDouble(DurationUnit.SECONDS)
        }
        .let { println("Martin: $it") }
}
