package de.uriegel.superfit.extensions

import android.util.Xml
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import java.io.OutputStream

fun OutputStream.exportToGpx(name: String, track: Track, trackPoints: Array<TrackPoint>) {
    val serializer = Xml.newSerializer()
    serializer.setOutput(this, "UTF-8")

    serializer.document("UTF-8", true) {
        element(null, "gpx") {
            attribute(null,"version", "1.1")
            element(null, "trk") {
                element(null, "info") {
                    element(null, "name", name)
                    element(null, "distance", "${track.distance}")
                    element(null, "duration", "${track.duration}")
                    element(null, "date", track.time.formatRfc3339())
                    element(null, "averageSpeed", "${track.averageSpeed}")
                    element(null, "trackPoints", "${trackPoints.size}")
                }
                element(null, "trkseg") {
                    trackPoints.forEach {
                        element(null, "trkpt") {
                            attribute(null,"lat", it.latitude.toString())
                            attribute(null,"lon", it.longitude.toString())
                            element(null, "ele", it.elevation.toString())
                            element(null, "time", it.time.formatRfc3339())
                            element(null, "pdop", it.precision.toString())
                            element(null, "heartrate", it.heartRate.toString())
                            element(null, "speed", it.speed.toString())
                        }
                    }
                }
            }
        }
    }
}