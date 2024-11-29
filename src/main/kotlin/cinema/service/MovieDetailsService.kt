package com.bwasik.cinema.service

import com.bwasik.cinema.model.http.MovieDetailsResponse
import com.bwasik.cinema.model.http.MovieRatingRequest
import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.omdb.OmdbClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID

class MovieDetailsService(
    private val omdbClient: OmdbClient,
    private val movieDetailsRepository: MovieDetailsRepository
) {

    suspend fun getMovieDetails(movieId: String) = coroutineScope{
        val omdbDetails =  async { omdbClient.getMovieDetails(movieId) }
        val internalRates = async { movieDetailsRepository.getAverageRate(movieId) }
        MovieDetailsResponse.from(omdbDetails.await(), internalRates.await())
    }


    fun postMovieRatings(movieRatingRequest: MovieRatingRequest, principalId: UUID) {
        movieDetailsRepository.postMovieRatings(movieRatingRequest, principalId.toString())
    }

}