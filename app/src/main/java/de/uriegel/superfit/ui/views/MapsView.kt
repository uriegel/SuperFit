package de.uriegel.superfit.ui.views

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import de.uriegel.superfit.R
import de.uriegel.superfit.location.LocationMarker
import de.uriegel.superfit.location.LocationProvider.Companion.locationEmpty
import de.uriegel.superfit.location.TrackLine
import de.uriegel.superfit.models.LocationModel
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.FileInputStream

@Composable
fun MapsView(trackLine: TrackLine, followLocation: Boolean, viewModel: LocationModel?) {

    Log.i("MIST", "fl: $followLocation")

    var onFollowLocationChanged by remember {
        mutableStateOf(FollowLocationCallback {})
    }

    var onLocationChanged by remember {
        mutableStateOf(LocationChangedCallback {_,_,_ -> null} )
    }

    var locationMarker by remember {
        mutableStateOf(null as LocationMarker?)
    }

    onFollowLocationChanged.func(followLocation)
    if (viewModel != null)
        locationMarker = onLocationChanged.func(viewModel.currentPosition.value, followLocation, locationMarker)

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    isClickable = true
                    setZoomLevel(16)
                    setCenter(LatLong(50.90042250198412, 6.715496743031949))
                    setBuiltInZoomControls(!followLocation)
                    mapZoomControls.setMarginVertical(120)
                    mapScaleBar.marginVertical = 130
                    mapScaleBar.isVisible = !followLocation
                    val tileCache = AndroidUtil.createTileCache(
                        context, "mapcache", model.displayModel.tileSize, 1f,
                        model.frameBufferModel.overdrawFactor
                    )
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    preferences?.getString("PREF_MAP", null)?.let {
                        val uri = Uri.parse(it)
                        val fis: FileInputStream =
                            context.contentResolver.openInputStream(uri) as FileInputStream
                        val mapDataStore: MapDataStore = MapFile(fis)
//        val tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
//            InternalRenderTheme.OSMARENDER, false, true, false)
                        val tileRendererLayer = TileRendererLayer(
                            tileCache, mapDataStore,
                            model.mapViewPosition, AndroidGraphicFactory.INSTANCE
                        )
                        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER)
                        layerManager.layers.add(tileRendererLayer)
                        layerManager.layers.add(trackLine)

                        onLocationChanged = LocationChangedCallback { loc, followLocation, locationMarker ->
                            if (followLocation && loc != locationEmpty)
                                setCenter(loc)
                            if (locationMarker != null)
                                layerManager.layers.remove(locationMarker)
                            val result = LocationMarker(loc)
                            layerManager.layers.add(result)
                            result
                        }

                    } ?: Toast.makeText(context, R.string.toast_nomaps, Toast.LENGTH_LONG).show()

                    onFollowLocationChanged = FollowLocationCallback {
                        Log.i("MIST", "fl ch: $followLocation, $it")
                        if (this.mapScaleBar.isVisible == it) {
                            this.setBuiltInZoomControls(!it)
                            this.mapScaleBar.isVisible = !it
                        }
                    }
                }
            },
            onRelease = {
                it.layerManager.layers.clear()
            })
    }
}

data class FollowLocationCallback(
    val func: ((followLocation: Boolean)->Unit)
)

data class LocationChangedCallback(
    val func: ((loc: LatLong, followLocation: Boolean, locationMarker: LocationMarker?)->LocationMarker?)
)