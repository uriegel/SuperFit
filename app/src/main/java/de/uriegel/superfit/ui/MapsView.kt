package de.uriegel.superfit.ui

import android.os.Environment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File

@Composable
fun MapsView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                isClickable = true
                setZoomLevel(16)
                setCenter(LatLong(50.90042250198412, 6.715496743031949))
                setBuiltInZoomControls(true)
                mapZoomControls.setMarginVertical(100)
                mapScaleBar.marginVertical = 100
                mapScaleBar.isVisible = true
                val tileCache = AndroidUtil.createTileCache(
                    context, "mapcache", model.displayModel.tileSize, 1f,
                    model.frameBufferModel.overdrawFactor
                )
                val mapDataStore: MapDataStore = MapFile(
                    File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/germany.map").inputStream())
//                val tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
//                             InternalRenderTheme.OSMARENDER, false, true, false)
                val tileRendererLayer = TileRendererLayer(
                    tileCache, mapDataStore,
                    model.mapViewPosition, AndroidGraphicFactory.INSTANCE
                )
                tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER)
                layerManager.layers.add(tileRendererLayer)
            }
        })
}