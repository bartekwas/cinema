package com.bwasik.plugins

import com.bwasik.security.jwt.service.ROLE_CLAIM
import com.bwasik.security.jwt.service.USERID_CLAIM
import com.bwasik.security.user.model.UserRole
import com.bwasik.utils.safeValueOf
import com.bwasik.utils.toUUID
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.util.reflect.typeInfo

class RoleBasedAuthorizationConfiguration {
    var roles: Set<UserRole> = emptySet()
}

val RoleBasedAuthorizationPlugin =
    createRouteScopedPlugin(
        name = "RoleBasedAuthorizationPlugin",
        createConfiguration = ::RoleBasedAuthorizationConfiguration,
    ) {
        val roles = pluginConfig.roles
        pluginConfig.apply {
            on(AuthenticationChecked) { call ->
                if (!roles.contains(call.getRole())) {
                    call.respond(
                        message = HttpStatusCode.Forbidden,
                        typeInfo = typeInfo<HttpStatusCode>(),
                    )
                }
            }
        }
    }

fun ApplicationCall.getRole() =
    this
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(ROLE_CLAIM)
        ?.asString()
        ?.safeValueOf<UserRole>()

fun ApplicationCall.principalId() =
    this
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(USERID_CLAIM)
        ?.asString()
        ?.toUUID()

fun Route.requireRoles(
    vararg requiredRoles: UserRole,
    route: Route.() -> Unit,
) {
    install(RoleBasedAuthorizationPlugin) { roles = requiredRoles.toSet() }
    route()
}
