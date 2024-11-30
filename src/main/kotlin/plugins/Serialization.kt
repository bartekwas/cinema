package com.bwasik.plugins

import com.bwasik.cinema.model.http.ExternalRating
import com.bwasik.cinema.model.http.InternalRating
import com.bwasik.cinema.model.http.Rating
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun Application.installSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule =
                    SerializersModule {
                        polymorphic(Rating::class) {
                            subclass(InternalRating::class, InternalRating.serializer())
                            subclass(ExternalRating::class, ExternalRating.serializer())
                        }
                    }
                prettyPrint = true
                ignoreUnknownKeys = true
            },
        )
    }
}
