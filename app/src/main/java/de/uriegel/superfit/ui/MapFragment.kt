package de.uriegel.superfit.ui

import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.FragmentTrackingBinding
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.maps.LocationMarker
import de.uriegel.superfit.maps.TrackLine
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.ui.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rotation.RotateView
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.FileInputStream

open class MapFragment(private val tracking: Boolean) : Fragment(), CoroutineScope {

    constructor() : this(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        frameLayout = binding.root.findViewById(R.id.mapContainer)
        mapView = MapView(activity)
        with(mapView) {
            isClickable = true
            setBuiltInZoomControls(true)
            model.frameBufferModel.overdrawFactor = 1.0
            setZoomLevelMin(10)
            setZoomLevelMax(20)
            setZoomLevel(16)
        }
        frameLayout.addView(mapView)

        val tileCache = AndroidUtil.createTileCache(activity, "mapcache", mapView.model.displayModel.tileSize, 1.0F,
            mapView.model.frameBufferModel.overdrawFactor)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val uriString = preferences?.getString(PreferenceFragment.PREF_MAP, "")
        if (uriString == "") {
            requireActivity().finish()
            requireActivity().toast(R.string.toast_nomaps, Toast.LENGTH_LONG)
            return binding.root
        }
        val uri = Uri.parse(uriString)
        val fis: FileInputStream = requireActivity().contentResolver.openInputStream(uri) as FileInputStream
        val mapDataStore: MapDataStore = MapFile(fis)
        val tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
            InternalRenderTheme.OSMARENDER, false, true, false)

        val center = LatLong(50.90042250198412, 6.715496743031949)
        with (mapView) {
            layerManager.layers.add(tileRendererLayer)
            model.frameBufferModel.overdrawFactor = 1.0
            mapScaleBar.isVisible = true

            setCenter(center)
        }

        trackLine = TrackLine()
        mapView.layerManager.layers.add(trackLine)

        if (tracking)
            loadGpxTrack(null)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switcher.setOnClickListener {
            if (activity is DisplayActivity) {
                followLocation = !followLocation
                enableBearing(followLocation)
//                (activity as DisplayActivity).pagingEnabled = followLocation
            }
        }
    }

    override fun onDestroy() {
        LocationManager.listener = null
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main

    fun loadGpxTrack(trackNrChoice: Int?) {
        launch {
            val trackNr = trackNrChoice ?: LocationManager.getCurrentTrack()

            trackNr?.let { tnr ->
                viewModel.findTrackPointsAsync(tnr).await()?.let { track ->
                    track.forEach { (trackLine.latLongs.add(LatLong(it.latitude, it.longitude))) }
                    if (trackNrChoice != null)
                        zoomAndPan()
                }
            }

            if (trackNrChoice == null) {
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
            }
        }
    }

    fun enableBearing(enable: Boolean) {
        if (rotateViewChangeState == RotateViewChangeState.Changing)
            return

        if (enable && rotateView == null && rotateViewChangeState == RotateViewChangeState.Disabled) {
            rotateViewChangeState = RotateViewChangeState.Changing
            frameLayout.removeAllViews()
            rotateView = RotateView(activity)
            with(rotateView!!) {
                addView(mapView)
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            frameLayout.addView(rotateView)
            rotateViewChangeState = RotateViewChangeState.Enabled
        }
        else if (!enable && rotateView != null && rotateViewChangeState == RotateViewChangeState.Enabled) {
            rotateViewChangeState = RotateViewChangeState.Changing
            frameLayout.removeAllViews()
            rotateView!!.removeAllViews()
            rotateView = null
            frameLayout.addView(mapView)
            rotateViewChangeState = RotateViewChangeState.Disabled
        }
        followLocation = enable
    }

    private fun zoomAndPan() {
        val boundingBox = BoundingBox(trackLine.latLongs)
        val width = mapView.width
        val height = mapView.height
        if (width <= 0 || height <= 0)
            return
        val centerPoint = LatLong((boundingBox.maxLatitude + boundingBox.minLatitude) / 2, (boundingBox.maxLongitude + boundingBox.minLongitude) / 2)
        mapView.setCenter(centerPoint)
    }

    private enum class RotateViewChangeState {
        Disabled,
        Enabled,
        Changing
    }

    companion object {
        private const val ACCURACY_BEARING = 10F
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var frameLayout: FrameLayout
    private lateinit var mapView: MapView
    private lateinit var binding: FragmentTrackingBinding
    private lateinit var trackLine: TrackLine
    private var followLocation = true
    private var location: LocationMarker? = null
    private var lastLocation: Location? = null
    private var rotateView: RotateView? = null
    private var rotateViewChangeState = RotateViewChangeState.Disabled
}