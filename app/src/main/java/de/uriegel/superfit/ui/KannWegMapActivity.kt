package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityMapBinding
import de.uriegel.superfit.maps.exportToGpx
import de.uriegel.superfit.model.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class KannWegMapActivity: AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            val visibility = if (binding.btnDelete.visibility == View.GONE) View.VISIBLE else View.GONE
            binding.btnDelete.visibility = visibility
            binding.btnEdit.visibility = visibility
            binding.btnSave.visibility = visibility
        }

        binding.btnDelete.setOnClickListener {
           val builder = AlertDialog.Builder(this)
           builder.apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    launch { viewModel.deleteTrackAsync(trackNr) }
                    finish()
                }
               setNegativeButton(R.string.cancel) { _, _ -> }
           }
            val dialog = builder
                .setMessage(getString(R.string.alert_delete_track))
                .setTitle(getString(R.string.alert_title_delete_track))
                .create()
            dialog.show()
        }

        binding.btnSave.setOnClickListener {
            launch {
                viewModel.findTrackAsync(trackNr).await()?.let { track ->
                    val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
                    val date = (Date(track.time + track.timeOffset - timeZone))
                    val cal = Calendar.getInstance()
                    cal.time = date

                    val name: String = if (track.trackName?.isNotEmpty() == true)
                        track.trackName!!
                    else {
                        "${cal.get(Calendar.YEAR)}" +
                                "-${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}" +
                                "-${cal.get(Calendar.DATE).toString().padStart(2, '0')}" +
                                "-${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}" +
                                "-${cal.get(Calendar.MINUTE).toString().padStart(2, '0')}"
                    }

                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "application/gpx+xml"
                    intent.putExtra(Intent.EXTRA_TITLE, "$name.gpx")
                    val result = activityRequest.launch(intent)
                    if (result.resultCode == Activity.RESULT_OK) {
                        val uri = result.data?.data!!
                        runCatching {
                            val stream = contentResolver.openOutputStream(uri)
                            stream?.let {
                                viewModel.findTrackPointsAsync(trackNr).await()?.let { trackPoints ->
                                    exportToGpx(it, name, track, trackPoints)
                                }
                                it.close()
                            }
                        }
                        this@KannWegMapActivity.finish()
                    }
                }
            }
        }

        trackNr = intent.getIntExtra(TracksFragment.TRACK_NR, -1)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
        fragment.loadGpxTrack(trackNr)
        launch {
            viewModel.findTrackPointsAsync(trackNr).await()?.let {
            }
        }
    }

    override val coroutineContext = Dispatchers.Main

    private val activityRequest: ActivityRequest = ActivityRequest(this)

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMapBinding
    private var trackNr: Int = -1
}
