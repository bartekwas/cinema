package com.bwasik.omdb

import com.bwasik.omdb.model.MovieDetailsResponse
import io.ktor.client.call.*
import io.ktor.client.request.*

class OmdbClient(
    private val clientProvider: KtorClientProvider,
    private val baseUrl: String,
    private val apiKey: String
) {
    suspend fun getMovieDetails(movieId: String): MovieDetailsResponse =
        clientProvider.client.get("$baseUrl/") {
            url {
                parameters.append("apikey", apiKey)
                parameters.append("i", movieId)
            }
        }.body()
}