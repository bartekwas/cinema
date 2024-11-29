package com.bwasik.cinema.repository

import com.bwasik.cinema.model.db.AverageRate
import com.bwasik.cinema.model.db.Ratings
import com.bwasik.cinema.model.http.InternalRating
import com.bwasik.cinema.model.http.MovieRatingRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object MovieDetailsRepository {

    fun getAverageRate(id: String): InternalRating? {
        return transaction {
            AverageRate
                .select { AverageRate.movieId eq id }
                .map { row ->
                    InternalRating(
                        value = row[AverageRate.rate].toString()+"/10",
                        ratesCount = row[AverageRate.count]
                    )
                }
                .singleOrNull()
        }
    }

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