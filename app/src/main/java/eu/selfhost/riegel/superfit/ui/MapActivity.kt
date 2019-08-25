package eu.selfhost.riegel.superfit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.SoundEffectConstants
import android.webkit.JavascriptInterface
import android.webkit.WebView
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.maps.exportToGpx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.util.*

class MapActivity : ActivityEx(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_map)

		webView = findViewById(R.id.mapViewControls)
		webView.setBackgroundColor(Color.TRANSPARENT)
		webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)

		with(webView.settings)
		{
			javaScriptEnabled = true
			domStorageEnabled = true
			allowFileAccessFromFileURLs = true
			allowUniversalAccessFromFileURLs = true
		}
		webView.addJavascriptInterface(javaScriptInterface, "NativeMapControls")
		webView.loadUrl("file:///android_asset/index.html#map-view-controls")

		trackNr = intent.getLongExtra(TracksFragment.TRACK_NR, -1L)
		launch {
			val trackPoints = DataBase.getTrackPoints(trackNr)
			val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
			fragment.loadGpxTrack(trackPoints)
		}
	}

	private val javaScriptInterface = object {
		@JavascriptInterface
		fun doHapticFeedback() = launch { webView.playSoundEffect(SoundEffectConstants.CLICK) }

		@Suppress("DEPRECATION")
		@JavascriptInterface
		fun saveTrack()
		{
			doHapticFeedback()
			launch {
				val track = DataBase.getTrack(trackNr)

				val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
				val date = (Date(track.time+ track.timeOffset - timeZone))
				val name = if (track.name.isEmpty()) "${date.year + 1900}-${(date.month + 1).toString().padStart(2, '0')}-${date.date.toString().padStart(2, '0')}-${date.hours.toString().padStart(2, '0')}-${date.minutes.toString().padStart(2, '0')}" else track.name

				val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
				intent.addCategory(Intent.CATEGORY_OPENABLE)
				intent.type = "application/gpx+xml"
				intent.putExtra(Intent.EXTRA_TITLE, "$name.gpx")
				val result = activityRequest(intent)
				if (result?.resultCode == Activity.RESULT_OK) {
					val uri = result.data?.data!!
					val stream = contentResolver.openOutputStream(uri)
					stream.let {
						val trackPoints = DataBase.getTrackPoints(trackNr)
						exportToGpx(it, name, track, trackPoints)
						it.close()
					}
					this@MapActivity.finish()
				}
			}
		}

		@JavascriptInterface
		fun deleteTrack() {
			doHapticFeedback()
			launch {
				alert("Möchtest Du diesen Track löschen?", "Track löschen") {
					yesButton {
						DataBase.deleteTrack(trackNr)
						val intent = Intent()
						intent.putExtra(RESULT_TYPE, RESULT_TYPE_DELETE)
						setResult(Activity.RESULT_OK, intent)
						this@MapActivity.finish()
					}
					noButton {}
				}.show()
			}
		}
	}

	companion object {
		const val RESULT_TYPE = "RESULT_TYPE"
		const val RESULT_TYPE_DELETE = "RESULT_TYPE_DELETE"
	}

	private lateinit var webView: WebView
	private var trackNr = 0L
}

