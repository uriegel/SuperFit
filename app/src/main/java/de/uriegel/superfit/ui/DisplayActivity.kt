package de.uriegel.superfit.ui


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

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

	private inner class PagerAdapter(private val pagerId: Int, fm: FragmentManager)
		: FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
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
			return supportFragmentManager.findFragmentByTag(tag)!!
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
