package com.bwasik.plugins

import com.bwasik.utils.DatabaseFactory
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

fun Application.setupDatabase() {
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.init()
}
