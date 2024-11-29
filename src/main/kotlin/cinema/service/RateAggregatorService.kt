package com.bwasik.cinema.service

import com.bwasik.cinema.repository.AverageRateRepository
import kotlinx.coroutines.*

class RateAggregatorService(
    private val averageRateRepository: AverageRateRepository
) {
    fun start(){
        val job = Job()
        CoroutineScope(Dispatchers.Default + job).launch {
            while (isActive) {
                averageRateRepository.calculateAndSaveAverageRates()
                delay(30 * 1000)
            }
        }
    }
}