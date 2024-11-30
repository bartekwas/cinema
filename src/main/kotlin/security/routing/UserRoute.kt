package com.bwasik.security.routing

import com.bwasik.plugins.requireRoles
import com.bwasik.security.user.model.UserRole
import com.bwasik.security.user.model.http.UserCreationRequest
import com.bwasik.security.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoute(userService: UserService) {
    route("/user") {
        post {
            val userRequest = call.receive<UserCreationRequest>()
            val createdUser =
                userService.insert(
                    userRequest = userRequest,
                ) ?: return@post call.respond(HttpStatusCode.BadRequest)

            call.response.header(
                name = "id",
                value = createdUser.id.toString(),
            )
            call.respond(
                message = HttpStatusCode.Created,
            )
        }

        // just for test purposes
        authenticate {
            requireRoles(UserRole.ADMIN) {
                get {
                    call.respond(
                        message = userService.findAll(),
                    )
                }
            }
        }
    }
}
