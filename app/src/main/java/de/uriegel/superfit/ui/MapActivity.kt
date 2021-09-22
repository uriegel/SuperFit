package de.uriegel.superfit.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import de.uriegel.superfit.R
import de.uriegel.superfit.maps.TrackLine
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.ui.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.android.util.AndroidUtil

import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.core.model.LatLong
import java.io.FileInputStream

abstract class MapActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (root, mapContainer) = initializeBinding()
        this.mapContainer = mapContainer
        setContentView(root)

        mapView = MapView(this)
        with(mapView) {
            isClickable = true
            //model.frameBufferModel.overdrawFactor = 1.0
//            setZoomLevelMin(10)
//            setZoomLevelMax(20)
            setZoomLevel(16)
            setCenter(LatLong(50.90042250198412, 6.715496743031949))
            setBuiltInZoomControls(true)
            mapZoomControls.setMarginVertical(100)
            mapScaleBar.marginVertical = 100
            mapScaleBar.isVisible = true
        }
        mapContainer.addView(mapView)

        val tileCache = AndroidUtil.createTileCache(this, "mapcache", mapView.model.displayModel.tileSize, 1f,
            mapView.model.frameBufferModel.overdrawFactor)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences?.getString(PreferenceFragment.PREF_MAP, null)?.let {
            val uri = Uri.parse(it)
            val fis: FileInputStream = contentResolver.openInputStream(uri) as FileInputStream
            val mapDataStore: MapDataStore = MapFile(fis)
//        val tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
//            InternalRenderTheme.OSMARENDER, false, true, false)
            val tileRendererLayer = TileRendererLayer(
                tileCache, mapDataStore,
                mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE
            )
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER)
            mapView.layerManager.layers.add(tileRendererLayer)
        } ?: toast(R.string.toast_nomaps, Toast.LENGTH_LONG)

        trackLine = TrackLine()
        mapView.layerManager.layers.add(trackLine)
    }

    override fun onDestroy() {
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    protected data class BindingData(val root: View, val mapContainer: FrameLayout)

    protected abstract fun initializeBinding(): BindingData

    protected suspend fun loadGpxTrack(trackNr: Int) {
        viewModel.findTrackPointsAsync(trackNr).await()?.let { track ->
            track.forEach { (trackLine.latLongs.add(LatLong(it.latitude, it.longitude))) }
        }
    }

    override val coroutineContext = Dispatchers.Main

    private lateinit var mapContainer: FrameLayout
    protected lateinit var mapView: MapView
    protected lateinit var trackLine: TrackLine
    private val viewModel: MainViewModel by viewModels()
}