package com.gmail.beprogressive.it.alertview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.transition.Slide
import androidx.transition.TransitionManager

class AlertView : ConstraintLayout, PopupMenu.OnMenuItemClickListener {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        init()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("alertMessage")
        fun setAlertMessage(view: AlertView, message: String?) {
            setAlertMessage(view, message, null)
        }

        @JvmStatic
        @BindingAdapter("alertMessage", "alertAnimationGravity")
        fun setAlertMessage(view: AlertView, message: String?, alertAnimationGravity: Int?) {
            view.findViewById<TextView>(R.id.alert_tv).text = message

            view.switchVisibilityErrorView(alertAnimationGravity)
            if (message == null || message == "") view.findViewById<TextView>(R.id.alert_tv).visibility =
                View.INVISIBLE
            else view.findViewById<TextView>(R.id.alert_tv).visibility = View.VISIBLE
        }
    }

    private var animation = Slide(Gravity.TOP)

    private fun init() {
        inflate(context, R.layout.view_alert, this)

        setOnClickListener {
            hideErrorView()
        }

        setOnLongClickListener {
            val popupMenu = PopupMenu(context, this)
            popupMenu.menu.add("Copy")
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
            true
        }

//                val a = context.obtainStyledAttributes(attrs, R.styleable.ErrorView, defStyle, 0)
//                val errorMessage = a.getString(R.styleable.ErrorView_errorMessage)
//                a.recycle()
    }

    private fun getErrorMessage(): String? {
        return findViewById<TextView>(R.id.alert_tv).text.toString()
    }

    private fun switchVisibilityErrorView(alertAnimationGravity: Int?) {
        if (alertAnimationGravity != null)
            animation.slideEdge = alertAnimationGravity
        animation.duration = 500
        TransitionManager.beginDelayedTransition(this, animation)
    }

    private fun hideErrorView() {
        switchVisibilityErrorView(null)
        findViewById<TextView>(R.id.alert_tv).visibility = View.INVISIBLE
    }

    fun setMessage(message: String?) {
        findViewById<TextView>(R.id.alert_tv).text = message
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) when (item.title) {
            "Copy" -> {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(null, getErrorMessage())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }
}