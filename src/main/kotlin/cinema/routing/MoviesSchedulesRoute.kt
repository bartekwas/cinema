package com.bwasik.cinema.routing

import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.movieSchedulesRoute(
    movieScheduleService: MovieScheduleService
) {
    route("/movie_schedules") {
        get {
            val movies = movieScheduleService.getAllMoviesWithSchedules()
            call.respond(movies)
        }

        get("{movie_id}") {
            val movieId = call.parameters["movie_id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            val movie = movieScheduleService.getMoviesWithSchedules(movieId) ?: return@get call.respond(HttpStatusCode.NotFound, "Movie ID not found")
            call.respond(movie)
        }
        patch {
            val movieWithSchedules = call.receive<MovieWithSchedules>()
            movieScheduleService.updateMovieWithSchedules(movieWithSchedules)
            call.respond("Movie schedulkeadded successfully!")
        }

        delete("{movie_id}") {
            val movieId = call.parameters["movie_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            movieScheduleService.deleteMovieSchedule(movieId)
            call.respond("Movie schedule deleted successfully!")
        }
    }

    route("/schedule/{schedule_id}") {
        delete {
            val scheduleId = call.parameters["schedule_id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest, "Movie ID is required")
            movieScheduleService.deleteSchedule(scheduleId)
            call.respond("Schedule deleted successfully!")
        }
    }
}