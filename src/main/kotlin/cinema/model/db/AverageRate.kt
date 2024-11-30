package com.bwasik.cinema.model.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object AverageRate : Table("average_rate") {
    val movieId = varchar("movie_id", 50)
    val rate = decimal("rate", precision = 5, scale = 2)
    val count = integer("count")
    val lastUpdate = datetime("last_update").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(movieId, name = "PK_AverageRate_MovieId")
}
