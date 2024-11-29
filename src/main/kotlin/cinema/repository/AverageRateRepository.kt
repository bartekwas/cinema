package com.bwasik.cinema.repository

import com.bwasik.cinema.model.db.AverageRate
import com.bwasik.cinema.model.db.Ratings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

object AverageRateRepository {

    fun calculateAndSaveAverageRates() {
        transaction {
            val averageRates = Ratings
                .slice(Ratings.movieId, Ratings.rate.avg(), Ratings.rate.count())
                .selectAll()
                .groupBy(Ratings.movieId)
                .map { row ->
                    val movieId = row[Ratings.movieId]
                    val averageRate = row[Ratings.rate.avg()] ?: BigDecimal.ZERO
                    val count = row[Ratings.rate.count()] ?: 0
                    Triple(movieId, averageRate, count)
                }
            averageRates.forEach { (movieId, avgRate, ratesCount) ->
                val existingRecord = AverageRate.select { AverageRate.movieId eq movieId }
                    .singleOrNull()

                if (existingRecord == null) {
                    AverageRate.insert {
                        it[AverageRate.movieId] = movieId
                        it[AverageRate.rate] = avgRate
                        it[AverageRate.count] = ratesCount.toInt()
                        it[AverageRate.lastUpdate] = LocalDateTime.now()
                    }
                } else {
                    AverageRate.update({ AverageRate.movieId eq movieId }) {
                        it[rate] = avgRate
                        it[count] = ratesCount.toInt()
                        it[lastUpdate] = LocalDateTime.now()
                    }
                }
            }
        }
    }
}