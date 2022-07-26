import java.io.FileInputStream
import java.util.*

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "icu.ketal"
version = "0.0.1"
application {
    mainClass.set("icu.ketal.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<JavaExec> {
    systemProperties["AppSecret"] = getLocalProperty("AppSecret")
    systemProperties["AppId"] = getLocalProperty("AppId")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.jetbrains.exposed:exposed-core:0.38.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.38.2")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:0.38.2")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation(kotlin("test-junit", kotlinVersion))
}

fun Project.getLocalProperty(key: String, file: String = "local.properties"): Any {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        FileInputStream(File(rootProject.rootDir, "local.properties")).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")
    return properties.getProperty(key)
}
