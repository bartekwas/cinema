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

val ktorVersion = "3.0.1"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-plugins:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.54")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.insert-koin:koin-ktor:4.0.0")
    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("com.auth0:java-jwt:4.4.0")

    implementation("io.lettuce:lettuce-core:6.5.0.RELEASE")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("io.mockk:mockk:1.13.13")
    implementation("io.insert-koin:koin-ktor:$ktorVersion")
    testImplementation("io.insert-koin:koin-test:$ktorVersion")
    testImplementation("io.insert-koin:koin-ktor:$ktorVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    testImplementation("io.ktor:ktor-server-tests:2.3.13")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("io.kotest:kotest-extensions-jvm:5.9.1")

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