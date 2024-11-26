package com.bwasik.cinema.service

import com.bwasik.omdb.OmdbClient

class CinemaService(
    private val omdbClient: OmdbClient
) {

    suspend fun getMovieDetails(movieId: String) =
        omdbClient.getMovieDetails(movieId)
}