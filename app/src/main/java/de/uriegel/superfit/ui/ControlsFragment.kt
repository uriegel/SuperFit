package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.BR
import de.uriegel.superfit.R
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.databinding.FragmentControlsBinding
import de.uriegel.superfit.model.ControlsModel

class ControlsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_controls, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val viewModel = ViewModelProvider(this).get(ControlsModel::class.java)
        binding.setVariable(BR.controlsModel, viewModel)
        return binding.root
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
    }

    private lateinit var binding: FragmentControlsBinding
}