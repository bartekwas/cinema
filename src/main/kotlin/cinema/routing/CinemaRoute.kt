package com.bwasik.cinema.routing

import com.bwasik.cinema.service.CinemaService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cinemaRoute(cinemaService: CinemaService) {
    route("/api/movie/{movieId}") {
        get {
            val movieId = call.parameters["movieId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieDetails = cinemaService.getMovieDetails(movieId)

            call.respond(
                message = movieDetails
            )
        }
    }
}