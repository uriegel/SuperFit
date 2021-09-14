package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.databinding.FragmentControlsBinding
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import de.uriegel.superfit.sensors.Searcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ControlsFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

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
        binding.btnDisplay.setOnClickListener { launch { startActivity(Intent(activity, DisplayActivity::class.java))}}
        binding.btnReset.setOnClickListener {
//            launch {
//                activity?.alert("Möchtest Du die App resetten?", "Reset") {
//                    yesButton {
//                        Searcher.stop()
//                        HeartRate.stop()
//                        Bike.stop()
//                        activity?.let { Searcher.start(requireActivity()) }
//                    }
//                    noButton {}
//                }?.show()
//            }
        }
        binding.btnStop.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.stopService(startIntent)
            activity?.finish()
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
