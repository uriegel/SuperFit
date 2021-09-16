package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.uriegel.superfit.databinding.FragmentTracksBinding

class TracksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTracksBinding.inflate(layoutInflater)
        return binding.root
    }

    private lateinit var binding: FragmentTracksBinding

    companion object {
        const val TRACK_NR = "TRACK_NR"
    }
}