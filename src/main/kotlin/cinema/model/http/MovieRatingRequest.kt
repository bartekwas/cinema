package com.bwasik.cinema.model.http

import BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class MovieRatingRequest(
    val movieId: String,
    @Serializable(with = BigDecimalSerializer::class)
    val rate: BigDecimal,
) {
    init {
        require(rate > BigDecimal.ZERO) { "Rating must be greater than zero" }
        require(rate <= BigDecimal.valueOf(10)) { "Rating must be max 10" }
    }
}
