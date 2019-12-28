package eu.selfhost.riegel.superfit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.sensors.HeartRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class DisplayFragment : Fragment(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme_DisplayFullScreen)
        // clone the inflater using the ContextThemeWrapper
        // clone the inflater using the ContextThemeWrapper
        val localInflater = inflater.cloneInContext(contextThemeWrapper)

        // inflate the layout using the cloned inflater, not default inflater
        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.fragment_display, container, false)
//        webView = WebView(activity)
//
//        with (webView) {
//            with(settings) {
//                javaScriptEnabled = true
//                domStorageEnabled = true
//                allowFileAccessFromFileURLs = true
//                allowUniversalAccessFromFileURLs = true
//            }
//            addJavascriptInterface(javaScriptInterface, "DisplayNative")
//        }
//        WebView.setWebContentsDebuggingEnabled(true)
//        webView.loadUrl("file:///android_asset/display.html")
//
//        HeartRate.listener = { heartRate -> launch { webView.evaluateJavascript("onHeartRateChanged($heartRate)", null) } }
//        timer = Timer()
//        timer.schedule(timerTask {
//            if (Bike.isStarted)
//                launch {
//                    webView.evaluateJavascript(
//                    "onBikeDataChanged(${Bike.speed}, ${Bike.maxSpeed}, ${Bike.averageSpeed}, ${Bike.distance}, ${Bike.duration}, ${Bike.cadence})", null)
//                }
//        }, delay , delay )
//
//        LocationManager.setGpsActive = { launch { webView.evaluateJavascript("setGpsActive()", null) } }
//
//        return webView
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
