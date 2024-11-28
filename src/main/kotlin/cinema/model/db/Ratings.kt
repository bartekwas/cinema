package com.bwasik.cinema.model.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Ratings : Table("ratings") {
    val movieId = varchar("movie_id", 50).references(Movies.id)
    val userId = varchar("user_id", 50)
    val rate = decimal("rate", precision = 5, scale = 2)
    val insertDate = datetime("insert_date").defaultExpression(CurrentDateTime)
    val lastUpdateDate = datetime("last_update_date").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(movieId, userId, name = "PK_Ratings_Movie_User") // Composite primary key
}