package ru.etu.soundboard

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton

class SideButton : androidx.appcompat.widget.AppCompatButton {

    private var mIsDown = false

    interface SideButtonListener {
        fun onButtonDown(button: SideButton)
        fun onButtonUp(button: SideButton)
    }

    private val mListeners = ArrayList<SideButtonListener>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mIsDown = true
                triggerDown()
                return false
            }
            MotionEvent.ACTION_UP -> {
                mIsDown = false
                triggerUp()
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    fun addListener(listener: SideButtonListener) {
        mListeners.add(listener)
    }

    private fun triggerDown() {
        for (listener in mListeners) {
            listener.onButtonDown(this)
        }
    }

    private fun triggerUp() {
        for (listener in mListeners) {
            listener.onButtonUp(this)
        }
    }
}