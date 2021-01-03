package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.R
import de.uriegel.superfit.database.DataBase
import de.uriegel.superfit.database.Track
import de.uriegel.superfit.databinding.FragmentTracksBinding
import de.uriegel.superfit.ui.adapters.TracksAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TracksFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTracksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.tracksView.layoutManager = linearLayoutManager
        binding.tracksView.setHasFixedSize(true)
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.tracksView.addItemDecoration(itemDecoration)

        fun onItemClick(track: Track) {
            launch {
                val intent = Intent(activity, MapActivity::class.java)
                intent.putExtra(TRACK_NR, track.trackNr.toLong())
                val result = (activity as ActivityEx).activityRequest(intent)
                if (result?.resultCode == Activity.RESULT_OK)
                    if (result.data?.getStringExtra(MapActivity.RESULT_TYPE) == MapActivity.RESULT_TYPE_DELETE)
                        (binding.tracksView.adapter as TracksAdapter).delete(track)
            }
        }

        launch {
            val tracks = DataBase.getTracks()
            binding.tracksView.adapter = TracksAdapter(requireActivity(), tracks, ::onItemClick)
        }
    }

    private lateinit var binding: FragmentTracksBinding

    companion object {
        const val TRACK_NR = "TRACK_NR"
    }
}
