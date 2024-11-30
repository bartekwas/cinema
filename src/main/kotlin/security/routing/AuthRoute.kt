package com.bwasik.security.routing

import com.bwasik.security.jwt.model.http.LoginRequest
import com.bwasik.security.jwt.model.http.LoginResponse
import com.bwasik.security.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.reflect.typeInfo

fun Route.loginRoute(userService: UserService) {
    route("/login") {
        post {
            val loginRequest = call.receive<LoginRequest>()

            userService.authenticate(loginRequest)?.let {
                call.respond(
                    message = it,
                    typeInfo = typeInfo<LoginResponse>(),
                )
            } ?: call.respond(
                message = HttpStatusCode.Unauthorized,
                typeInfo = typeInfo<HttpStatusCode>(),
            )
        }
    }
}
