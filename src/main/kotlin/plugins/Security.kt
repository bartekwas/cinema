package com.bwasik.plugins

import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.jwt.service.extractUsername
import com.bwasik.security.user.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.inject

fun Application.installSecurity() {
    val jwtService: JWTService by inject()
    val userService: UserService by inject()

    authentication {
        jwt {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                credential
                    .extractUsername()
                    ?.let {
                        userService.findById(it)
                    }?.let {
                        jwtService.customValidator(credential)
                    }
            }
        }
    }
}
