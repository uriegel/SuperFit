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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.*
import java.util.*

class MapActivity : ActivityEx()
{
	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_map)

		webView = findViewById<WebView>(R.id.mapViewControls)
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

		webView.loadUrl("file:///android_asset/mapViewControls.html")

		val bundle = intent.extras
		trackNr = bundle.getLong(MainActivity.TRACK_NR)
		async(UI)
		{
			val trackPoints = DataBase.getTrackPointsAsync(trackNr).await()
			val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
			fragment.loadGpxTrack(trackPoints)
		}
	}

	private val javaScriptInterface = object
	{
		@JavascriptInterface
		fun doHapticFeedback() = doAsync { uiThread { webView.playSoundEffect(SoundEffectConstants.CLICK) } }

		@Suppress("DEPRECATION")
		@JavascriptInterface
		fun saveTrack()
		{
			doHapticFeedback()
			async(UI)
			{
				val track = DataBase.getTrackAsync(trackNr).await()
				val date = Date(track.time)
				val name = if (track.name.isEmpty()) "${date.year + 1900}-${date.month + 1}-${date.date}-${date.hours}-${date.minutes}" else track.name

				val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
				intent.addCategory(Intent.CATEGORY_OPENABLE)
				intent.type = "text/xml"
				intent.putExtra(Intent.EXTRA_TITLE, "$name.gpx")
				val result = activityRequest(intent)
				if (result?.resultCode == Activity.RESULT_OK)
				{
					val uri = result.data?.data!!
					val stream = contentResolver.openOutputStream(uri)
					val trackPoints = DataBase.getTrackPointsAsync(trackNr).await()
					exportToGpx(stream, name, track, trackPoints)
					stream.close()
					this@MapActivity.finish()
				}
			}
		}

		@JavascriptInterface
		fun deleteTrack()
		{
			doHapticFeedback()
			async(UI) {
				alert("Möchtest Du diesen Track löschen?", "Track löschen") {
					yesButton {
						async(UI) {
							DataBase.deleteTrackAsync(trackNr).await()
							val intent = Intent()
							intent.putExtra(RESULT_TYPE, RESULT_TYPE_DELETE)
							setResult(Activity.RESULT_OK, intent)
							this@MapActivity.finish()
						}
					}
					noButton {}
				}.show()
			}
		}
	}

	companion object
	{
		const val RESULT_TYPE = "RESULT_TYPE"
		const val RESULT_TYPE_DELETE = "RESULT_TYPE_DELETE"
	}

	private lateinit var webView: WebView
	private var trackNr = 0L
}

