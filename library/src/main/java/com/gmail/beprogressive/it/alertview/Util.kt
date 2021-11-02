package com.gmail.beprogressive.it.alertview

import android.animation.Animator
import android.util.Log
import android.view.View

inline fun <reified T> T.log(message: String) {
    var tag: String? = T::class.java.enclosingClass?.simpleName
    if (tag == null)
        tag = T::class.java.simpleName
    Log.v(tag, message)
}

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