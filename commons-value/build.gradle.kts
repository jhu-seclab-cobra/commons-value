plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    `java-library`
    `maven-publish`
}

group = "edu.jhu.cobra"
version = "0.1.1"

val jvmVersion =
    libs.versions.jvm
        .get()
        .toInt()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmVersion))
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform {
        excludeTags("performance")
    }
    maxHeapSize = "1g"
    setForkEvery(1)
}

tasks.register<Test>("performanceTest") {
    description = "Runs performance tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("performance")
    }
    testLogging {
        showStandardStreams = true
    }
    jvmArgs("-Xmx2g", "-Xms1g")
}

kover {
    currentProject {
        instrumentation {
            excludedClasses.add("*PerformanceTest*")
            disabledForTestTasks.add("performanceTest")
        }
    }
}

publishing {
    publications { create<MavenPublication>("maven") { from(components["java"]) } }
}

ktlint {
    version.set("1.5.0")
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
}
