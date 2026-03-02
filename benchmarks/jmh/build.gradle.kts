plugins {
    kotlin("jvm") version "2.2.10"
    id("me.champeau.jmh") version "0.7.2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjdk.jmh:jmh-core:1.37")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

kotlin {
    jvmToolchain(17)
}
