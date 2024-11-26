package com.bwasik.cinema.routing

import com.bwasik.cinema.model.db.Movies
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.cinemaRoute(
    movieDetailsService: MovieDetailsService,
    movieScheduleService: MovieScheduleService
) {
    route("/api/movie/{movieId}") {
        get {
            val movieId = call.parameters["movieId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieDetails = movieDetailsService.getMovieDetails(movieId)

            call.respond(
                message = movieDetails
            )
        }
    }

    route("/api/movies") {
        get {
            val movies = transaction {
                Movies.selectAll().map {
                    mapOf(
                        "id" to it[Movies.id],
                        "title" to it[Movies.title],
                    )
                }
            }
            call.respond(movies)
        }
    }

    route("/api/movies/{id}/{name}") {
        post {
            val movieId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movieName = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            transaction {
                Movies.insert {
                    it[id] = movieId
                    it[title] = movieName
                }
            }
            call.respond("Movie added successfully!")
        }
    }
}