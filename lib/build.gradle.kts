plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    val srcJavaVersion = libs.versions.javaSource.get()
    val tarJavaVersion = libs.versions.javaTarget.get()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(srcJavaVersion))
    }
    sourceCompatibility = JavaVersion.toVersion(srcJavaVersion)
    targetCompatibility = JavaVersion.toVersion(tarJavaVersion)
}

