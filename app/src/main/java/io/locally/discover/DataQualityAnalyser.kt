package io.locally.discover

import android.location.Location

class DataQualityAnalyser {

    private var lastLocation: Location? = null

    private var lastDataQuality: Double = 0.0

    fun dataQuality(location: Location): Double {

        val locationChange = lastLocation?.distanceTo(location) ?: 0.0

        lastLocation = location

        if (locationChange == 0.0) {
            lastDataQuality = 0.0

            return lastDataQuality
        }

        lastDataQuality = when (location.accuracy) {
            in 0..100 -> {
                val scoreForAccuracy = 0.5 - (location.accuracy / 100 * 0.5)
                0.5 + scoreForAccuracy
            }

            else -> 0.5
        }

        return lastDataQuality
    }
}