package de.uriegel.superfit.ui

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.BR
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.FragmentTrackingBinding
import de.uriegel.superfit.maps.LocationMarker
import de.uriegel.superfit.maps.LocationProvider
import de.uriegel.superfit.model.DisplayModel
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

class TrackingFragment: MapFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        launch {
            LocationProvider.trackNr?.let {
                loadGpxTrack(it)
            }
            val locationObserver = Observer<Location> {
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
            binding.displayModel?.locationData?.observe(viewLifecycleOwner, locationObserver)
        }

        mapView.setBuiltInZoomControls(false)
        mapView.mapScaleBar.isVisible = false

        binding.switcher.setOnClickListener {
            followLocation = !followLocation
            pagingEnabled.value = followLocation
        }

        return view
    }

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): BindingData {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val viewModel = ViewModelProvider(this).get(DisplayModel::class.java)
        binding.setVariable(BR.displayModel, viewModel)
        return BindingData(binding.root, binding.mapContainer)
    }

    companion object {
        val pagingEnabled: MutableLiveData<Boolean> = MutableLiveData()
    }

    private lateinit var binding: FragmentTrackingBinding

    private var followLocation = true
        set(value) {
            field = value
            mapView.setBuiltInZoomControls(!value)
            mapView.mapScaleBar.isVisible = !value
        }
    private var location: LocationMarker? = null
    private var lastLocation: Location? = null
}
