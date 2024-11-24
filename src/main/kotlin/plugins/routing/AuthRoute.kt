package com.bwasik.plugins.routing

import com.bwasik.security.jwt.model.http.LoginRequest
import com.bwasik.security.jwt.model.http.LoginResponse
import com.bwasik.security.user.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

fun Route.loginRoute(userService: UserService) {
    route("/api/login") {
        post {
            val loginRequest = call.receive<LoginRequest>()

            userService.authenticate(loginRequest)?.let {
                call.respond(
                    message = it,
                    typeInfo = typeInfo<LoginResponse>()
                )
            } ?: call.respond(
                message = HttpStatusCode.Unauthorized,
                typeInfo = typeInfo<HttpStatusCode>()
            )
        }
    }
}