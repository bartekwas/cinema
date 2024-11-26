package com.bwasik

import com.bwasik.koin.authModule
import com.bwasik.koin.cinemaModule
import com.bwasik.koin.omdbModule
import com.bwasik.koin.userModule
import com.bwasik.plugins.installSecurity
import com.bwasik.plugins.installSerialization
import com.bwasik.plugins.routing.installRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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
        modules(cinemaModule)
    }
    installSerialization()
    installSecurity()
    installRouting()
}