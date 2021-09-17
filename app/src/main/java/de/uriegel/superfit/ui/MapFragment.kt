package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.FragmentTrackingBinding

class MapFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        : View {

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        frameLayout = binding.root.findViewById(R.id.mapContainer)

        return binding.root
    }

    private lateinit var frameLayout: FrameLayout
    private lateinit var binding: FragmentTrackingBinding
}