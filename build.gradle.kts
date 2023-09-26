plugins {
    application
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.3"
}

group = "store.flow.delta.be"
version = "0.0.1"

application {
    mainClass.set("store.flow.delta.be.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Utils
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.azam.ulidj:ulidj:1.0.4")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.42.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.42.1")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.42.1")
    implementation("org.xerial:sqlite-jdbc:3.42.0.1")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.3")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.3")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10")
}

application {
    mainClass.set("be.delta.flow.store.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
