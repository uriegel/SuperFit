package eu.selfhost.riegel.superfit.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.view.MotionEvent

class ExtendedViewPager(context: Context) : ViewPager(context) {
	var pagingEnabled = true

	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return if (pagingEnabled)
			super.onTouchEvent(event)
		else
			false
	}

	override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
		return if (pagingEnabled)
			super.onInterceptTouchEvent(ev)
		else
			false
	}
}