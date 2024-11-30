package com.bwasik.cinema.repository

import com.bwasik.cinema.model.db.Ratings
import com.bwasik.cinema.model.http.MovieRatingRequest
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class MovieDetailsRepository {

    fun postMovieRatings(movieRatingRequest: MovieRatingRequest, principalId: String) {
        transaction {
            val existingRating = Ratings.select {
                (Ratings.movieId eq movieRatingRequest.movieId) and (Ratings.userId eq principalId)
            }.singleOrNull()

            if (existingRating == null) {
                Ratings.insert {
                    it[movieId] = movieRatingRequest.movieId
                    it[userId] = principalId
                    it[rate] = movieRatingRequest.rate
                    it[insertDate] = LocalDateTime.now()
                    it[lastUpdateDate] = LocalDateTime.now()
                }
            } else {
                // Update the existing review
                Ratings.update({
                    (Ratings.movieId eq movieRatingRequest.movieId) and (Ratings.userId eq principalId)
                }) {
                    it[rate] = movieRatingRequest.rate
                    it[lastUpdateDate] = LocalDateTime.now()
                }
            }
        }
    }
}