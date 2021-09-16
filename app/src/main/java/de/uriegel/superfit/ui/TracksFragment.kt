package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.databinding.FragmentTracksBinding
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.ui.adapters.TrackListAdapter

class TracksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTracksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerSetup {
//            launch {
//                val intent = Intent(activity, MapActivity::class.java)
//                intent.putExtra(TRACK_NR, track.trackNr.toLong())
//                val result = (activity as ActivityEx).activityRequest(intent)
//                if (result?.resultCode == Activity.RESULT_OK)
//                    if (result.data?.getStringExtra(MapActivity.RESULT_TYPE) == MapActivity.RESULT_TYPE_DELETE)
//                        (binding.tracksView.adapter as TracksAdapter).delete(track)
//            }
        }
        observerSetup()
    }

    private fun observerSetup() {
        viewModel.getAllTracks()?.observe(viewLifecycleOwner, { tracks ->
            tracks?.let {
                adapter?.setTrackList(it)
            }
        })
    }

    private fun recyclerSetup(onItemClick: (track: Track)->Unit) {
        adapter = TrackListAdapter(requireActivity(), onItemClick)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.tracksView.layoutManager = linearLayoutManager
        binding.tracksView.setHasFixedSize(true)
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.tracksView.addItemDecoration(itemDecoration)
        binding.tracksView.adapter = adapter
    }

    private lateinit var binding: FragmentTracksBinding
    private val viewModel: MainViewModel by viewModels()
    private var adapter: TrackListAdapter? = null

//    companion object {
//        const val TRACK_NR = "TRACK_NR"
//    }
}