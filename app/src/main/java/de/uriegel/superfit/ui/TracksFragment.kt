package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.databinding.FragmentTracksBinding
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.ui.MapFragment.Companion.TRACK_NR
import de.uriegel.superfit.ui.adapters.TrackListAdapter

class TracksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerSetup {
            val intent = Intent(activity, TrackActivity::class.java)
            intent.putExtra(TRACK_NR, it.id)
            startActivity(intent)
        }
        observerSetup()
    }

    private fun observerSetup() {
        viewModel.allTracks.observe(viewLifecycleOwner, { tracks ->
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

    private var binding: FragmentTracksBinding
        get() = _binding!!
        set(value) { _binding = value }
    private var _binding: FragmentTracksBinding? = null


    private val viewModel: MainViewModel by viewModels()
    private var adapter: TrackListAdapter? = null
}