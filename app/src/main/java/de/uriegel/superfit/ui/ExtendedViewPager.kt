package de.uriegel.superfit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ExtendedViewPager(context: Context, attrs: AttributeSet?)
	: ViewPager(context, attrs) {

	constructor(context: Context): this(context, null)
	var pagingEnabled = true

	@SuppressLint("ClickableViewAccessibility")
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