import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    `java-library`
    `maven-publish`
}

group = "edu.jhu.cobra"
version = libs.versions.projectVersion.get()

val minJavaProperty = providers.gradleProperty("minJava").get()
val minJava =
    minJavaProperty.toIntOrNull()?.takeIf { it >= 8 }
        ?: throw GradleException("minJava must be a Java version of 8 or later, got '$minJavaProperty'.")
val targetJavaVersion = JavaVersion.toVersion(minJava)
val runningJavaVersion = JavaVersion.current()
if (runningJavaVersion < targetJavaVersion) {
    throw GradleException(
        "minJava=$minJava requires JDK $minJava or later to run Gradle. " +
            "Gradle is running on JDK ${runningJavaVersion.majorVersion}.",
    )
}

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
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
        freeCompilerArgs.add("-Xjdk-release=$minJava")
    }
}

java {
    sourceCompatibility = targetJavaVersion
    targetCompatibility = targetJavaVersion
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(minJava)
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

ktlint {
    version.set(libs.versions.ktlintEngine.get())
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
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
    publications {
        create<MavenPublication>("maven") { from(components["java"]) }
    }
}
