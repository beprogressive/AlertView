package com.gmail.beprogressive.it.alertview

import android.animation.Animator
import android.view.View
import timber.log.Timber

inline fun <reified T> T.log(message: String) =
    Timber.v(message)

fun View.slideOutRight(onAnimationEnd: () -> Unit) {

    animate()
        .setDuration(100)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                visibility = View.GONE
                onAnimationEnd()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
        .translationX(context.resources.displayMetrics.widthPixels.toFloat())
        .start()
}

fun View.slideInRight() {
    animate()
        .setDuration(80)
        .translationX(0F)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
        .start()
}