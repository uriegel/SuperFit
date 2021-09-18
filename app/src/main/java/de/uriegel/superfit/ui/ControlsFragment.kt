package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.databinding.FragmentControlsBinding

class ControlsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentControlsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.startService(startIntent)
            startActivity(Intent(activity, DisplayActivity::class.java))
        }

        Service.setOnStateChangedListener { onStateChanged(it) }
    }

    private fun onStateChanged(isRunning: Boolean) {
        binding.btnStart.isEnabled = !isRunning
        binding.btnDisplay.isEnabled = isRunning
        binding.btnReset.isEnabled = isRunning
        binding.btnStop.isEnabled = isRunning
    }

    private lateinit var binding: FragmentControlsBinding
}