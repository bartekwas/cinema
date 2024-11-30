package com.bwasik.plugins

import com.bwasik.security.jwt.service.ROLE_CLAIM
import com.bwasik.security.jwt.service.USERID_CLAIM
import com.bwasik.security.user.model.UserRole
import com.bwasik.utils.safeValueOf
import com.bwasik.utils.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

class RoleBasedAuthorizationConfiguration {
    var roles: Set<UserRole> = emptySet()
}

val RoleBasedAuthorizationPlugin = createRouteScopedPlugin(
    name = "RoleBasedAuthorizationPlugin",
    createConfiguration = ::RoleBasedAuthorizationConfiguration
){
    val roles = pluginConfig.roles
    pluginConfig.apply{
        on(AuthenticationChecked){ call ->
            if(!roles.contains(call.getRole())){
                call.respond(
                    message = HttpStatusCode.Forbidden,
                    typeInfo = typeInfo<HttpStatusCode>()
                )
            }
        }
    }
}

 fun ApplicationCall.getRole() =
    this.principal<JWTPrincipal>()?.payload?.getClaim(ROLE_CLAIM)?.asString()?.safeValueOf<UserRole>()
 fun ApplicationCall.principalId() =
    this.principal<JWTPrincipal>()?.payload?.getClaim(USERID_CLAIM)?.asString()?.toUUID()


fun Route.requireRoles(
    vararg requiredRoles: UserRole,
    route: Route.() -> Unit
){
    install(RoleBasedAuthorizationPlugin) { roles = requiredRoles.toSet() }
    route()
}