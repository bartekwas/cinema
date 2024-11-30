package com.bwasik.omdb

import com.bwasik.omdb.model.OmdbMovieDetailsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class OmdbClient(
    private val clientProvider: KtorClientProvider,
    private val baseUrl: String,
    private val apiKey: String,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun getMovieDetails(movieId: String): OmdbMovieDetailsResponse {
        logger.info("Calling OMBD for movie $movieId")
        return clientProvider.executeRequest {
            get("$baseUrl/") {
                url {
                    parameters.append("apikey", apiKey)
                    parameters.append("i", movieId)
                }
            }.body()
        }
    }
}
