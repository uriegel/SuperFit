package de.uriegel.superfit.ui.views

import androidx.compose.runtime.Composable
import de.uriegel.superfit.location.LocationProvider

@Composable
fun TrackingMapsView(followLocation: Boolean) {
    MapsView(LocationProvider.trackLine, followLocation)
}