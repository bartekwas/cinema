package com.bwasik.cinema.model.http

import com.bwasik.omdb.model.OmdbMovieDetailsResponse
import kotlinx.serialization.SerialName
import com.bwasik.omdb.model.Rating as OmdbRating
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsResponse(
    val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String,
    val language: String,
    val country: String,
    val awards: String,
    val poster: String,
    val metascore: String,
    val imdbRating: String,
    val imdbVotes: String,
    val imdbID: String,
    val type: String,
    val dvd: String,
    val boxOffice: String,
    val production: String,
    val website: String,
    val response: String,
    val ratings: List<Rating>
){
    companion object {
        fun from(omdb: OmdbMovieDetailsResponse, internalRating: InternalRating? = null): MovieDetailsResponse {
            return MovieDetailsResponse(
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
                boxOffice = omdb.boxOffice
            )
        }
    }
}


sealed interface Rating{
    val source: String
    val value: String
}

@Serializable

@SerialName("EXTERNAL")
data class ExternalRating(
    override val source: String,
    override val value: String
) : Rating {
    companion object{
        fun from(rating: OmdbRating): ExternalRating =
            ExternalRating(
                source = rating.source,
                value = rating.value
            )
    }
}

@Serializable
@SerialName("INTERNAL")
data class InternalRating(
    override val value: String,
    val ratesCount: Int,
): Rating {
    override val source = "INTERNAL"
}