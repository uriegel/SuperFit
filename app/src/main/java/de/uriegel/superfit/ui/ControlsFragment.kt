package de.uriegel.superfit.ui

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
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
            //startActivity(Intent(activity, DisplayActivity::class.java))
            startActivity(Intent(activity, TrackingActivity::class.java))
        }
        binding.btnDisplay.setOnClickListener { startActivity(Intent(activity, DisplayActivity::class.java))}
        binding.btnMap.setOnClickListener { startActivity(Intent(activity, TrackingActivity::class.java))}
        binding.btnStop.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.stopService(startIntent)
        }
        Service.setOnStateChangedListener { onStateChanged(it) }
    }

    private fun onStateChanged(isRunning: Boolean) {
        binding.btnStart.visibility = if (isRunning) GONE else VISIBLE
        //binding.btnDisplay.visibility = if (isRunning) VISIBLE else GONE
        binding.btnMap.visibility = if (isRunning) VISIBLE else GONE
        binding.btnStop.visibility = if (isRunning) VISIBLE else GONE
    }

    private lateinit var binding: FragmentControlsBinding
}