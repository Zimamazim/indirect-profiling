package profilelib

import kotlin.math.pow
import kotlin.math.sqrt

fun mean(values: List<Double>): Double = values.sum() / values.size
fun variance(values: List<Double>): Double = values.sumOf{ (it - mean(values)).let { it * it } } / values.size
fun stdDev(values: List<Double>): Double = Math.sqrt(variance(values))
fun cv(values: List<Double>): Double = stdDev(values) / mean(values)
fun formattedMeanCV(values: List<Double>): String = "%.3f (%.3f)".format(mean(values), cv(values))

fun compareVarianceAndBias(values: Map<String, List<Double>>) {
    values.forEach { println("${it.key}: ${formattedMeanCV(it.value)}") }
    println()
    values.values.map(::mean).let {
        val bias = it.max() - it.min()
        println("Bias: $bias (${bias / ((it.max() + it.min()) / 2)})")
    }
}

fun propagateErrorThroughRatio(x: Double, xError: Double, y: Double, yError: Double) =
    // Formula for propagation of uncertainty through division
    sqrt( (x/y) * (xError.pow(2) / x + yError.pow(2) / y ) )

fun propagateErrorThroughSum(xError: Double, yError: Double) = sqrt(xError.pow(2) + yError.pow(2))
