package com.bwasik.cinema.model.http

import com.bwasik.omdb.model.OmdbMovieDetailsResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.bwasik.omdb.model.Rating as OmdbRating

@Serializable
data class MovieDetailsResponse(
    val title: String? = null,
    val year: String? = null,
    val rated: String? = null,
    val released: String? = null,
    val runtime: String? = null,
    val genre: String? = null,
    val director: String? = null,
    val writer: String? = null,
    val actors: String? = null,
    val plot: String? = null,
    val language: String? = null,
    val country: String? = null,
    val awards: String? = null,
    val poster: String? = null,
    val metascore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val imdbID: String? = null,
    val type: String? = null,
    val dvd: String? = null,
    val boxOffice: String? = null,
    val production: String? = null,
    val website: String? = null,
    val response: String? = null,
    val ratings: List<Rating> = emptyList(),
) {
    companion object {
        fun from(
            omdb: OmdbMovieDetailsResponse,
            internalRating: InternalRating? = null,
        ): MovieDetailsResponse =
            MovieDetailsResponse(
                title = omdb.title,
                year = omdb.year,
                rated = omdb.rated,
                released = omdb.released,
                runtime = omdb.runtime,
                genre = omdb.genre,
                director = omdb.director,
                writer = omdb.writer,
                actors = omdb.actors,
                plot = omdb.plot,
                language = omdb.language,
                country = omdb.country,
                awards = omdb.awards,
                poster = omdb.poster,
                ratings = listOfNotNull(internalRating) + omdb.ratings.map { ExternalRating.from(it) },
                imdbRating = omdb.imdbRating,
                imdbVotes = omdb.imdbVotes,
                imdbID = omdb.imdbID,
                type = omdb.type,
                dvd = omdb.dvd,
                metascore = omdb.metascore,
                production = omdb.production,
                response = omdb.response,
                website = omdb.website,
                boxOffice = omdb.boxOffice,
            )
    }
}

sealed interface Rating {
    val source: String
    val value: String
}

@Serializable
@SerialName("EXTERNAL")
data class ExternalRating(
    override val source: String,
    override val value: String,
) : Rating {
    companion object {
        fun from(rating: OmdbRating): ExternalRating =
            ExternalRating(
                source = rating.source,
                value = rating.value,
            )
    }
}

@Serializable
@SerialName("INTERNAL")
data class InternalRating(
    override val value: String,
    val ratesCount: Int,
) : Rating {
    override val source = "INTERNAL"
}
