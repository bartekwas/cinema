package com.bwasik.koin

import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import com.bwasik.omdb.KtorClientProvider
import com.bwasik.omdb.OmdbClient
import com.bwasik.security.jwt.JWTConfig
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.security.user.service.UserService
import com.bwasik.utils.DatabaseFactory
import com.bwasik.utils.envVariable
import org.koin.dsl.module

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

val dbModule = module {
    single {
        DatabaseFactory(
            url = "database.url".envVariable(),
            driver = "database.driver".envVariable(),
            user = "database.user".envVariable(),
            password = "database.password".envVariable(),
        ).apply {
            init()
        }
    }
}

val cinemaModule = module {
    single {
        MovieDetailsService(
            omdbClient = get()
        )
    }
    single {
        MovieScheduleService()
    }
}