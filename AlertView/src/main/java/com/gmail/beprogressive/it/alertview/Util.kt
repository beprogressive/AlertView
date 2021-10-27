package com.gmail.beprogressive.it.alertview

import android.animation.Animator
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import timber.log.Timber

inline fun <reified T> T.log(message: String) =
    Timber.v(message)

fun View.slideOutRight(onAnimationEnd: () -> Unit) {
    YoYo.with(Techniques.SlideOutRight)
        .duration(100)
        .repeat(0)
        .withListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                onAnimationEnd()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        .playOn(this)
}

fun View.slideInRight() {
    YoYo.with(Techniques.SlideInRight)
        .duration(100)
        .repeat(0)
        .withListener(object : Animator.AnimatorListener {
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
        .playOn(this)
}