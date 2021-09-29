package de.uriegel.superfit.ui

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.uriegel.superfit.databinding.FragmentTrackingBinding
import de.uriegel.superfit.maps.LocationMarker
import de.uriegel.superfit.maps.LocationProvider
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

class TrackingFragment: MapFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        LocationProvider.listener = {
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
        LocationProvider.getCurrentTrack()?.let {
            launch {
                loadGpxTrack(it)
            }
        }

        mapView.setBuiltInZoomControls(false)
        mapView.mapScaleBar.isVisible = false

        binding.switcher.setOnClickListener {
            followLocation = !followLocation
            // TODO: decoupling
            if (activity is DisplayActivity)
                (activity as DisplayActivity).pagingEnabled = followLocation
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): BindingData {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return BindingData(binding.root, binding.mapContainer)
    }

    private var binding: FragmentTrackingBinding
        get() = _binding!!
        set(value) { _binding = value }
    private var _binding: FragmentTrackingBinding? = null

    private var followLocation = true
        set(value) {
            field = value
            mapView.setBuiltInZoomControls(!value)
            mapView.mapScaleBar.isVisible = !value
        }
    private var location: LocationMarker? = null
    private var lastLocation: Location? = null
}
