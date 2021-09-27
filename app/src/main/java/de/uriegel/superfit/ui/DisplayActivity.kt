package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.uriegel.superfit.databinding.ActivityDisplayBinding

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

    var pagingEnabled
        get() = binding.viewPager.isUserInputEnabled
        set(value) { binding.viewPager.isUserInputEnabled = value }

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

    private val binding by lazy {
        ActivityDisplayBinding.inflate(layoutInflater)
    }
    private var bikeSupport = false
}