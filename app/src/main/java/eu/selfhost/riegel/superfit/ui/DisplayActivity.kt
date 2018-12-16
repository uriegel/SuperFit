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

		viewPager = ExtendedViewPager(this)
		val pagerId = 1
		with(viewPager) {
			adapter = PagerAdapter(pagerId, supportFragmentManager)
			id = pagerId
			addOnPageChangeListener(onPageChangeListener)
		}

		setContentView(viewPager)
	}

	var pagingEnabled
		get() = viewPager.pagingEnabled
		set(value) { viewPager.pagingEnabled = value }

	private inner class PagerAdapter(private val pagerId: Int, fm: FragmentManager?)
		: FragmentPagerAdapter(fm)
	{
		override fun getCount() = 2

		override fun getItem(position: Int): Fragment {
			return when (position) {
				0    -> DisplayFragment()
				else -> {
					val fragment = MapFragment()
					val bundle = Bundle()
					bundle.putBoolean(MapFragment.SHOW_TRACKING_CONTROL, true)
					fragment.arguments = bundle
					fragment
				}
			}
		}

		fun getFragmentForPosition(position: Int): Fragment {
			val tag = makeFragmentName(pagerId, getItemId(position))
			return supportFragmentManager.findFragmentByTag(tag)
		}

		private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
	}

	private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
		override fun onPageScrollStateChanged(state: Int) {}

		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

		override fun onPageSelected(position: Int) {
			val mapFragment = ((viewPager.adapter as PagerAdapter).getFragmentForPosition(1) as MapFragment)
			when (position) {
				0    -> mapFragment.enableBearing(false)
				else -> mapFragment.enableBearing(true)
			}
		}
	}

	private lateinit var viewPager: ExtendedViewPager
}
