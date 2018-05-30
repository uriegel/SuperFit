package eu.selfhost.riegel.superfit.ui

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.database.DataBase
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var webView = findViewById<WebView>(R.id.mapViewControls)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }

        webView.loadUrl("file:///android_asset/mapViewControls.html")

        val bundle = intent.extras
        val trackNr = bundle.getLong(MainActivity.TRACK_NR)
        async(UI) {
            val trackPoints = DataBase.getTrackPointsAsync(trackNr).await()
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
            fragment.loadGpxTrack(trackPoints)
        }
    }
}
