package eu.selfhost.riegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.android.Service
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import eu.selfhost.riegel.superfit.sensors.Searcher
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
            startIntent.action = Service.ACTION_START
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
            startIntent.action = Service.ACTION_STOP
            activity?.startService(startIntent)
            activity?.finish()
        }

        Service.setOnStateChangedListener { onStateChanged(it) }
    }

    private fun onStateChanged(state: Service.ServiceState) {
        btnStart.isEnabled = state == Service.ServiceState.Stopped
        btnDisplay.isEnabled = state == Service.ServiceState.Started
        btnReset.isEnabled = state == Service.ServiceState.Started
        btnStop.isEnabled = state == Service.ServiceState.Started
    }
}
