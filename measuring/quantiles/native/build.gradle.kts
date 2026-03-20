plugins {
    kotlin("multiplatform") version "2.2.10"
}

repositories {
    mavenCentral()
}

kotlin {

    linuxX64 {
        binaries {
            executable {
                entryPoint = "org.jetbrains.stdlibprofiling.main"
//                freeCompilerArgs += "-Xruntime-logs=gc=info"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.14")
            }
        }
    }
}
