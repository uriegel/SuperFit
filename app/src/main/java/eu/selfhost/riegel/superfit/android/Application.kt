package eu.selfhost.riegel.superfit.android

import org.mapsforge.map.android.graphics.AndroidGraphicFactory

class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidGraphicFactory.createInstance(this)
        instance = this
    }

    companion object {
        lateinit var instance: Application
            private set
    }
}


