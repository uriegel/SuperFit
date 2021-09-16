package de.uriegel.superfit.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.uriegel.superfit.ui.ControlsFragment

class ExtendedPagerAdapter (fm: FragmentManager)
    // TODO switch to viewpager2
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount() = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0    -> ControlsFragment()
            else -> TracksFragment()
        }
    }

//        fun getFragmentForPosition(position: Int): Fragment {
//            val tag = makeFragmentName(pagerId, getItemId(position))
//            return supportFragmentManager.findFragmentByTag(tag)!!
//        }

//        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
}