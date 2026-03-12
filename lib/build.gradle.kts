plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    `java-library`
    `maven-publish`
}

group = "edu.jhu.cobra"
version = "0.1.0"

val sourceJavaVersion = JavaVersion.toVersion(libs.versions.javaSource.get())
val targetJavaVersion = JavaVersion.toVersion(libs.versions.javaTarget.get())

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.13.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
}

kotlin {
    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(sourceJavaVersion.majorVersion)) }
    compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(targetJavaVersion.toString()) }
}

java {
    sourceCompatibility = sourceJavaVersion
    targetCompatibility = targetJavaVersion
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform {
        excludeTags("performance")
    }
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

