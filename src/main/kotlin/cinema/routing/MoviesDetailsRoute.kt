package com.bwasik.cinema.routing

import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.movieDetailsRoute(
    movieDetailsService: MovieDetailsService
) {
    route("/movie_details/{movieId}") {
        get {
            val movieId = call.parameters["movieId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieDetails = movieDetailsService.getMovieDetails(movieId)

            call.respond(
                message = movieDetails
            )
        }
    }

    route("/movie_details/rates/{movieId}") {
        post {
            val movieId = call.parameters["movieId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieDetails = movieDetailsService.getMovieDetails(movieId)

            call.respond(
                message = movieDetails
            )
        }
    }
}