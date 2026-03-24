plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.openjdk.jmc:flightrecorder:8.3.1")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.20")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta3n")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.1n")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.5.0n")
}

kotlin {
    jvmToolchain(23)
}
