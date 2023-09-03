package de.uriegel.superfit.location

import org.mapsforge.core.graphics.Canvas
import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.util.MercatorProjection
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.Layer

class LocationMarker(private val latLong: LatLong) : Layer() {
    override fun draw(boundingBox: BoundingBox?, zoomLevel: Byte, canvas: Canvas?, topLeftPoint: Point) {
        if (boundingBox?.contains(latLong) == true) {
            val mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.tileSize)
            val x1 = MercatorProjection.longitudeToPixelX(latLong.longitude, mapSize) - topLeftPoint.x
            val y1 = MercatorProjection.latitudeToPixelY(latLong.latitude, mapSize) - topLeftPoint.y

            val paint = AndroidGraphicFactory.INSTANCE.createPaint()
            with (paint) {
                setStyle(Style.FILL)
                color = AndroidGraphicFactory.INSTANCE.createColor(Color.WHITE)
            }
            canvas?.drawCircle(x1.toInt(), y1.toInt(), 16, paint)

            with (paint) {
                strokeWidth = 16F
                setStyle(Style.STROKE)
                color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE)
            }
            canvas?.drawCircle(x1.toInt(), y1.toInt(), 20, paint)
        }
    }
}