package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import de.uriegel.superfit.R
import de.uriegel.superfit.database.DataBase
import de.uriegel.superfit.maps.exportToGpx
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.util.*

@Suppress("DEPRECATION")
class MapActivity : ActivityEx(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_map)

		btnMenu.setOnClickListener {
			controlsActive = !controlsActive
			val visibility = if (controlsActive) View.VISIBLE else View.GONE
			btnDelete.visibility = visibility
			btnEdit.visibility = visibility
			btnSave.visibility = visibility
		}

		btnDelete.setOnClickListener {
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

		btnSave.setOnClickListener {
			launch {
				val track = DataBase.getTrack(trackNr)

				val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
				val date = (Date(track.time + track.timeOffset - timeZone))
				val name = if (track.name.isEmpty())
					"${date.year + 1900}" +
							"-${(date.month + 1).toString().padStart(2, '0')}" +
							"-${date.date.toString().padStart(2, '0')}" +
							"-${date.hours.toString().padStart(2, '0')}" +
							"-${date.minutes.toString().padStart(2, '0')}"
				else track.name

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
						exportToGpx(it!!, name, track, trackPoints)
						it.close()
					}
					this@MapActivity.finish()
				}
			}
		}

		trackNr = intent.getLongExtra(TracksFragment.TRACK_NR, -1L)
		launch {
			val trackPoints = DataBase.getTrackPoints(trackNr)
			val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
			fragment.loadGpxTrack(trackPoints)
		}
	}

	companion object {
		const val RESULT_TYPE = "RESULT_TYPE"
		const val RESULT_TYPE_DELETE = "RESULT_TYPE_DELETE"
	}

	private var trackNr = 0L
	private var controlsActive = false
}

