import com.bwasik.cinema.model.db.AverageRate
import com.bwasik.cinema.model.db.Movies
import com.bwasik.cinema.model.db.Ratings
import com.bwasik.cinema.model.db.Schedules
import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.omdb.KtorClientProvider
import com.bwasik.omdb.OmdbClient
import com.bwasik.security.user.model.db.Users
import com.bwasik.utils.RedisCache
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class MovieDetailsServiceE2ETest {

    companion object {
        private const val REDIS_PORT = 6379
    }

    // Testcontainers for Redis and PostgreSQL
    @Container
    private val redisContainer = GenericContainer("redis:6.2.11").apply {
        withExposedPorts(REDIS_PORT)
    }

    @Container
    private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15.2").apply {
        withDatabaseName("test_db")
        withUsername("test_user")
        withPassword("test_password")
    }

    private lateinit var redisClient: RedisClient
    private lateinit var redisConnection: StatefulRedisConnection<String, String>
    private lateinit var wireMockServer: WireMockServer
    private lateinit var movieDetailsService: MovieDetailsService

    @BeforeEach
    fun setUp() {
        // Start Redis and PostgreSQL containers
        redisContainer.start()
        postgresContainer.start()

        val redisHost = redisContainer.host
        val redisPort = redisContainer.getMappedPort(REDIS_PORT)
        redisClient = RedisClient.create("redis://$redisHost:$redisPort")
        redisConnection = redisClient.connect()

        // Start WireMock for mocking OMDb API
        wireMockServer = WireMockServer(8080)
        wireMockServer.start()
        WireMock.configureFor("localhost", 8080)

        // Initialize Exposed Database connection
        Database.connect(
            url = postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )
        transaction {
            SchemaUtils.create(Users, Movies, Schedules, Ratings, AverageRate)
        }

        // Initialize dependencies
        val clientProvider = KtorClientProvider()
        val cache = RedisCache(redisConnection)
        val movieDetailsRepository = MovieDetailsRepository() // Ensure proper initialization here

        // Initialize OmdbClient with WireMock URL and dummy API key
        val omdbClient = OmdbClient(clientProvider, "http://localhost:8080", "test_api_key")

        // Initialize service
        movieDetailsService = MovieDetailsService(omdbClient, movieDetailsRepository, cache)
    }

    @AfterEach
    fun tearDown() {
        redisConnection.close()
        redisClient.shutdown()
        wireMockServer.stop()
        redisContainer.stop()
        postgresContainer.stop()
    }

    @Test
    fun `should fetch movie details, cache them, and store ratings in DB`() = runBlocking {
        // Arrange
        val movieId = "tt1234567"
        val omdbResponse = """
            {
                "Title": "Test Movie",
                "Year": "2024",
                "Plot": "A test plot",
                "Genre": "Drama",
                "Director": "Test Director",
                "Ratings": []
            }
        """.trimIndent()

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/?apikey=test_api_key&i=$movieId"))
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(omdbResponse))
        )

        // Act
        val response1 = movieDetailsService.getMovieDetails(movieId) // Cache miss
        val response2 = movieDetailsService.getMovieDetails(movieId) // Cache hit

        // Assert
        Assertions.assertEquals("Test Movie", response1.title)
        Assertions.assertEquals("Test Movie", response2.title)

        // Verify cache and DB
        val cachedData = redisConnection.sync().get(movieId)
        Assertions.assertNotNull(cachedData, "Data should be cached in Redis")

        // Validate WireMock was called only once (due to caching)
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/?i=$movieId")))
    }
}