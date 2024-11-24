package com.bwasik.plugins.routing

import com.bwasik.security.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.installRouting(){
    val userService: UserService by inject()
    routing {
        loginRoute(userService)
        userRoute(userService)
    }
}