package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.superfit.R
import de.uriegel.superfit.android.logWarnung
import de.uriegel.superfit.databinding.FragmentTrackBinding
import de.uriegel.superfit.maps.exportToGpx
import de.uriegel.superfit.model.MainViewModel
import kotlinx.coroutines.launch
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import java.util.*

class TrackFragment: MapFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        activityRequest = ActivityRequest(requireActivity())
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val trackNr = requireActivity().intent.getIntExtra(TRACK_NR, -1)

        launch {
            loadGpxTrack(trackNr)
            zoomAndPan()
        }

        binding.btnMenu.setOnClickListener {
            val visibility =
                if (binding.btnDelete.visibility == View.GONE) View.VISIBLE else View.GONE
            binding.btnDelete.visibility = visibility
            binding.btnSave.visibility = visibility
        }

        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    launch { viewModel.deleteTrackAsync(trackNr) }
                    requireActivity().finish()
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
                    val timeZone =
                        TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
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
                            val stream = requireActivity().contentResolver.openOutputStream(uri)
                            stream?.let {
                                viewModel.findTrackPointsAsync(trackNr).await()
                                    ?.let { trackPoints ->
                                        exportToGpx(it, name, track, trackPoints)
                                    }
                                it.close()
                            }
                        }
                        requireActivity().finish()
                    }
                }
            }
        }
        return view
    }

    override fun initializeBinding(): BindingData {
        binding = FragmentTrackBinding.inflate(layoutInflater)
        return BindingData(binding.root, binding.mapContainer)
    }

    private fun zoomAndPan() {
        try {
            val boundingBox = BoundingBox(trackLine.latLongs)
            val width = mapView.width
            val height = mapView.height
            if (width <= 0 || height <= 0)
                return
            val centerPoint = LatLong(
                (boundingBox.maxLatitude + boundingBox.minLatitude) / 2,
                (boundingBox.maxLongitude + boundingBox.minLongitude) / 2
            )
            mapView.setCenter(centerPoint)
        } catch (e: Exception) {
            logWarnung("Zoom and Pan", e)
        }
    }

    private lateinit var binding: FragmentTrackBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var activityRequest: ActivityRequest
}