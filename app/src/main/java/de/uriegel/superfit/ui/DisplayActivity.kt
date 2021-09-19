package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewPager = ViewPager2(this)
        with(viewPager) {
            id = containerViewId
            adapter = PagerAdapter(supportFragmentManager)
            offscreenPageLimit = if (bikeSupport) 2 else 1
            isUserInputEnabled = bikeSupport
            registerOnPageChangeCallback(onPageChangeListener)
        }

        setContentView(viewPager)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewPager).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    var pagingEnabled
        get() = viewPager.isUserInputEnabled
        set(value) { viewPager.isUserInputEnabled = value }

    private inner class PagerAdapter(fm: FragmentManager)
        : FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int = if (bikeSupport) 2 else 1

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0    -> if (bikeSupport) DisplayFragment() else MapFragment(true)
                else -> MapFragment(true)
            }
        }

        fun getFragmentForPosition(position: Int): Fragment? {
            val tag = makeFragmentName(containerViewId, getItemId(position))
            return supportFragmentManager.findFragmentByTag(tag)
        }

        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
    }

    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            val mapFragment = ((viewPager.adapter as PagerAdapter).getFragmentForPosition(1) as MapFragment?)
            when (position) {
                0    -> mapFragment?.enableBearing(false)
                else -> mapFragment?.enableBearing(true)
            }
        }

        override fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> Log.w("SF", "SCROLL_STATE_IDLE")
                ViewPager2.SCROLL_STATE_DRAGGING -> Log.w("SF", "SCROLL_STATE_DRAGGING")
                ViewPager2.SCROLL_STATE_SETTLING -> Log.w("SF", "SCROLL_STATE_SETTLING")
            }
        }
    }

    private lateinit var viewPager: ViewPager2
    private val bikeSupport by lazy {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.getBoolean(PreferenceFragment.BIKE_SUPPORT, false)
    }
    private val containerViewId = 2000
}