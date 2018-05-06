package eu.selfhost.riegel.superfit.maps

import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Polyline

class TrackLine : Polyline(paint, AndroidGraphicFactory.INSTANCE) {
    companion object {
        private val paint = AndroidGraphicFactory.INSTANCE.createPaint()
        init {
            with (paint) {
                setStyle(Style.STROKE)
                strokeWidth = 15F
                color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLACK)
            }
        }
    }
}