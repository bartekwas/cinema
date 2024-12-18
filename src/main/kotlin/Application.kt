package com.bwasik

import com.bwasik.koin.authModule
import com.bwasik.koin.cinemaModule
import com.bwasik.koin.dbModule
import com.bwasik.koin.omdbModule
import com.bwasik.koin.redisModule
import com.bwasik.koin.userModule
import com.bwasik.plugins.installSecurity
import com.bwasik.plugins.installSerialization
import com.bwasik.plugins.routing.installRouting
import com.bwasik.plugins.setupBackgroundJobs
import com.bwasik.plugins.setupDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application.Kt")

fun main() {
    try {
        embeddedServer(
            Netty,
            port = System.getenv("PORT")?.toInt() ?: 8080,
            module = Application::cinemaApp,
        ).start(wait = true)
    } catch (t: Throwable) {
        logger.error(t.message, t)
        throw t
    }
}

fun Application.cinemaApp() {
    install(Koin) {
        modules(userModule)
        modules(authModule)
        modules(omdbModule)
        modules(dbModule)
        modules(redisModule)
        modules(cinemaModule)
    }
    setupDatabase()
    setupBackgroundJobs()
    installSerialization()
    installSecurity()
    installRouting()
}
