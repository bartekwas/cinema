package com.bwasik.cinema.model.http

import BigDecimalSerializer
import com.bwasik.utils.UUIDSerializer
import com.bwasik.utils.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.ZonedDateTime

@Serializable
data class MovieWithSchedules(
    val id: String,
    val title: String? = null,
    val schedules: List<Schedule>
){
    init {
        require(id.isNotBlank()) { "Movie ID must not be blank" }
        title?.let {
            require(it.isNotBlank()) { "Movie title must not be blank if provided" }
        }
        require(schedules.isNotEmpty()) { "Schedules must not be empty" }
    }
}

@Serializable
data class Schedule(
    val id: Long? = null,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val dateTime: ZonedDateTime,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal
){
    init {
        require(price > BigDecimal.ZERO) { "Price must be greater than zero" }
    }
}
