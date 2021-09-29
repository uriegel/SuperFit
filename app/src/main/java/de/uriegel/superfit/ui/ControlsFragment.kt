package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import de.uriegel.superfit.R
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.databinding.FragmentControlsBinding

class ControlsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentControlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            val startIntent = Intent(activity, Service::class.java)
            activity?.startService(startIntent)
            startActivity(Intent(activity, DisplayActivity::class.java))
        }
        binding.btnDisplay.setOnClickListener { startActivity(Intent(activity, DisplayActivity::class.java))}
        binding.btnStop.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    val startIntent = Intent(activity, Service::class.java)
                    activity?.stopService(startIntent)
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }
            val dialog = builder
                .setMessage(getString(R.string.alert_stop_service))
                .setTitle(getString(R.string.alert_title_stop_service))
                .create()
            dialog.show()

        }
        Service.setOnStateChangedListener { onStateChanged(it) }
    }

    private fun onStateChanged(isRunning: Boolean) {
        binding.btnStart.visibility = if (isRunning) GONE else VISIBLE
        binding.btnDisplay.visibility = if (isRunning) VISIBLE else GONE
        binding.btnStop.visibility = if (isRunning) VISIBLE else GONE
    }

    private var binding: FragmentControlsBinding
        get() = _binding!!
        set(value) { _binding = value }
    private var _binding: FragmentControlsBinding? = null
}