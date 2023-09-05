package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.uriegel.superfit.location.TrackLine
import de.uriegel.superfit.room.TracksRepository
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

@Composable
fun TrackMapView(trackId: Int) {

    val coroutineScope = rememberCoroutineScope()
    val trackLine by remember { mutableStateOf(TrackLine()) }
    var bottomBar by remember { mutableStateOf(true)}

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

    Scaffold(
        content = {
            Box(
                modifier = Modifier.padding(it)
            ) {
                MapViewControl(trackLine, false, null)
            }
        },
        bottomBar = { if (bottomBar)
            BottomAppBar(
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { /* Check onClick */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "")
                    }
                    IconButton(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { /* Edit onClick */ }) {
                        Icon(
                            Icons.Filled.Delete, contentDescription = ""
                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { bottomBar = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "")
                    }
                }
            )
        }
    )
}

@Preview
@Composable
fun TrackMapViewPreview() {
    TrackMapView(0)
}

