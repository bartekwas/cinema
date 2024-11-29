package com.bwasik.cinema.model.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Schedules : Table("schedules") {
    val id = integer("id").autoIncrement()
    val movieId = varchar("movie_id", 50).references(Movies.id)
    val dateTime = datetime("date_time")
    val price = decimal("price", 10, 2)

    override val primaryKey = PrimaryKey(id)
}