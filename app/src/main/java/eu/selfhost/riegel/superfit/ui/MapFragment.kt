package eu.selfhost.riegel.superfit.ui


import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.maps.LocationManager.getCurrentTrack
import eu.selfhost.riegel.superfit.maps.LocationMarker
import eu.selfhost.riegel.superfit.maps.TrackLine
import eu.selfhost.riegel.superfit.utils.getSdCard
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rotation.RotateView
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File

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
            setOnTouchListener(onTouchListener)
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

        frameLayout.addView(mapView)

        async(UI) {
            val tracks = getCurrentTrack().await()
            if (tracks.isNotEmpty())
                tracks.forEach({(trackLine.latLongs.add(it))})

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
                        && it.accuracy < ACCURACY_BEARING)
                    rotateView!!.heading = lastLocation!!.bearingTo(it)
                lastLocation = it
            }
        }

        return frameLayout
    }

    override fun onDestroy() {
        LocationManager.listener = null
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    fun enableBearing(enable: Boolean) {
        if (rotateViewChangeState == RotateViewChangeState.Changing)
            return

        if (enable && rotateView == null && rotateViewChangeState == RotateViewChangeState.Disabled) {
            rotateViewChangeState = RotateViewChangeState.Changing
            frameLayout.removeAllViews()
            rotateView = RotateView(activity)
            with (rotateView!!) {
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
        followLocation = true
    }

    private val onTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressed = true
                    x = event.x
                    y = event.y
                }
                MotionEvent.ACTION_UP -> pressed = false
                else -> {
                    if (event != null && event.pointerCount > 1)
                        pressed = false
                    if (pressed && (Math.abs(x - event!!.x) > PAN_OFFSET || Math.abs(y - event.y) > PAN_OFFSET)) {
                        enableBearing(false)
                        followLocation = false
                        pressed = false
                    }
                }
            }
            return false
        }

        private var pressed = false
        private var x = 0F
        private var y = 0F
    }

    private enum class RotateViewChangeState {
        Disabled,
        Enabled,
        Changing
    }

    companion object {
        private const val ACCURACY_BEARING = 10F
        private const val PAN_OFFSET = 50
    }

    private var rotateViewChangeState = RotateViewChangeState.Disabled
    private lateinit var frameLayout: FrameLayout
    private lateinit var mapView: MapView
    private lateinit var tileCache: TileCache
    private lateinit var trackLine: TrackLine
    private lateinit var tileRendererLayer: TileRendererLayer
    private var rotateView: RotateView? = null
    private var location: LocationMarker? = null
    private var followLocation = true
    private var lastLocation: Location? = null
}
