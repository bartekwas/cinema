package com.bwasik.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.installRouting(){
    routing {
        get("/") {
            call.respondText("Hello, Ktor!", ContentType.Text.Plain)
        }
    }
}