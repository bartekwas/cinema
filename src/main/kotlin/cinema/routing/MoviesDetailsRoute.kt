package com.bwasik.cinema.routing

import com.bwasik.cinema.model.http.MovieRatingRequest
import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import com.bwasik.plugins.principalId
import com.bwasik.plugins.requireRoles
import com.bwasik.security.jwt.model.http.LoginRequest
import com.bwasik.security.user.model.UserRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.math.BigDecimal

fun Route.movieDetailsRoute(
    movieDetailsService: MovieDetailsService
) {
    route("/movie_details/{movieId}") {
        get {
            val movieId =
                call.parameters["movieId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieDetails = movieDetailsService.getMovieDetails(movieId)

            call.respond(
                message = movieDetails
            )
        }
    }

    authenticate {
        requireRoles(UserRole.USER) {
            route("/movie_details/rates") {

                post {
                    val movieRatingRequest = call.receive<MovieRatingRequest>()
                    val principal = call.principalId() ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        "Log in to review the movie"
                    )

                    val movieDetails = movieDetailsService.postMovieRatings(movieRatingRequest, principal)

                    call.respond(
                        message = HttpStatusCode.Created
                    )
                }
            }
        }
    }
}