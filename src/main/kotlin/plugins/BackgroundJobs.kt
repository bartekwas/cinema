package com.bwasik.plugins

import com.bwasik.cinema.service.RateAggregatorService
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.setupBackgroundJobs() {
    val aggregatorService: RateAggregatorService by inject()
    aggregatorService.start()
}