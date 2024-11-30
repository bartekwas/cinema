package com.bwasik.cinema.model.db

import org.jetbrains.exposed.sql.Table

object Movies : Table("movies") {
    val id = varchar("id", 50)
    val title = varchar("title", 255).nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Movies_Id")
}
