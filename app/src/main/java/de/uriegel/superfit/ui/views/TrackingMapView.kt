package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.superfit.location.LocationProvider
import de.uriegel.superfit.models.LocationModel

@Composable
fun TrackingMapView(followLocation: Boolean, viewModel: LocationModel? = viewModel()) {
    Scaffold(
        content = {
            Box(
                modifier = Modifier.padding(it)
            ) {
                MapViewControl(LocationProvider.trackLine, followLocation, viewModel)
            }
        }
    )
}