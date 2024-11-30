package com.bwasik.omdb

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.slf4j.LoggerFactory
import java.time.Duration

class KtorClientProvider {
    private val logger = LoggerFactory.getLogger(KtorClientProvider::class.java)

    val client: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

    private val circuitBreaker: CircuitBreaker =
        CircuitBreaker.of(
            "httpClientCircuitBreaker",
            CircuitBreakerConfig
                .custom()
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .minimumNumberOfCalls(5)
                .build(),
        )

    private val retry: Retry =
        Retry.of(
            "httpClientRetry",
            RetryConfig
                .custom<Any>()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception::class.java)
                .build(),
        )

    suspend fun <T> executeRequest(block: suspend HttpClient.() -> T): T =
        circuitBreaker.executeSuspendFunction {
            retry.executeSuspendFunction {
                client.block()
            }
        }

    fun close() {
        client.close()
    }
}
