package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityDisplayBinding

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.navButton.setOnClickListener {
            navController.navigate(R.id.dashboardFragment)
        }


        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

//        viewPager = ViewPager2(this)
//        with(viewPager) {
//            id = containerViewId
//            adapter = PagerAdapter(supportFragmentManager)
//            offscreenPageLimit = if (bikeSupport) 2 else 1
//            isUserInputEnabled = bikeSupport
//            registerOnPageChangeCallback(onPageChangeListener)
//        }

//        setContentView(viewPager)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

//    var pagingEnabled
//        get() = false //viewPager.isUserInputEnabled
//        set(value) { } //viewPager.isUserInputEnabled = value }

//    private inner class PagerAdapter(fm: FragmentManager)
//        : FragmentStateAdapter(fm, lifecycle) {
//
//        override fun getItemCount(): Int = if (bikeSupport) 2 else 1
//
//        override fun createFragment(position: Int): Fragment {
//            return when (position) {
//                0    -> if (bikeSupport) DisplayFragment() else MapFragment(true)
//                else -> MapFragment(true)
//            }
//        }
//
////        fun getFragmentForPosition(position: Int): Fragment? {
////            val tag = makeFragmentName(containerViewId, getItemId(position))
////            return supportFragmentManager.findFragmentByTag(tag)
////        }
//
//        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
 //   }

//    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
//
//        override fun onPageSelected(position: Int) {
//            val mapFragment = ((viewPager.adapter as PagerAdapter).getFragmentForPosition(1) as MapFragment?)
//            when (position) {
//                0    -> mapFragment?.enableBearing(false)
//                else -> mapFragment?.enableBearing(true)
//            }
//        }
//    }

    private lateinit var binding: ActivityDisplayBinding
//    private lateinit var viewPager: ViewPager2
    private val bikeSupport by lazy {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.getBoolean(PreferenceFragment.BIKE_SUPPORT, false)
    }
    //private val containerViewId = 2000
}