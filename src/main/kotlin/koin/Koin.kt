package com.bwasik.koin

import com.bwasik.cinema.service.CinemaService
import com.bwasik.omdb.KtorClientProvider
import com.bwasik.omdb.OmdbClient
import com.bwasik.security.jwt.JWTConfig
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.security.user.service.UserService
import com.bwasik.utils.envVariable
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.koin.dsl.module
import kotlin.math.sin

val userModule = module {
    single { UserRepository() }
    single { UserService(userRepository = get(), jwtService = get()) }
}

val authModule = module {
    single {
        JWTService(
            JWTConfig(
                issuer = "jwt.issuer".envVariable(),
                secret = "jwt.secret".envVariable(),
                audience = "jwt.audience".envVariable(),
                realm = "jwt.realm".envVariable(),
            )
        )
    }
}

val omdbModule = module {
    single {
        KtorClientProvider()
    }
    single {
        OmdbClient(
            clientProvider = get(),
            apiKey = "omdb.apiKey".envVariable(),
            baseUrl = "omdb.baseUrl".envVariable(),
        )
    }
}

val cinemaModule = module {
    single {
        CinemaService(
            omdbClient = get()
        )
    }
}