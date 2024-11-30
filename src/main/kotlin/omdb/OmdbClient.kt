package com.bwasik.omdb

import com.bwasik.omdb.model.OmdbMovieDetailsResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.slf4j.LoggerFactory

class OmdbClient(
    private val clientProvider: KtorClientProvider,
    private val baseUrl: String,
    private val apiKey: String
) {
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    suspend fun getMovieDetails(movieId: String): OmdbMovieDetailsResponse {
        LOGGER.info("Calling OMBD for movie $movieId")
        return clientProvider.client.get("$baseUrl/") {
            url {
                parameters.append("apikey", apiKey)
                parameters.append("i", movieId)
            }
        }.body()
    }
}