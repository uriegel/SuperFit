package de.uriegel.superfit.models

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.location.LocationProvider
import org.mapsforge.core.model.LatLong

class LocationModel : ViewModel() {
    val currentPosition: MutableState<LatLong> = LocationProvider.currentPosition
}