package com.bwasik

import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.koin.*
import com.bwasik.plugins.installSecurity
import com.bwasik.plugins.installSerialization
import com.bwasik.plugins.routing.installRouting
import com.bwasik.utils.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin


fun main() {
    embeddedServer(Netty, port = 8080) {
        setup()
    }.start(wait = true)
}

fun Application.setup() {
    install(Koin) {
        modules(userModule)
        modules(authModule)
        modules(omdbModule)
        modules(dbModule)
        modules(cinemaModule)
    }
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.init()
    installSerialization()
    installSecurity()
    installRouting()
}