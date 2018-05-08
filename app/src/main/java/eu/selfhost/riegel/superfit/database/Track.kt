package eu.selfhost.riegel.superfit.database

data class Track(
        val trackNr: Int,
        val name: String,
        val distance: Float,
        val averageSpeed: Float,
        val time: Long
)