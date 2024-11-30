package com.bwasik.koin

import com.bwasik.cinema.repository.AverageRateRepository
import com.bwasik.cinema.repository.MovieDetailsRepository
import com.bwasik.cinema.repository.MovieSchedulesRepository
import com.bwasik.cinema.service.MovieDetailsService
import com.bwasik.cinema.service.MovieScheduleService
import com.bwasik.cinema.service.RateAggregatorService
import com.bwasik.omdb.KtorClientProvider
import com.bwasik.omdb.OmdbClient
import com.bwasik.utils.RedisCache
import com.bwasik.security.jwt.JWTConfig
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.security.user.service.UserService
import com.bwasik.utils.DatabaseFactory
import com.bwasik.utils.envVariable
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
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

val redisModule = module {
    single {
        RedisClient.create("redis.url".envVariable())
    }
    single<StatefulRedisConnection<String, String>> {
        get<RedisClient>().connect()
    }
    single {
        RedisCache(
            redisConnection = get()
        )
    }
}

val cinemaModule = module {
    single {
        MovieSchedulesRepository
    }
    single {
        MovieDetailsRepository
    }
    single {
        AverageRateRepository
    }
    single {
        RateAggregatorService(
            averageRateRepository = get()
        )
    }
    single {
        MovieDetailsService(
            omdbClient = get(),
            movieDetailsRepository = get(),
            cache = get()
        )
    }
    single {
        MovieScheduleService(
            movieScheduleRepository = get()
        )
    }
}