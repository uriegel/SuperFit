package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.BR.displayModel
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.FragmentDisplayBinding
import de.uriegel.superfit.model.DisplayModel

class DisplayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display, container, false)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(DisplayModel::class.java)
        binding.setVariable(displayModel, viewModel)
        return binding.root
    }

    private lateinit var binding: FragmentDisplayBinding
}