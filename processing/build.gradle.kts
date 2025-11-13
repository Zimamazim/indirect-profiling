plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.openjdk.jmc:flightrecorder:8.3.1")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.20")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
}

kotlin {
    jvmToolchain(23)
}
