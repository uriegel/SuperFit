package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.BR
import de.uriegel.superfit.databinding.FragmentDisplayBinding
import de.uriegel.superfit.model.DisplayModel

class DisplayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding.lifecycleOwner = this
        binding.setVariable(BR.displayModel, viewModel)
        return binding.root
    }

    private val binding by lazy {
        FragmentDisplayBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(DisplayModel::class.java)
    }
}