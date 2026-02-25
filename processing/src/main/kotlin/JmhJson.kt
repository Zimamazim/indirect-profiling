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

fun loadScores(json: JsonElement): Map<String, Double> = json
    .jsonArray.map {
        val name = it.jsonObject["benchmark"]!!.jsonPrimitive.content.substringAfterLast(".")
        val score = it.jsonObject["primaryMetric"]!!.jsonObject["score"]!!.jsonPrimitive.double
        name to score
    }
    .toMap()