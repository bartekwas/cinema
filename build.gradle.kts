plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("com.diffplug.spotless") version "6.25.0"
    application
}

group = "com.bwasik"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

spotless {
    kotlin {
        ktlint("1.3.1")
            .setEditorConfigPath(rootDir.resolve(".editorconfig"))
    }
}
val ktorVersion = "3.0.1"
val kotestVersion = "5.5.5"
val koinVersion = "4.0.0"
val testcontainersVersion = "1.20.4"

dependencies {
    // Ktor dependencies
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.54")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // JWT & Security
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.mindrot:jbcrypt:0.4")

    // Redis
    implementation("io.lettuce:lettuce-core:6.5.0.RELEASE")

    // PostgreSQL Driver
    implementation("org.postgresql:postgresql:42.7.4")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Testcontainers
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")

    // Mocking & Testing
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:3.0.1")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
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
