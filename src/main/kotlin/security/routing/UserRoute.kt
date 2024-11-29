package com.bwasik.security.routing

import com.bwasik.plugins.requireRoles
import com.bwasik.security.user.model.User
import com.bwasik.security.user.model.UserRole
import com.bwasik.security.user.model.http.UserCreationRequest
import com.bwasik.security.user.model.http.UserCreationResponse
import com.bwasik.security.user.service.UserService
import com.bwasik.utils.safeValueOf
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userService: UserService) {
    route("/user") {
        post {
            val userRequest = call.receive<UserCreationRequest>()
            val createdUser = userService.insert(
                userRequest = userRequest
            ) ?: return@post call.respond(HttpStatusCode.BadRequest)

            call.response.header(
                name = "id",
                value = createdUser.id.toString()
            )
            call.respond(
                message = HttpStatusCode.Created
            )
        }

        // just for test purposes
        authenticate {
            requireRoles(UserRole.ADMIN) {
                get {
                    call.respond(
                        message = userService.findAll()
                    )
                }
            }
        }
    }
}