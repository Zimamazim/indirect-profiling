plugins {
    kotlin("multiplatform") version "2.1.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.5.0-SNAPSHOT"
    kotlin("plugin.allopen") version "2.0.20"
}

group = "org.jetbrains.kotlinx"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    jvm()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.5.0-SNAPSHOT")
            }
        }
    }
}

benchmark {
    targets {
        register("jvm")
        register("linuxX64")
    }

    configurations {
        named("main") {
            include("StringFirstBenchmark")
            mode = "AverageTime"
//            advanced("jvmGCAfterIteration", true)
//            advanced("nativeGCAfterIteration", true)
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}