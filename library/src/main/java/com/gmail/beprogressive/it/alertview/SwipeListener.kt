package com.gmail.beprogressive.it.alertview

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.transition.Slide
import kotlin.math.abs
import kotlin.math.sqrt


class SwipeListener(private val view: AlertView) : View.OnTouchListener {

    private val longPressHandler: Handler = Handler(Looper.getMainLooper())
    private var longPressedRunnable = Runnable {
        // Long press detected in long press Handler!
        view.performLongClick()
        isLongPressHandlerActivated = true
    }

    private var isLongPressHandlerActivated = false

    private var isActionMoveEventStored = false
    private var lastActionMoveEventBeforeUpX = 0f
    private var lastActionMoveEventBeforeUpY = 0f

    private var initialX = 0F
    private var initialY = 0F

    private var dX = 0F
    private var moveHorizontal = false

    override fun onTouch(v: View?, event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            initialX = event.x
            initialY = event.y

            view.onTouchAlert(true)

            moveHorizontal = false
            longPressHandler.postDelayed(longPressedRunnable, 1000)
            dX = view.x - event.rawX
        }
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_HOVER_MOVE) {
            if (!isActionMoveEventStored) {
                if (event.x - initialX < 2 && event.y - initialY < 2) return true
                isActionMoveEventStored = true
                lastActionMoveEventBeforeUpX = event.x
                lastActionMoveEventBeforeUpY = event.y
            } else {
                val currentX = event.x
                val currentY = event.y
                val firstX: Float = lastActionMoveEventBeforeUpX
                val firstY: Float = lastActionMoveEventBeforeUpY
                val distance = sqrt(
                    (
                            (currentY - firstY) * (currentY - firstY) + (currentX - firstX) * (currentX - firstX)).toDouble()
                )
                if (distance > 10) {
                    longPressHandler.removeCallbacks(longPressedRunnable)
                    moveHorizontal = true
                    view.animate()
                        .x(event.rawX + dX)
                        .setDuration(0)
                        .start()
                }
            }
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            val clickHandler = isActionMoveEventStored
            isActionMoveEventStored = false
            longPressHandler.removeCallbacks(longPressedRunnable)

            if (isLongPressHandlerActivated) {
                // Long Press detected; halting propagation of motion event
                isLongPressHandlerActivated = false
                return false
            }

            if (!clickHandler) {
                view.performClick()
            }

            if (moveHorizontal && shouldClose(view.x))
                view.switchVisibilityAlertView(false, Slide(Gravity.END))
            else if (moveHorizontal && shouldClose(abs(view.x)))
                view.switchVisibilityAlertView(false, Slide(Gravity.START))
            else view.animate()
                .x(0F)
                .setDuration(200)
                .start()

            view.onTouchAlert(false)
        }
        return true
    }

    private fun shouldClose(delta: Float): Boolean {
        return delta > view.measuredWidth / 4
    }
}