package de.uriegel.superfit.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.superfit.models.TracksModel

@Composable
fun TracksList(tracksModel: TracksModel = viewModel()) {

    val tracks = tracksModel.tracks.observeAsState()

    Text("Das ist in Arbeit und das sind ${tracks.value?.count()}")
}