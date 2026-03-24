plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "commons-value"

// Unique module identifier to avoid composite build name collisions (gradle/gradle#847)
include("jhu-seclab-cobra-commons-value")
project(":jhu-seclab-cobra-commons-value").projectDir = file("lib")
