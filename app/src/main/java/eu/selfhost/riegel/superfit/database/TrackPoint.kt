package eu.selfhost.riegel.superfit.database

data class TrackPoint(
        val latitude: Double,
        val longitude: Double,
        val elevation: Float,
        val time: Long,
        val precision: Float,
        val speed: Float,
        val heartRate: Int
)