package de.uriegel.superfit.ui.views

import android.app.Activity
import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.uriegel.superfit.R
import de.uriegel.superfit.extensions.exportToGpx
import de.uriegel.superfit.extensions.getFileName
import de.uriegel.superfit.location.TrackLine
import de.uriegel.superfit.room.TracksRepository
import de.uriegel.superfit.ui.rememberLauncherWithState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mapsforge.core.model.LatLong

@Composable
fun TrackMapView(navController: NavHostController, trackId: Int,
                 lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    val launcher = rememberLauncherWithState(ActivityResultContracts.StartActivityForResult())
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val trackLine by remember { mutableStateOf(TrackLine()) }
    var bottomBar by remember { mutableStateOf(true)}

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog)
        ResourceAlertDialog(
            R.string.alert_title_delete_track, R.string.alert_delete_track,
            { showDeleteDialog = false },
            { coroutineScope.launch {
                    TracksRepository.deleteTrackAsync(trackId)
                }
                navController.navigateUp()
            }
        )

    LaunchedEffect(lifecycleOwner) {
        Log.i("AFFE", "male")
        coroutineScope.launch {
            TracksRepository
                .findTrackPointsAsync(trackId)
                .await()
                ?.let{
                    withContext(Dispatchers.Default) {
                        it.apply { it.forEach {
                            trackLine.addPoint(LatLong(it.latitude, it.longitude))
                        }}
                    }
                }
                ?.let {
                    trackLine.zoomAndPan()
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
                                getTrack(trackId)?.let{ track ->
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    intent.type = "application/gpx+xml"
                                    val fileName = track.getFileName()
                                    intent.putExtra(Intent.EXTRA_TITLE, "$fileName.gpx")
                                    launcher.launch(intent) { res ->
                                        if (res.resultCode == Activity.RESULT_OK) {
                                            res.data?.data?.let {
                                                runCatching {
                                                    val stream = context.contentResolver.openOutputStream(it)
                                                    stream?.let {
                                                        coroutineScope.launch {
                                                            TracksRepository
                                                                .findTrackPointsAsync(trackId)
                                                                .await()
                                                                ?.let { trackPoints ->
                                                                    it.exportToGpx(fileName, track, trackPoints)
                                                                    it.close()
                                                                }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                        Icon(Icons.Filled.Share, contentDescription = "")
                    }
                    IconButton(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { showDeleteDialog = true }) {
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

suspend fun getTrack(trackId: Int) =
    TracksRepository
        .findTrackAsync(trackId)
        .await()

@Preview
@Composable
fun TrackMapViewPreview() {
    TrackMapView(rememberNavController(),0)
}

