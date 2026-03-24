plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "commons-value"
include("jhu-seclab-cobra-commons-value")
project(":jhu-seclab-cobra-commons-value").projectDir = file("commons-value")
