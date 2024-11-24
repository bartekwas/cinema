package com.bwasik

import com.bwasik.koin.userModule
import com.bwasik.plugins.installRouting
import com.bwasik.plugins.installSerialization
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
    installSerialization()
    install(Koin) {
        modules(userModule)
    }
    installRouting()
}