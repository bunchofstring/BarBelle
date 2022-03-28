package com.bunchofstring.experimental.barbelle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

/**
 * 1. When the content starts scrolling, then highlight the scrollbar thumb
 * 2. When the content stops scrolling, then un-highlight the scrollbar thumb
 * 3. Note: Ideally the solution can easily be applied for all scrollable views (1) within one app (2) within all apps running on a particular android user (3) within any installed app on a particular device
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        setupInteractionListeners(findViewById(R.id.exampleScrollView))
    }

    private fun setupInteractionListeners(sv: NestedScrollView){
        val mid = MotionInteractionDetector(object: MotionInteractionDetector.MotionInteractionObserver{
            override fun onStart() {
                sv.isPressed = true
            }
            override fun onEnd() {
                sv.isPressed = false
            }
        })
        attachScrollView(sv,mid)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun attachScrollView(sv: NestedScrollView, mid: MotionInteractionDetector) {
        sv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            mid.onMotionY()
        })
        sv.setOnTouchListener { p0, p1 ->
            when(p1.action){
                MotionEvent.ACTION_DOWN -> mid.onInteractionStart()
                MotionEvent.ACTION_UP -> mid.onInteractionEnd()
            }
            false
        }
    }
}
