package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import de.uriegel.superfit.BR
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.FragmentDisplayBinding
import de.uriegel.superfit.model.DisplayModel

class DisplayFragment: Fragment() {
    private lateinit var binding: FragmentDisplayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display, container, false)
        binding.lifecycleOwner = this

        binding.navigationToMap.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.TrackingFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(BR.displayModel, viewModel)
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(DisplayModel::class.java)
    }
}