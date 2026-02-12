
import kotlin.concurrent.Volatile
import kotlin.time.measureTime
import kotlin.time.DurationUnit
import kotlin.time.Duration

@Volatile
private var blackhole: Any? = null
@Volatile
private var string = "abc".repeat(100000)

fun main() {
    repeat(1_000_000) { blackhole = string.substring(1000, 5000) }
    val iterations = 1_000_000
    val times = ArrayList<Duration>(iterations)
    repeat(iterations) {
        measureTime {
            repeat(100) {
                blackhole = string.substring(1000, 5000)
            }
        }.let { times.add(it) }
    }
    times.forEach { println(it.toDouble(DurationUnit.SECONDS)) }
}
