package de.uriegel.superfit.ui

import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import de.uriegel.superfit.databinding.ActivityTrackingBinding
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.maps.LocationMarker
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.rotation.RotateView

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

            if (rotateView != null
                && lastLocation != null
                && lastLocation!!.accuracy < ACCURACY_BEARING
                && it.accuracy < ACCURACY_BEARING
            )
                rotateView!!.heading = lastLocation!!.bearingTo(it)
            lastLocation = it
        }
        enableBearing(true)
        LocationManager.getCurrentTrack()?.let {
            launch {
                loadGpxTrack(it)
            }
        }

        binding.switcher.setOnClickListener {
            followLocation = !followLocation
            enableBearing(followLocation)
        }
    }

    override fun initializeBinding(): BindingData {
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        return BindingData(binding.root, binding.mapContainer)
    }

    private fun enableBearing(enable: Boolean) {
        if (rotateViewChangeState == RotateViewChangeState.Changing)
            return

        mapContainer.removeAllViews()
        if (enable && rotateView == null && rotateViewChangeState == RotateViewChangeState.Disabled) {
            rotateViewChangeState = RotateViewChangeState.Changing
            rotateView = RotateView(this)
            mapContainer.addView(rotateView)
            with(rotateView!!) {
                addView(mapView)
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            }

            rotateViewChangeState = RotateViewChangeState.Enabled
        } else if (!enable && rotateView != null && rotateViewChangeState == RotateViewChangeState.Enabled) {
            rotateViewChangeState = RotateViewChangeState.Changing
            rotateView!!.removeAllViews()
            rotateView = null
            mapContainer.addView(mapView)
            rotateViewChangeState = RotateViewChangeState.Disabled
        }
        followLocation = enable
    }

    private enum class RotateViewChangeState {
        Disabled,
        Enabled,
        Changing
    }

    companion object {
        private const val ACCURACY_BEARING = 10F
    }

    private lateinit var binding: ActivityTrackingBinding
    private var followLocation = true
    private var location: LocationMarker? = null
    private var lastLocation: Location? = null
    private var rotateViewChangeState = RotateViewChangeState.Disabled
    private var rotateView: RotateView? = null
}
