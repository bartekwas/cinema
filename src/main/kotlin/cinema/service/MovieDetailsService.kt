package com.bwasik.cinema.service

import com.bwasik.cinema.model.http.MovieDetailsResponse
import com.bwasik.cinema.model.http.MovieRatingRequest
import com.bwasik.cinema.repository.AverageRateRepository
import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.omdb.OmdbClient
import com.bwasik.utils.RedisCache
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import kotlin.time.Duration.Companion.hours

private const val NAMESPACE = "OMDB"
private val CACHE_TIME = 12.hours.inWholeSeconds

class MovieDetailsService(
    private val omdbClient: OmdbClient,
    private val movieDetailsRepository: MovieDetailsRepository,
    private val averageRateRepository: AverageRateRepository,
    private val cache: RedisCache,
) {
    suspend fun getMovieDetails(movieId: String) =
        coroutineScope {
            val omdbDetails =
                async {
                        cache.cached(movieId, NAMESPACE, CACHE_TIME) {
                        omdbClient.getMovieDetails(movieId)
                    }
                }
            val internalRates = async { averageRateRepository.getAverageRate(movieId) }
            MovieDetailsResponse.from(omdbDetails.await(), internalRates.await())
        }

    fun postMovieRatings(
        movieRatingRequest: MovieRatingRequest,
        principalId: UUID,
    ) {
        movieDetailsRepository.postMovieRatings(movieRatingRequest, principalId.toString())
    }
}
