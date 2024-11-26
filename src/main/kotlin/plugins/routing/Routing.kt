package com.bwasik.plugins.routing

import com.bwasik.cinema.routing.cinemaRoute
import com.bwasik.cinema.service.CinemaService
import com.bwasik.security.routing.loginRoute
import com.bwasik.security.routing.userRoute
import com.bwasik.security.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.installRouting(){
    val userService: UserService by inject()
    val cinemaService: CinemaService by inject()
    routing {
        loginRoute(userService)
        userRoute(userService)
        cinemaRoute(cinemaService)
    }
}