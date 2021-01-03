package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.R
import de.uriegel.superfit.BR.displayModel
import de.uriegel.superfit.databinding.FragmentDisplayBinding
import de.uriegel.superfit.models.DisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DisplayFragment : Fragment(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main
    lateinit var binding: FragmentDisplayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme_DisplayFullScreen)
        // clone the inflater using the ContextThemeWrapper
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        // inflate the layout using the cloned inflater, not default inflater
        binding =
            DataBindingUtil.inflate(localInflater, R.layout.fragment_display, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setVariable(displayModel, viewModel)
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(DisplayModel::class.java)
    }
}
