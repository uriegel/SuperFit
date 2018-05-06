package eu.selfhost.riegel.superfit.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.WindowManager

class DisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewPager = ViewPager(this)
        viewPager.adapter = PagerAdapter(supportFragmentManager)
        viewPager.id = 1

        setContentView(viewPager)
    }

    private inner class PagerAdapter(fm: FragmentManager?)
        : FragmentPagerAdapter(fm) {

        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return DisplayFragment()
                else -> return MapFragment()
            }
        }

        fun getFragmentForPosition(viewPager: ViewPager, position: Int): Fragment {
            val tag = makeFragmentName(viewPager.getId(), getItemId(position))
            val fragment = supportFragmentManager.findFragmentByTag(tag)
            return fragment
        }

        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
    }

    private lateinit var viewPager: ViewPager
}
