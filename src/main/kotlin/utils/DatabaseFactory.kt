package com.bwasik.utils

import com.bwasik.cinema.model.db.*
import com.bwasik.security.user.model.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class DatabaseFactory(
    private val url: String,
    private val driver: String,
    private val user: String,
    private val password: String
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun init() {
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )
        transaction {
            SchemaUtils.create(Users, Movies, Schedules, Ratings, AverageRate)
        }
        logger.info("Connected to PostgreSQL Database")
    }
}