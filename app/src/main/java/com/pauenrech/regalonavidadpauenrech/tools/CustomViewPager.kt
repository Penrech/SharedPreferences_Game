package com.pauenrech.regalonavidadpauenrech.tools

import android.content.Context
import android.view.MotionEvent
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var enable: Boolean = false

    init {
        this.enable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.enable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.enable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enable = enabled
    }
}