package com.bwasik.plugins.routing

import com.bwasik.cinema.routing.movieDetailsRoute
import com.bwasik.cinema.routing.movieSchedulesRoute
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import com.bwasik.security.routing.loginRoute
import com.bwasik.security.routing.userRoute
import com.bwasik.security.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import org.koin.ktor.ext.inject

fun Application.installRouting() {
    val userService: UserService by inject()
    val movieDetailsService: MovieDetailsService by inject()
    val movieScheduleService: MovieScheduleService by inject()
    routing {
        route("api"){
            loginRoute(userService)
            userRoute(userService)
            movieSchedulesRoute(movieScheduleService = movieScheduleService)
            movieDetailsRoute(movieDetailsService = movieDetailsService)
        }
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
    }
}