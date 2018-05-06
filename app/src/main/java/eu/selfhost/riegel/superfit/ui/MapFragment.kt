package eu.selfhost.riegel.superfit.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.maps.LocationMarker
import eu.selfhost.riegel.superfit.maps.TrackLine
import eu.selfhost.riegel.superfit.utils.TrackGpxParser
import eu.selfhost.riegel.superfit.utils.getSdCard
import eu.selfhost.riegel.superfit.utils.serialize
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rotation.RotateView
import org.mapsforge.map.layer.overlay.Polyline
import org.mapsforge.map.reader.MapFile
import java.io.File


/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        frameLayout = FrameLayout(activity)
        mapView = MapView(activity)
        with(mapView){
            isClickable = true
            setBuiltInZoomControls(true)
            model.frameBufferModel.overdrawFactor = 1.0
            setZoomLevelMin(10)
            setZoomLevelMax(20)
            setZoomLevel(16)
        }

        val rotateView = RotateView(activity)
        with (rotateView) {
            addView(mapView )
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            heading = 35F
        }

        tileCache = AndroidUtil.createTileCache(activity, "mapcache", mapView.model.displayModel.tileSize, 1.0F,
                mapView.model.frameBufferModel.overdrawFactor)

        val sdCard: String = activity.getSdCard()
        val mapsDir = "$sdCard/Maps"
        val mapDataStore = MapFile(File(mapsDir, "germany.map"))

        tileRendererLayer = AndroidUtil.createTileRendererLayer(tileCache, mapView.model.mapViewPosition, mapDataStore,
                InternalRenderTheme.OSMARENDER, false, true, false)

        val center = LatLong(50.90042250198412, 6.715496743031949)
        with(mapView) {
            layerManager.layers.add(tileRendererLayer)
            model.frameBufferModel.overdrawFactor = 1.0
            mapScaleBar.isVisible = true

            setCenter(center)
        }

        trackLine = TrackLine()
        mapView.layerManager.layers.add(trackLine)

        frameLayout.addView(rotateView)
        //frameLayout.addView(mapView)

        LocationManager.listener = {
            val currentLatLong = LatLong(it.latitude, it.longitude)
            if (location != null)
                mapView.layerManager.layers.remove(location)
            location = LocationMarker(currentLatLong)
            mapView.layerManager.layers.add(location)

            trackLine.latLongs.add(currentLatLong)
        }
        return frameLayout
    }

    override fun onDestroy() {
        LocationManager.listener = null
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    private lateinit var frameLayout: FrameLayout
    private lateinit var mapView: MapView
    private lateinit var tileCache: TileCache
    private lateinit var trackLine: TrackLine
    private lateinit var tileRendererLayer: TileRendererLayer
    private var location: LocationMarker? = null
}
