package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.uriegel.superfit.BR
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityDisplayBinding
import de.uriegel.superfit.model.DisplayModel

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(DisplayModel::class.java)
        binding.setVariable(BR.displayModel, viewModel)

        val pagingEnabledObserver = Observer<Boolean> {
            binding.viewPager.isUserInputEnabled = it
        }
        binding.displayModel?.pagingEnabled?.observe(this, pagingEnabledObserver)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences?.getBoolean(PreferenceFragment.BIKE_SUPPORT, false)?.let {
            bikeSupport = it
        }

        with(binding.viewPager) {
            adapter = PagerAdapter(supportFragmentManager)
            offscreenPageLimit = if (bikeSupport) 2 else 1
            isUserInputEnabled = bikeSupport
        }
    }

    private inner class PagerAdapter(fm: FragmentManager)
        : FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int = if (bikeSupport) 2 else 1

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0    -> if (bikeSupport) DisplayFragment() else TrackingFragment()
                else -> TrackingFragment()
            }
        }
    }

    private lateinit var binding: ActivityDisplayBinding
    private var bikeSupport = false
}