package de.uriegel.superfit.ui

import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import de.uriegel.superfit.databinding.ActivityTrackingBinding
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.maps.LocationMarker
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

class TrackingActivity: MapActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        LocationManager.listener = {
            val currentLatLong = LatLong(it.latitude, it.longitude)
            if (location != null)
                mapView.layerManager.layers.remove(location)
            location = LocationMarker(currentLatLong)
            mapView.layerManager.layers.add(location)
            if (followLocation)
                mapView.setCenter(currentLatLong)
            trackLine.latLongs.add(currentLatLong)
            lastLocation = it
        }
        LocationManager.getCurrentTrack()?.let {
            launch {
                loadGpxTrack(it)
            }
        }

        mapView.setBuiltInZoomControls(false)
        mapView.mapScaleBar.isVisible = false

        binding.switcher.setOnClickListener {
            followLocation = !followLocation
        }
    }

    override fun initializeBinding(): BindingData {
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        return BindingData(binding.root, binding.mapContainer)
    }

    private lateinit var binding: ActivityTrackingBinding
    private var followLocation = true
        set(value) {
            field = value
            mapView.setBuiltInZoomControls(!value)
            mapView.mapScaleBar.isVisible = !value
        }
    private var location: LocationMarker? = null
    private var lastLocation: Location? = null
}
