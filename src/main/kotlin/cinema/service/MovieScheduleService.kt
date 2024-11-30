package com.bwasik.cinema.service

import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.repository.MovieSchedulesRepository
import com.bwasik.utils.RedisCache
import kotlin.time.Duration.Companion.minutes

private const val NAMESPACE = "SCHEDULES"
private val CACHE_TIME = 2.minutes.inWholeSeconds


class MovieScheduleService(
    private val movieScheduleRepository: MovieSchedulesRepository,
    private val cache: RedisCache,
) {

    fun updateMovieWithSchedules(movieSchedule: MovieWithSchedules) =
        movieScheduleRepository.updateMovieWithSchedules(movieSchedule)


    fun deleteSchedule(scheduleId: Long) =
        movieScheduleRepository.deleteSchedule(scheduleId)


    fun getAllMoviesWithSchedules(): List<MovieWithSchedules> =
        cache.cached("ALL_SCHEDULES", NAMESPACE, CACHE_TIME) {
            movieScheduleRepository.getAllMoviesWithSchedules()
        }


    fun getMoviesWithSchedules(movieId: String): MovieWithSchedules? =
        cache.cached(movieId, NAMESPACE, CACHE_TIME) {
            movieScheduleRepository.getMoviesWithSchedules(movieId)
        }

    fun deleteMovieSchedule(movieId: String) =
        movieScheduleRepository.deleteMovieSchedule(movieId)
}

