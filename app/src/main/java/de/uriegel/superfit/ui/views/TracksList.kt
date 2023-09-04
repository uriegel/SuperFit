package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.superfit.models.TracksModel

@Composable
fun TracksList(tracksModel: TracksModel = viewModel()) {

    val tracks = tracksModel.tracks.observeAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState()) ) {
        tracks.value?.forEach {TrackView(it)}
    }
}

