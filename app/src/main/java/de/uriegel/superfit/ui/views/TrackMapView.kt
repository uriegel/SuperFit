package de.uriegel.superfit.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import de.uriegel.superfit.location.TrackLine
import de.uriegel.superfit.room.TracksRepository
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

@Composable
fun TrackMapView(trackId: Int) {

    val coroutineScope = rememberCoroutineScope()
    val trackLine by remember { mutableStateOf(TrackLine()) }

    SideEffect {
        coroutineScope.launch {
            TracksRepository
                .findTrackPointsAsync(trackId)
                .await()
                ?.forEach {
                    trackLine.addPoint(LatLong(it.latitude, it.longitude))
                }
        }
    }

    MapViewControl(trackLine, false, null)
}

@Preview
@Composable
fun TrackMapViewPreview() {
    TrackMapView(0)
}

