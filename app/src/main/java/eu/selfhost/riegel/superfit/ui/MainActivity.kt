package eu.selfhost.riegel.superfit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.SoundEffectConstants
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import eu.selfhost.riegel.superfit.android.Service
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.maps.exportToGpx
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import eu.selfhost.riegel.superfit.sensors.Searcher
import eu.selfhost.riegel.superfit.utils.createDocument
import eu.selfhost.riegel.superfit.utils.getSdCard
import eu.selfhost.riegel.superfit.utils.onCreateDocument
import eu.selfhost.riegel.superfit.utils.serialize
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import java.io.File


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        with (webView) {
            with (settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            addJavascriptInterface(javaScriptInterface, "Native")
        }
        WebView.setWebContentsDebuggingEnabled(true)

        Service.setOnStateChangedListener { onStateChanged(it) }

        if (checkPermissions())
            initilize()
    }

    override fun onBackPressed() {
        if (isInitialized)
            webView.evaluateJavascript("onBackPressed()", null)
        else
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    initilize()
                else
                    toast("Some Permission denied")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            onCreateDocument(requestCode, resultCode, data) -> return
        }
    }

    private fun onStateChanged(state: Service.ServiceState) = webView.evaluateJavascript("onStateChanged(${state.serialize()})", null)

    private val javaScriptInterface = object {
        @JavascriptInterface
        fun getState() = doAsync { uiThread { onStateChanged(Service.state) }}

        @JavascriptInterface
        fun doHapticFeedback() = doAsync { uiThread { webView.playSoundEffect(SoundEffectConstants.CLICK) }}

        @JavascriptInterface
        fun fillTracks()  {
            async(UI) {
                val tracks = DataBase.getTracksAsync().await()
                val gson = Gson()
                val json = gson.toJson(tracks)
                webView.evaluateJavascript("onTracks($json)", null)
            }
        }

        @JavascriptInterface
        fun fillMaps()  {
            async(UI) {
                val sdCard: String = getSdCard()
                val mapsDir = "$sdCard/Maps"
                val directory = File(mapsDir)
                val files = directory.listFiles().filter { it.extension == "map" }.map { it.name }
                val gson = Gson()
                val json = gson.toJson(files)
                webView.evaluateJavascript("onMaps($json)", null)
            }
        }

        @Suppress("DEPRECATION")
        @JavascriptInterface
        fun onTrackSelected(trackNr: Long) {
            async(UI) {
                val track = DataBase.getTrackAsync(trackNr).await()
                val date = Date(track.time)
                val name = if (track.name.isEmpty()) "${date.year + 1900}-${date.month + 1}-${date.date}-${date.hours}-${date.minutes}" else track.name

                val uri = createDocument("$name.gpx")
                if (uri != null) {
                    val stream = contentResolver.openOutputStream(uri)
                    val trackPoints = DataBase.getTrackPointsAsync(trackNr).await()
                    exportToGpx(stream, name, track, trackPoints)
                    stream.close()
                }
            }
        }

        @JavascriptInterface
        fun onMapSelected(map: String) {
            val prefs = getSharedPreferences(PREFS, 0)
            val editor = prefs.edit()
            editor.putString(PREF_MAP, map)
            editor.apply()
        }

        @JavascriptInterface
        fun start() = doAsync { uiThread {
            val startIntent = Intent(this@MainActivity, Service::class.java)
            startIntent.action = Service.ACTION_START
            startService(startIntent)
            startActivity(Intent(this@MainActivity, DisplayActivity::class.java))
        }}

        @JavascriptInterface
        fun stop() = doAsync { uiThread {
            val startIntent = Intent(this@MainActivity, Service::class.java)
            startIntent.action = Service.ACTION_STOP
            startService(startIntent)
            finish()
        }}

        @JavascriptInterface
        fun reset() {
            Searcher.stop()
            HeartRate.stop()
            Bike.stop()
            Searcher.start(this@MainActivity)
        }

        @JavascriptInterface
        fun display()  = doAsync { uiThread { startActivity(Intent(this@MainActivity, DisplayActivity::class.java)) }}

        @JavascriptInterface
        fun finish() = doAsync { uiThread { forceOnBackPressed() }}
    }

    private fun forceOnBackPressed() = super.onBackPressed()

    private fun checkPermissions(): Boolean{
        val permissions = listOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED}
        if (permissions.count() > 0) {
            requestPermissions(permissions.toTypedArray(), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            return false
        }
        else
            return true
    }

    private fun initilize() {
        webView.loadUrl("file:///android_asset/main.html")

        isInitialized = true

        if (Service.state == Service.ServiceState.Started)
            startActivity(Intent(this@MainActivity, DisplayActivity::class.java))
    }

    private lateinit var webView: WebView
    private var isInitialized = false

    companion object {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
        const val PREFS = "PREFS"
        const val PREF_MAP = "PREF_MAP"
        private const val CREATE_REQUEST_CODE = 40
    }
}
