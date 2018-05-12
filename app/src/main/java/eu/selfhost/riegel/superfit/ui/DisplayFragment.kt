package eu.selfhost.riegel.superfit.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import eu.selfhost.riegel.superfit.android.Service
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.concurrent.timerTask

class DisplayFragment : Fragment() {

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
        webView.loadUrl("file:///android_asset/display.html")

        HeartRate.listener = { heartRate -> doAsync { uiThread { webView.evaluateJavascript("onHeartRateChanged($heartRate)", null) } } }
        timer = Timer()
        timer.schedule(timerTask {
            if (Bike.isStarted)
                doAsync {
                    uiThread {
                        webView.evaluateJavascript(
                        "onBikeDataChanged(${Bike.speed}, ${Bike.maxSpeed}, ${Bike.averageSpeed}, ${Bike.distance}, ${Bike.time}, ${Bike.cadence})", null)
                    }
                }
        }, delay , delay )

        LocationManager.setGpsActive = { doAsync { uiThread { webView.evaluateJavascript("setGpsActive()", null) } } }

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
                doAsync { uiThread { webView.evaluateJavascript("setGpsActive()", null) } }
        }
    }

    private lateinit var webView: WebView
    private lateinit var timer: Timer
    private val delay = 500L
}
