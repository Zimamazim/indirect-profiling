plugins {
    kotlin("multiplatform") version "2.2.10"
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64(){
        binaries {
            executable{
                entryPoint = "main"
            }
        }
    }
}
