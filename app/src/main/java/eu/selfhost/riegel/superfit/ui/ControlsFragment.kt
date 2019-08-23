package eu.selfhost.riegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import eu.selfhost.riegel.superfit.android.Service
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import eu.selfhost.riegel.superfit.sensors.Searcher
import eu.selfhost.riegel.superfit.utils.serialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ControlsFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        webView = WebView(activity)

        with(webView)
        {
            settings.javaScriptEnabled = true
            addJavascriptInterface(javaScriptInterface, "Native")
            webView.loadUrl("file:///android_asset/index.html#main")

        }
        WebView.setWebContentsDebuggingEnabled(true)

        Service.setOnStateChangedListener { onStateChanged(it) }

        return webView
    }

    private val javaScriptInterface = object {
        @JavascriptInterface
        fun getState() = launch { onStateChanged(Service.state) }

        @JavascriptInterface
        fun doHapticFeedback() = launch { webView.playSoundEffect(SoundEffectConstants.CLICK) }

        @JavascriptInterface
        fun start() = launch {
            val startIntent = Intent(activity, Service::class.java)
            startIntent.action = Service.ACTION_START
            activity?.startService(startIntent)
            startActivity(Intent(activity, DisplayActivity::class.java))
        }

        @JavascriptInterface
        fun stop() = launch {
            val startIntent = Intent(activity, Service::class.java)
            startIntent.action = Service.ACTION_STOP
            activity?.startService(startIntent)
            activity?.finish()
        }

        @JavascriptInterface
        fun reset() {
            // TODO: Confirmation
//            Searcher.stop()
//            HeartRate.stop()
//            Bike.stop()
//            activity?.let { Searcher.start(it) }
        }

        @JavascriptInterface
        fun display() = launch { startActivity(Intent(activity, DisplayActivity::class.java)) }

        @JavascriptInterface
        fun finish() = launch { activity?.onBackPressed() }
    }

    private fun onStateChanged(state: Service.ServiceState) = webView.evaluateJavascript("onStateChanged(${state.serialize()})", null)

    private lateinit var webView: WebView
}
