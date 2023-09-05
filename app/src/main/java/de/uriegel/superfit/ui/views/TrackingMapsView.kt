package de.uriegel.superfit.ui.views

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.superfit.location.LocationProvider
import de.uriegel.superfit.models.LocationModel

@Composable
fun TrackingMapsView(followLocation: Boolean, viewModel: LocationModel? = viewModel()) {
    MapViewControl(LocationProvider.trackLine, followLocation, viewModel)
}