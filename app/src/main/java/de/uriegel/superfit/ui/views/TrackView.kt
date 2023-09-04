package de.uriegel.superfit.ui.views

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.ui.NavRoutes
import java.util.Calendar
import java.util.Date

@Composable
fun TrackView(track: Track, navController: NavHostController) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable{
                navController.navigate(NavRoutes.TrackMapView.route + "/${track.id}")
            }
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(track.getName(context))
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
        "${dateFormat.format(date)} ${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${cal.get(
            Calendar.MINUTE).toString().padStart(2, '0')}"
    }
