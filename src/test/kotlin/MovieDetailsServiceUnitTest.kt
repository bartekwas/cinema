import com.bwasik.cinema.model.http.InternalRating
import com.bwasik.cinema.model.http.MovieDetailsResponse
import com.bwasik.cinema.repository.AverageRateRepository
import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.omdb.OmdbClient
import com.bwasik.omdb.model.OmdbMovieDetailsResponse
import com.bwasik.omdb.model.Rating
import com.bwasik.utils.RedisCache
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.testcontainers.containers.GenericContainer

class MovieDetailsServiceUnitTest {

    companion object {
        private const val REDIS_PORT = 6379
    }

    private lateinit var redisContainer: GenericContainer<*>
    private lateinit var cache: RedisCache
    private lateinit var omdbClient: OmdbClient
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var averageRateRepository: AverageRateRepository
    private lateinit var movieDetailsService: MovieDetailsService

    @BeforeEach
    fun setUp() {
        redisContainer = GenericContainer("redis:6.2.11").apply {
            withExposedPorts(REDIS_PORT)
            start()
        }
        val redisHost = redisContainer.host
        val redisPort = redisContainer.getMappedPort(REDIS_PORT)

        val redisClient = RedisClient.create("redis://$redisHost:$redisPort")
        val redisConnection: StatefulRedisConnection<String, String> = redisClient.connect()
        cache = RedisCache(redisConnection) // Pass the connection to RedisCache
        omdbClient = mockk()
        movieDetailsRepository = mockk()
        averageRateRepository = mockk()

        movieDetailsService = MovieDetailsService(omdbClient, movieDetailsRepository, averageRateRepository, cache)
    }

    @AfterEach
    fun tearDown() {
        redisContainer.stop()
    }

    @Test
    fun `should fetch movie details and store in cache`() = runBlocking {
        val movieId = "tt1234567"
        val omdbResponse = OmdbMovieDetailsResponse("Test Movie", "2024", "Description", "Genre", "Director")
        val averageRate = InternalRating("8.5", 10)
        val expectedResponse = MovieDetailsResponse.from(omdbResponse, InternalRating("8.5", 10))

        coEvery { omdbClient.getMovieDetails(movieId) } returns omdbResponse
        coEvery { averageRateRepository.getAverageRate(movieId) } returns averageRate

        val response1 = movieDetailsService.getMovieDetails(movieId) // Cache miss
        val response2 = movieDetailsService.getMovieDetails(movieId) // Cache hit

        assertEquals(expectedResponse, response1)
        assertEquals(expectedResponse, response2)

        coVerify(exactly = 1) { omdbClient.getMovieDetails(movieId) }
        coVerify(exactly = 2) { averageRateRepository.getAverageRate(movieId) }
    }

    @Test
    fun `should merge internal and external ratings`() = runBlocking {
        val movieId = "tt7654321"
        val omdbResponse =
            OmdbMovieDetailsResponse(
                "Another Movie",
                "2022",
                "Another Description",
                "Action",
                "Another Director",
                ratings =listOf(
                    Rating("omdb", "6")
                )
            )

        val averageRate = InternalRating("8.5", 10)

        coEvery { omdbClient.getMovieDetails(movieId) } returns omdbResponse
        coEvery { averageRateRepository.getAverageRate(movieId) } returns averageRate

        val response = movieDetailsService.getMovieDetails(movieId)

        // Assert
        assertEquals(omdbResponse.title, response.title)
        assertEquals(response.ratings[0].source, "INTERNAL")
    }
}