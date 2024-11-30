import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinemaApp
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.system.withSystemProperties
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.testing.testApplication
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class ApplicationE2ETest :
    StringSpec(
        {

            // Setup Test Containers
            val postgresContainer =
                PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest")).apply {
                    withDatabaseName("test_db")
                    withUsername("test_user")
                    withPassword("test_password")
                }

            val redisContainer =
                GenericContainer<Nothing>(DockerImageName.parse("redis:latest")).apply {
                    withExposedPorts(6379)
                }

            val wireMockServer = WireMockServer(8080)

            beforeSpec {
                // Start Containers
                postgresContainer.start()
                redisContainer.start()
                wireMockServer.start()

                // WireMock Stub for OMDb API
                wireMockServer.stubFor(
                    get(urlPathEqualTo("/"))
                        .withQueryParam("apikey", equalTo("test_api_key"))
                        .withQueryParam("i", equalTo("tt1375666"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                    """
                                    {
                                        "Title": "Inception",
                                        "Year": "2010",
                                        "imdbRating": "8.8",
                                        "imdbID": "tt1375666"
                                    }
                                    """.trimIndent(),
                                ),
                        ),
                )

                // Start Koin with Test Module
                startKoin {
                    modules(testModule(postgresContainer, redisContainer))
                }
            }

            afterSpec {
                // Cleanup
                stopKoin()
                postgresContainer.stop()
                redisContainer.stop()
                wireMockServer.stop()
            }

            "should fetch movie details and persist data in the database" {
                withSystemProperties(
                    mapOf(
                        "JWT_SECRET_KEY" to "pwd",
                        "JWT_SECRET_KEY" to "pwd",
                        "jwt.secret" to "pwdpwd",
                        "DB_URL" to postgresContainer.jdbcUrl,
                        "DB_USER" to postgresContainer.username,
                        "DB_PASSWORD" to postgresContainer.password,
                        "REDIS_URL" to "redis://${redisContainer.host}:${redisContainer.getMappedPort(6379)}",
                        "OMDB_BASE_URL" to "http://localhost:8080", // WireMock URL
                        "OMDB_API_KEY" to "test_api_key",
                    ),
                ) {
                    testApplication {
                        application {
                            cinemaApp()
                        }
                        val client =
                            createClient {
                                followRedirects = true
                            }

                        val response = client.get("/api/movie_details/tt1375666")
                        response.status.value shouldBe 200
                        val responseBody = response.body<String>()
                        responseBody shouldContain "Inception"
                    }
                }
            }
        },
    )

fun testModule(
    postgresContainer: PostgreSQLContainer<*>,
    redisContainer: GenericContainer<*>,
): org.koin.core.module.Module =
    module {
        single { setupDatabase(postgresContainer) }
        single { setupRedis(redisContainer) }
        single { MovieDetailsService(get(), get(), get(), get()) }
        single { MovieDetailsRepository() }
    }

fun setupDatabase(container: PostgreSQLContainer<*>): Database =
    Database.connect(
        url = container.jdbcUrl,
        driver = "org.postgresql.Driver",
        user = container.username,
        password = container.password,
    )

fun setupRedis(container: GenericContainer<*>): RedisCommands<String, String> {
    val redisClient = RedisClient.create("redis://${container.host}:${container.getMappedPort(6379)}")
    return redisClient.connect().sync()
}
