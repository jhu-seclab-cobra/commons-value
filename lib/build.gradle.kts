plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

group = "edu.jhu.seclab.cobra"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.13.0")
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
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

