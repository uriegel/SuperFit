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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import de.uriegel.superfit.room.Track
import java.util.Calendar
import java.util.Date

@Composable
fun TrackView(track: Track) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable{ }
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append("welcome to ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900, color = Color(0xFF4552B8))
                    ) {
                        append("Jetpack Compose Playground")
                    }
                }
            )
            Text(
                buildAnnotatedString {
                    append("Now you are in the ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                        append("Card ")
                    }
                    append(track.getName(context))
                }
            )
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
