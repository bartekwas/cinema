plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    application
}

group = "com.bwasik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-netty:3.0.1")
    implementation("io.ktor:ktor-server-auth:3.0.1")
    implementation("io.ktor:ktor-server-auth-jwt:3.0.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-client-core:3.0.1")
    implementation("io.ktor:ktor-client-cio:3.0.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.1")

    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
    implementation("io.insert-koin:koin-ktor:4.0.0")
    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("com.auth0:java-jwt:4.4.0")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation("io.ktor:ktor-server-tests:3.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.bwasik.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

val localEnvFile = File("${projectDir}/localSetup/local.env")
if (localEnvFile.exists()) {
    localEnvFile.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .forEach { line ->
            val (key, value) = line.split("=", limit = 2)
            System.setProperty(key.trim(), value.trim())
        }
}

tasks.withType<JavaExec> {
    environment(System.getProperties().map { it.key.toString() to it.value.toString() }.toMap())
}