package com.perron.webviewtest

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView

class NestedScrollableWebview(context: Context): WebView(context), View.OnTouchListener {

    var scrollable = false

    val range: Int
        get() {
            return computeVerticalScrollRange()
        }

    val extent: Int
        get() {
            return computeVerticalScrollExtent()
        }

    init {
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(scrollable) {
            if (event?.action == MotionEvent.ACTION_SCROLL || event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_DOWN) {
                v?.parent?.requestDisallowInterceptTouchEvent(true)
            }
            v?.performClick()
            return false
        } else {
            return true
        }
    }
}