package de.uriegel.superfit.ui

import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import de.uriegel.superfit.R
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.maps.LocationMarker
import de.uriegel.superfit.maps.TrackLine
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.ui.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.android.util.AndroidUtil

import org.mapsforge.map.rendertheme.InternalRenderTheme

import org.mapsforge.map.android.graphics.AndroidGraphicFactory

import org.mapsforge.map.layer.renderer.TileRendererLayer

import org.mapsforge.map.reader.MapFile

import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.rotation.RotateView
import java.io.FileInputStream

abstract class MapActivity(private val trackNrChoice: Int?) : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (root, mapContainer) = initializeBinding()
        this.mapContainer = mapContainer
        setContentView(root)

        mapView = MapView(this)
        with(mapView) {
            // isClickable = true
            setBuiltInZoomControls(true)
            mapScaleBar.isVisible = true
            //model.frameBufferModel.overdrawFactor = 1.0
//            setZoomLevelMin(10)
//            setZoomLevelMax(20)
            setZoomLevel(16)
        }
        mapContainer.addView(mapView)

        val tileCache = AndroidUtil.createTileCache(this, "mapcache", mapView.model.displayModel.tileSize, 1f,
            mapView.model.frameBufferModel.overdrawFactor)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val uriString = preferences?.getString(PreferenceFragment.PREF_MAP, "")
        if (uriString == "") {
            toast(R.string.toast_nomaps, Toast.LENGTH_LONG)
            // TODO
            return
        }
        val uri = Uri.parse(uriString)
        val fis: FileInputStream = contentResolver.openInputStream(uri) as FileInputStream
        val mapDataStore: MapDataStore = MapFile(fis)
//        val tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
//            InternalRenderTheme.OSMARENDER, false, true, false)
        val tileRendererLayer = TileRendererLayer(
            tileCache, mapDataStore,
            mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE
        )
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
        with(mapView) {
            layerManager.layers.add(tileRendererLayer)
            setCenter(LatLong(50.90042250198412, 6.715496743031949))
        }

        trackLine = TrackLine()
        mapView.layerManager.layers.add(trackLine)
        loadGpxTrack()
    }

    override fun onDestroy() {
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    protected data class BindingData(val root: View, val mapContainer: FrameLayout)

    abstract protected fun initializeBinding(): BindingData

    private fun loadGpxTrack() {
        launch {
            val trackNr = trackNrChoice ?: LocationManager.getCurrentTrack()

            trackNr?.let { tnr ->
                viewModel.findTrackPointsAsync(tnr).await()?.let { track ->
                    track.forEach { (trackLine.latLongs.add(LatLong(it.latitude, it.longitude))) }
                    if (trackNrChoice != null)
                        zoomAndPan()
                }
            }
        }
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

    override val coroutineContext = Dispatchers.Main

    protected lateinit var mapContainer: FrameLayout
    protected lateinit var mapView: MapView
    protected lateinit var trackLine: TrackLine
    private val viewModel: MainViewModel by viewModels()
}