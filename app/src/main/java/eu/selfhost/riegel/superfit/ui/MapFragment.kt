package eu.selfhost.riegel.superfit.ui


import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.database.TrackPoint
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.maps.LocationManager.getCurrentTrackAsync
import eu.selfhost.riegel.superfit.maps.LocationMarker
import eu.selfhost.riegel.superfit.maps.TrackLine
import eu.selfhost.riegel.superfit.utils.getSdCard
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mapsforge.core.model.BoundingBox
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

class MapFragment : Fragment(), CoroutineScope {

	override val coroutineContext = Dispatchers.Main

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tracking, container, false)
        frameLayout = root.findViewById(R.id.mapContainer)
        mapView = MapView(activity)
        with(mapView)
        {
            isClickable = true
            setBuiltInZoomControls(true)
            model.frameBufferModel.overdrawFactor = 1.0
            setZoomLevelMin(10)
            setZoomLevelMax(20)
            setZoomLevel(16)
        }

        tileCache = AndroidUtil.createTileCache(activity, "mapcache", mapView.model.displayModel.tileSize, 1.0F,
                mapView.model.frameBufferModel.overdrawFactor)

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val map = preferences.getString(PreferenceActivity.PREF_MAP, null)
        val sdCard: String = activity.getSdCard()
        val mapsDir = "$sdCard/Maps"
        val mapDataStore = MapFile(File(mapsDir, map!!))

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

        launch {
            val tracks = withContext(Dispatchers.Default) { getCurrentTrackAsync() }
			if (tracks.isNotEmpty())
                tracks.forEach { (trackLine.latLongs.add(it)) }
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

        return root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		switcher.setOnClickListener {
			if (activity is DisplayActivity) {
				followLocation = !followLocation
				enableBearing(followLocation)
				(activity as DisplayActivity).pagingEnabled = followLocation
			}
		}
	}

	override fun onDestroy() {
		LocationManager.listener = null
		super.onDestroy()
		launch {
			mapView.destroyAll()
			AndroidGraphicFactory.clearResourceMemoryCache()
		}
	}

	fun loadGpxTrack(track: Array<TrackPoint>) {
		track.forEach { (trackLine.latLongs.add(LatLong(it.latitude, it.longitude))) }
		zoomAndPan()
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

		//        val pointSouthWest = LatLong(boundingBox.minLatitude, boundingBox.minLongitude)
		//        val pointNorthEast = LatLong(boundingBox.maxLatitude, boundingBox.maxLongitude)
		//        val maxLevel = mapView!!.model.mapViewPosition.zoomLevelMax
		//
		//        val projection = mapView!!.mapViewProjection
		//        for (zoomlevel in 1..maxLevel) {
		//            mapView!!.setZoomLevel(zoomlevel.toByte())
		//            val sw = projection.toPixels(pointSouthWest)
		//            val ne = projection.toPixels(pointNorthEast)
		//            if (ne.x - sw.x > width || sw.y -ne.y > height) {
		//                mapView!!.setZoomLevel((zoomlevel - 1).toByte())
		//                break
		//            }
		//        }
	}

	private enum class RotateViewChangeState {
		Disabled,
		Enabled,
		Changing
	}

	companion object {
		const val SHOW_TRACKING_CONTROL = "ShowTrackingControl"
		private const val ACCURACY_BEARING = 10F
	}

	private var rotateViewChangeState = RotateViewChangeState.Disabled
	private lateinit var frameLayout: FrameLayout
	private lateinit var mapView: MapView
	private lateinit var tileCache: TileCache
	private lateinit var trackLine: TrackLine
	private lateinit var gpxTrackLine: TrackLine
	private lateinit var tileRendererLayer: TileRendererLayer
	private var rotateView: RotateView? = null
	private var location: LocationMarker? = null
	private var followLocation = true
	private var lastLocation: Location? = null
}
