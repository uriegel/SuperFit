package eu.selfhost.riegel.superfit.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask

class DisplayFragment : Fragment(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        webView = WebView(activity)

        with (webView) {
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            addJavascriptInterface(javaScriptInterface, "DisplayNative")
        }
        WebView.setWebContentsDebuggingEnabled(true)
        webView.loadUrl("file:///android_asset/index.html#display")

        HeartRate.listener = { heartRate -> launch { webView.evaluateJavascript("onHeartRateChanged($heartRate)", null) } }
        timer = Timer()
        timer.schedule(timerTask {
            if (Bike.isStarted)
                launch {
                    webView.evaluateJavascript(
                    "onBikeDataChanged(${Bike.speed}, ${Bike.maxSpeed}, ${Bike.averageSpeed}, ${Bike.distance}, ${Bike.duration}, ${Bike.cadence})", null)
                }
        }, delay , delay )

        LocationManager.setGpsActive = { launch { webView.evaluateJavascript("setGpsActive()", null) } }

        return webView
    }

    override fun onDestroyView() {
        LocationManager.setGpsActive = null
        HeartRate.listener = null
        timer.cancel()
        super.onDestroyView()
    }

    private val javaScriptInterface = object {
        @JavascriptInterface
        fun onReady() {
            if (LocationManager.gpsActive)
                launch { webView.evaluateJavascript("setGpsActive()", null) }
        }
    }

    private lateinit var webView: WebView
    private lateinit var timer: Timer
    private val delay = 500L
}
