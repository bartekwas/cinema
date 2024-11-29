package com.bwasik.cinema.service

import com.bwasik.cinema.model.http.MovieWithSchedules
import com.bwasik.cinema.repository.MovieSchedulesRepository

class MovieScheduleService(
    private val movieScheduleRepository: MovieSchedulesRepository
) {

    fun updateMovieWithSchedules(movieSchedule: MovieWithSchedules) =
        movieScheduleRepository.updateMovieWithSchedules(movieSchedule)


    fun deleteSchedule(scheduleId: Long) =
        movieScheduleRepository.deleteSchedule(scheduleId)


    fun getAllMoviesWithSchedules(): List<MovieWithSchedules> =
        movieScheduleRepository.getAllMoviesWithSchedules()


    fun getMoviesWithSchedules(movieId: String): MovieWithSchedules? =
        movieScheduleRepository.getMoviesWithSchedules(movieId)

    fun deleteMovieSchedule(movieId: String) =
        movieScheduleRepository.deleteMovieSchedule(movieId)
}

