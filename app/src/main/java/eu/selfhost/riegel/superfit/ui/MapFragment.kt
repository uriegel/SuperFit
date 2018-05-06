package eu.selfhost.riegel.superfit.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import eu.selfhost.riegel.superfit.maps.LocationMarker
import eu.selfhost.riegel.superfit.utils.TrackGpxParser
import eu.selfhost.riegel.superfit.utils.getSdCard
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
        val rotateView = RotateView(activity)
        mapView = MapView(activity)
        with(mapView){
            isClickable = true
            setBuiltInZoomControls(true)
            setZoomLevelMin(10)
            setZoomLevelMax(20)
            setZoomLevel(16)
        }
        rotateView.addView(mapView )
        rotateView.heading = 35F

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

        location = LocationMarker(center)
        mapView.layerManager.layers.add(location)

        frameLayout.addView(rotateView)
        //frameLayout.addView(mapView)

        test()

        return frameLayout
    }

    override fun onDestroy() {
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    fun test() {

        val paint = AndroidGraphicFactory.INSTANCE.createPaint()
        with (paint) {
            setStyle(Style.STROKE)
            strokeWidth = 15F
            color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE)
        }

        val polyline = Polyline(paint, AndroidGraphicFactory.INSTANCE)

        val latLongs = polyline.latLongs
        var latLong: LatLong

        val sdCard: String = activity.getSdCard()
        val tracksDir = "$sdCard/Maps/tracks"
        val gpxFile = "$tracksDir/2018-4-3-16-56.gpx (3).xml"

        mapView.layerManager!!.layers.add(polyline)

        doAsync {
            try {
                var points = TrackGpxParser(File(gpxFile)).map { LatLong(it.latitude, it.longitude) }
                val kaunt2 =points.count()
                points += points
                //points = points + points

                val kaunt = points.count()

                for (point in points) {
                    Thread.sleep(500)
                    uiThread {
                        try {
                            latLongs.add(point)
                            if (location != null)
                                mapView.layerManager.layers.remove(location)
                            location = LocationMarker(point)
                            mapView.layerManager.layers.add(location)
                        } catch (_: Exception) {}
                    }
                }
            }
            catch (e: Exception) {
                var ee = e
                var emil = ee.toString()
            }
        }

//        var timer: Timer = Timer()
//        timer.schedule(object: TimerTask() {
//            override fun run() {
//        }, 50, 50)

        // add: mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(bb.getCenterPoint(), LatLongUtils.zoomForBounds(dimension, bb, mapView.getModel().displayModel.getTileSize())));
        // warp to track
    }

    private lateinit var frameLayout: FrameLayout
    private lateinit var mapView: MapView
    private lateinit var tileCache: TileCache
    private lateinit var tileRendererLayer: TileRendererLayer
    private var location: LocationMarker? = null
}
