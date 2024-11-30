package com.bwasik.cinema.routing

import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.service.MovieScheduleService
import com.bwasik.plugins.requireRoles
import com.bwasik.security.user.model.UserRole
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.movieSchedulesRoute(movieScheduleService: MovieScheduleService) {
    route("/movie_schedules") {
        get {
            val movies = movieScheduleService.getAllMoviesWithSchedules()
            call.respond(movies)
        }

        get("{movie_id}") {
            val movieId =
                call.parameters["movie_id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Movie ID is required",
                )
            val movie =
                movieScheduleService.getMoviesWithSchedules(movieId) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "Movie ID not found",
                )
            call.respond(movie)
        }
        authenticate {
            requireRoles(UserRole.ADMIN) {
                patch {
                    val movieWithSchedules = call.receive<MovieWithSchedules>()
                    movieScheduleService.updateMovieWithSchedules(movieWithSchedules)
                    call.respond("Movie schedulkeadded successfully!")
                }

                delete("{movie_id}") {
                    val movieId =
                        call.parameters["movie_id"] ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            "Movie ID is required",
                        )
                    movieScheduleService.deleteMovieSchedule(movieId)
                    call.respond("Movie schedule deleted successfully!")
                }
            }
        }
    }
    authenticate {
        requireRoles(UserRole.ADMIN) {
            route("/schedule/{schedule_id}") {
                delete {
                    val scheduleId =
                        call.parameters["schedule_id"]?.toLong() ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            "Movie ID is required",
                        )
                    movieScheduleService.deleteSchedule(scheduleId)
                    call.respond("Schedule deleted successfully!")
                }
            }
        }
    }
}
