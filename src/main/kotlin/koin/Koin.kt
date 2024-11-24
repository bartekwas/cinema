package com.bwasik.koin

import com.bwasik.security.jwt.JWTConfig
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.security.user.service.UserService
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.koin.dsl.module

val userModule = module {
    single { UserRepository() }
    single { UserService(userRepository = get(), jwtService = get()) }
}

val authModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
    single { JWTService(
        JWTConfig(
            issuer = config.property("jwt.issuer").getString(),
            secret = config.property("jwt.secret").getString(),
            audience = config.property("jwt.audience").getString(),
            realm = config.property("jwt.realm").getString(),
        )
    )
    }
}