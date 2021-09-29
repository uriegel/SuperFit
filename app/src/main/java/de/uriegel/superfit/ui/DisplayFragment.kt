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
        binding = FragmentDisplayBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.displayModel, viewModel)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private var binding: FragmentDisplayBinding
        get() = _binding!!
        set(value) { _binding = value }
    private var _binding: FragmentDisplayBinding? = null

    private val viewModel by lazy {
        ViewModelProvider(this).get(DisplayModel::class.java)
    }
}