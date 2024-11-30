package com.bwasik.cinema.service

import com.bwasik.cinema.repository.AverageRateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RateAggregatorService(
    private val averageRateRepository: AverageRateRepository,
) {
    fun start() {
        val job = Job()
        CoroutineScope(Dispatchers.Default + job).launch {
            while (isActive) {
                averageRateRepository.calculateAndSaveAverageRates()
                delay(30 * 1000)
            }
        }
    }
}
