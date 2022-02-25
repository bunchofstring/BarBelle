package com.bunchofstring.experimental.barbelle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

/**
 * 1. When the content starts scrolling, then highlight the scrollbar thumb
 * 2. When the content stops scrolling, then un-highlight the scrollbar thumb
 * 3. Note: Ideally the solution can easily be applied for all scrollable views (1) within one app (2) within all apps running on a particular android user (3) within any installed app
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manageThumbState(findViewById(R.id.exampleScrollView))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun manageThumbState(sv: NestedScrollView){
        sv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            v.isPressed = true
        })
        sv.setOnTouchListener { p0, p1 ->
            if (p1?.action == MotionEvent.ACTION_UP) {
                p0?.postDelayed({
                    p0.isPressed = false
                }, 1000) //TODO: Eliminate this ugly delay. Problem is that after the finger lifts off, the scrolling animation may take a while to finish - even longer than a second. Ideally, isPressed is set after the scrolling animation ends.
            }
            false
        }
    }
}
