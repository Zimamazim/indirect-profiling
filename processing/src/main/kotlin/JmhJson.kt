package profilelib

import java.io.File
import kotlinx.serialization.json.*

fun loadJson(path: String): JsonElement = Json.parseToJsonElement(File(path).readText())
fun singleBenchmark(it: JsonElement): JsonElement = it.jsonArray.single()
fun extractMetric(it: JsonElement, metric: String = ""): List<List<Double>> = it
    .jsonObject
    .let {
        when (metric) {
            "" -> it["primaryMetric"]!!
            else -> it["secondaryMetrics"]!!.jsonObject[metric]!!
        }
    }
    .jsonObject["rawData"]!!
    .jsonArray
    .map {
        it.jsonArray.map { it.jsonPrimitive.double }.toList()
    }
    .toList()

fun loadScores(json: JsonElement): Map<String, Pair<Double, Double>> = json
    .jsonArray.map {
        val name = it.jsonObject["benchmark"]!!.jsonPrimitive.content.substringAfterLast(".")
        val score = it.jsonObject["primaryMetric"]!!.jsonObject["score"]!!.jsonPrimitive.double
        val error = it.jsonObject["primaryMetric"]!!.jsonObject["scoreError"]!!.jsonPrimitive.double
        val params = it.jsonObject["params"]?.jsonObject?.toMap()?.mapValues { it.value.jsonPrimitive.toString() }
                        ?.takeIf { it.isNotEmpty() }?.toString()?.replace(" ", "") ?: ""
        (name + params) to (score to error)
    }
    .toMap()