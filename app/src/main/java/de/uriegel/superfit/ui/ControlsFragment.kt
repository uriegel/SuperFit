package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.uriegel.superfit.R
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import de.uriegel.superfit.sensors.Searcher
import kotlinx.android.synthetic.main.fragment_controls.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.*

class ControlsFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_controls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnStart.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.startService(startIntent)
            startActivity(Intent(activity, DisplayActivity::class.java))
        }
        btnDisplay.setOnClickListener { launch { startActivity(Intent(activity, DisplayActivity::class.java))}}
        btnReset.setOnClickListener {
            launch {
                activity?.alert("Möchtest Du die App resetten?", "Reset") {
                    yesButton {
                        Searcher.stop()
                        HeartRate.stop()
                        Bike.stop()
                        activity?.let { Searcher.start(activity!!) }
                    }
                    noButton {}
                }?.show()
            }
        }
        btnStop.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.stopService(startIntent)
            activity?.finish()
        }

        Service.setOnStateChangedListener { onStateChanged(it) }
    }

    private fun onStateChanged(isRunning: Boolean) {
        btnStart.isEnabled = !isRunning
        btnDisplay.isEnabled = isRunning
        btnReset.isEnabled = isRunning
        btnStop.isEnabled = isRunning
    }
}
