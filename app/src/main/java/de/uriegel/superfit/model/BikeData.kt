package de.uriegel.superfit.model

data class BikeData(
    val velocity: Float,
    val distance: Float,
    val maxVelocity: Float,
    val crankCyclesPerSecs: Int,
    val duration: Int,
    val averageVelocity: Float
)
