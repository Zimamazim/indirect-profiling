plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
    id("me.champeau.jmh") version "0.7.2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.openjdk.jmh:jmh-core:1.37")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

kotlin {
    jvmToolchain(17)
}

jmh {
    benchmarkMode = listOf("avgt")
    timeOnIteration = "1ms"
    iterations = 60000
    fork = 2
    includes = listOf("big_some_40000_end")
    resultFormat = "JSON"
    jvmArgs = listOf("-XX:+UseSerialGC", "-Xmx2g", "-Xms2g")
    resultsFile = project.file("results/1ms2g.json")
}