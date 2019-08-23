package eu.selfhost.riegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.utils.getSdCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class TracksFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        webView = WebView(activity)

        with(webView)
        {
            with(settings)
            {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            addJavascriptInterface(javaScriptInterface, "Native")
            webView.loadUrl("file:///android_asset/index.html#tracks")

        }
        WebView.setWebContentsDebuggingEnabled(true)

        return webView
    }

    private val javaScriptInterface = object {
        @JavascriptInterface
        fun doHapticFeedback() = launch { webView.playSoundEffect(SoundEffectConstants.CLICK) }

        @JavascriptInterface
        fun fillTracks() {
            launch {
                val tracks = DataBase.getTracks()
                val gson = Gson()
                val json = gson.toJson(tracks)
                webView.evaluateJavascript("onTracks($json)", null)
            }
        }

        @JavascriptInterface
        fun fillMaps() {
            launch {
                val sdCard: String = activity.getSdCard()
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
        fun onTrackSelected(trackNr: Long) = launch {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra(TRACK_NR, trackNr)
            val result = (activity as ActivityEx).activityRequest(intent)
            if (result?.resultCode == Activity.RESULT_OK) {
                if (result.data?.getStringExtra(MapActivity.RESULT_TYPE) == MapActivity.RESULT_TYPE_DELETE)
                    webView.evaluateJavascript("deleteTrack($trackNr)", null)
            }
        }
    }

    private lateinit var webView: WebView

    companion object {
        const val TRACK_NR = "TRACK_NR"
    }
}
