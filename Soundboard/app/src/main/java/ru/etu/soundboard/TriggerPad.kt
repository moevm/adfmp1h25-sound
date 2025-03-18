package ru.etu.soundboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class TriggerPad: View {

    private var mIsDown = false

    val DISPLAY_MASK        = 0x00000003
    val DISPLAY_RECT        = 0x00000000
    val DISPLAY_CIRCLE      = 0x00000001
    val DISPLAY_ROUND_RECT  = 0x00000002

    private var mDisplayFlags = DISPLAY_ROUND_RECT

    interface DrumPadTriggerListener {
        fun triggerDown(pad: TriggerPad)
        fun triggerUp(pad: TriggerPad)
    }

    var mListeners = ArrayList<DrumPadTriggerListener>()

    constructor(context: Context) : super(context)

    //
    // Input Routines
    //
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN ||
                event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
            mIsDown = true
            triggerDown()
            invalidate()
            return true
        } else if (event.actionMasked == MotionEvent.ACTION_UP) {
            mIsDown = false
            triggerUp()
            invalidate()
            return true
        }

        return false
    }

    //
    // Event Listeners
    //
    fun addListener(listener: DrumPadTriggerListener) {
        mListeners.add(listener)
    }

    private fun triggerDown() {
        for( listener in mListeners) {
            listener.triggerDown(this)
        }
    }

    private fun triggerUp() {
        for( listener in mListeners) {
            listener.triggerUp(this)
        }
    }
}
