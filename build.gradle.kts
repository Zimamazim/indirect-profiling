plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

group = "org.jetbrains.kotlinx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    maven {
//        url = uri("https://packages.adoptium.net/artifactory/maven/")
//    }
    maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }

    maven { url = uri("https://www.jetbrains" +
            ".com/intellij-repository/snapshots") }
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    maven { url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies") }
    maven { url = uri("https://packages.jetbrains.team/maven/p/intellij-platform/intellij-platform-dependencies") }


}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.openjdk.jmc:flightrecorder:8.3.1")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.20")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

//    implementation("com.jetbrains.intellij.platform:util:241.19416.19")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

application {
    mainClass.set("TwitterRunnerKt")
}