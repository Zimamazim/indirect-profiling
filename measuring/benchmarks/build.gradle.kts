import kotlinx.benchmark.gradle.NativeBenchmarkTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "2.2.10"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.14"
    kotlin("plugin.allopen") version "2.0.20"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    jvm()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.14")
            }
        }
    }
}

benchmark {
    configurations {
        register("selection") {
            mode = "avgt"
            include("StringSubstringBenchmark")
            include("StringBuilderBenchmark")
            include("MapBenchmark")
        }
    }
    targets {
        register("jvm")
        register("linuxX64") {
            this as NativeBenchmarkTarget
            buildType = NativeBuildType.DEBUG
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}
