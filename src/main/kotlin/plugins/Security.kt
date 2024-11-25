package com.bwasik.plugins

import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.jwt.service.extractUsername
import com.bwasik.security.user.service.UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.ktor.ext.inject
import java.util.*

fun Application.installSecurity() {
    val jwtService: JWTService by inject()
    val userService: UserService by inject()

    authentication {
        jwt {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                credential.extractUsername()?.let {
                    userService.findById(it)
                }?.let {
                    jwtService.customValidator(credential)
                }
            }
        }
    }
}

