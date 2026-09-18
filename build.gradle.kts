plugins {
    kotlin("jvm") version "2.4.0"

    id("io.github.goooler.shadow") version "8.1.8"
    id("io.ktor.plugin") version "2.3.11"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "shashankbisht.com"
version = "1.0-SNAPSHOT"

val ktorVersion = "3.0.0"
val langchain4jVersion = "0.31.0"

application {
    // Replace with your actual package name where Application.kt lives
    mainClass.set("shashankbisht.com.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Ktor Core & Server
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // JSON Serialization & Content Negotiation
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // Server-Sent Events (SSE) for streaming AI tokens
    implementation("io.ktor:ktor-server-sse:$ktorVersion")

    // LangChain4j for Ollama and PGVector
    implementation("dev.langchain4j:langchain4j:$langchain4jVersion")
    implementation("dev.langchain4j:langchain4j-ollama:$langchain4jVersion")
    implementation("dev.langchain4j:langchain4j-pgvector:$langchain4jVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}