pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "stdlib-profiling"
include("benchmarks")