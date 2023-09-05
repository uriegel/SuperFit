package de.uriegel.superfit.ui.views

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import de.uriegel.superfit.extensions.getFileName
import de.uriegel.superfit.location.TrackLine
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TracksRepository
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

@Composable
fun TrackMapView(trackId: Int) {

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

        }
    }


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
                        onClick = {
                            coroutineScope.launch {
                                getTrackExport(trackId)?.let{
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    intent.type = "application/gpx+xml"
                                    intent.putExtra(Intent.EXTRA_TITLE, "${it.name}.gpx")
                                    launcher.launch(intent)
                                }
                            }
                        }) {
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

suspend fun getTrackExport(trackId: Int) =
    TracksRepository
        .findTrackAsync(trackId)
        .await()
        ?.let {
            TrackExport(trackId, it.getFileName(), TracksRepository.findTrackPointsAsync(trackId).await())
        }

data class TrackExport(
    val trackId: Int,
    val name: String,
    val points: Array<TrackPoint>?)

@Preview
@Composable
fun TrackMapViewPreview() {
    TrackMapView(0)
}

