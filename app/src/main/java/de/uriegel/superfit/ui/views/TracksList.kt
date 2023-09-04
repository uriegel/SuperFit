package de.uriegel.superfit.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.superfit.models.TracksModel
import de.uriegel.superfit.room.Track
import java.util.Calendar
import java.util.Date

@Composable
fun TracksList(tracksModel: TracksModel = viewModel()) {

    val context = LocalContext.current
    val tracks = tracksModel.tracks.observeAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState()) ) {
        tracks.value?.forEach {
            Text(it.getName(context),
                Modifier.padding(20.dp))
        }
    }
}

fun Track.getName(context: Context) =
    if (trackName?.isNotEmpty() == true)
        trackName!!
    else {
        val date = Date(time)
        val cal = Calendar.getInstance()
        cal.time = date
        val dateFormat = android.text.format.DateFormat.getDateFormat(context)
        "${dateFormat.format(date)} ${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${cal.get(Calendar.MINUTE).toString().padStart(2, '0')}"
    }
