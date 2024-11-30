package com.bwasik.cinema.repository

import com.bwasik.cinema.model.db.Movies
import com.bwasik.cinema.model.db.Schedules
import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.model.http.Schedule
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.ZoneId

class MovieSchedulesRepository {
    fun updateMovieWithSchedules(movieSchedule: MovieWithSchedules) {
        transaction {
            val foundMovie = Movies.select { Movies.id eq movieSchedule.id }.singleOrNull()
            if (foundMovie == null) {
                Movies.insert {
                    it[id] = movieSchedule.id
                    it[title] = movieSchedule.title
                }
            } else {
                movieSchedule.title?.let { movieTitle ->
                    Movies.update({ Movies.id eq movieSchedule.id }) {
                        it[title] = movieTitle
                    }
                }
            }

            val existingSchedules =
                Schedules
                    .select { Schedules.movieId eq movieSchedule.id }
                    .map { it[Schedules.dateTime] to it[Schedules.price] }

            val newSchedules =
                movieSchedule.schedules.filter { schedule ->
                    schedule.dateTime.toLocalDateTime() !in existingSchedules.map { it.first }
                }

            newSchedules.forEach { schedule ->
                Schedules.insert {
                    it[movieId] = movieSchedule.id
                    it[dateTime] = schedule.dateTime.toLocalDateTime()
                    it[price] = schedule.price
                }
            }
        }
    }

    fun deleteSchedule(scheduleId: Long) {
        transaction {
            Schedules.deleteWhere { Schedules.id eq scheduleId }
        }
    }

    fun getAllMoviesWithSchedules(): List<MovieWithSchedules> =
        transaction {
            // Fetch all movies
            val movies =
                Movies.selectAll().map { movieRow ->
                    val movieId = movieRow[Movies.id]
                    val movieTitle = movieRow[Movies.title]

                    // Fetch schedules for the current movie
                    val schedules =
                        Schedules
                            .select { Schedules.movieId eq movieId }
                            .map { scheduleRow ->
                                Schedule(
                                    id = scheduleRow[Schedules.id],
                                    dateTime = scheduleRow[Schedules.dateTime].atZone(ZoneId.systemDefault()),
                                    price = scheduleRow[Schedules.price],
                                )
                            }

                    MovieWithSchedules(
                        id = movieId,
                        title = movieTitle,
                        schedules = schedules,
                    )
                }
            movies
        }

    fun getMoviesWithSchedules(movieId: String): MovieWithSchedules? =
        transaction {
            Movies.select { Movies.id eq movieId }.singleOrNull()?.let {
                val movieTitle = it[Movies.title]
                val schedules =
                    Schedules
                        .select { Schedules.movieId eq movieId }
                        .map { scheduleRow ->
                            Schedule(
                                id = scheduleRow[Schedules.id],
                                dateTime = scheduleRow[Schedules.dateTime].atZone(java.time.ZoneId.systemDefault()),
                                price = scheduleRow[Schedules.price],
                            )
                        }
                MovieWithSchedules(
                    id = movieId,
                    title = movieTitle,
                    schedules = schedules,
                )
            }
        }

    fun deleteMovieSchedule(movieId: String) {
        transaction {
            Movies.deleteWhere { Movies.id eq movieId }
            Schedules.deleteWhere { Schedules.movieId eq movieId }
        }
    }
}
