package de.uriegel.superfit.maps

import android.util.Xml
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import java.io.OutputStream

fun exportToGpx(outputStream: OutputStream, name: String, track: Track, trackPoints: Array<TrackPoint>) {

    val serializer = Xml.newSerializer()
    serializer.setOutput(outputStream, "UTF-8")

    serializer.document("UTF-8", true) {
        element(null, "gpx") {
            attribute(null,"version", "1.1")
            element(null, "trk") {
                element(null, "info") {
                    element(null, "name", name)
                    element(null, "distance", "${track.distance}")
                    element(null, "duration", "${track.duration}")
                    element(null, "date", formatRfc3339(track.time!!, track.timeOffset!!))
                    element(null, "averageSpeed", "${track.averageSpeed}")
                    element(null, "trackPoints", "${trackPoints.size}")
                }
                element(null, "trkseg") {
                    trackPoints.forEach {
                        element(null, "trkpt") {
                            attribute(null,"lat", it.latitude.toString())
                            attribute(null,"lon", it.longitude.toString())
                            element(null, "ele", it.elevation.toString())
                            element(null, "time", formatRfc3339(it.time!!, track.timeOffset!!))
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



//
//<trkpt lat="54.8591470" lon="-1.5754310">
//    <ele>29.2</ele>
//    <duration>2015-07-26T07:43:42Z</duration>
//    <extensions>
//        <gpxtpx:TrackPointExtension>
//            <gpxtpx:hr>92</gpxtpx:hr>
//            <gpxtpx:cad>0</gpxtpx:cad>
//        </gpxtpx:TrackPointExtension>
//    </extensions>
//</trkpt>