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
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.transition.Slide
import androidx.transition.TransitionManager

@BindingMethods(
    BindingMethod(
        type = AlertView::class,
        attribute = "set_alert_message",
        method = "setAlertMessage"
    )
)
class AlertView : ConstraintLayout, PopupMenu.OnMenuItemClickListener {

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
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

        val a = context.obtainStyledAttributes(attrs, R.styleable.AlertView, 0, 0)
        val alertAnimationGravity =
            a.getInt(R.styleable.AlertView_alert_animation_gravity, Gravity.TOP)
        val alertMessage = a.getString(R.styleable.AlertView_set_alert_message)
        animation = Slide(alertAnimationGravity)
        setAlertMessage(alertMessage)
        a.recycle()
    }

    private var animation = Slide(Gravity.TOP)

    private fun getErrorMessage(): String? {
        return findViewById<TextView>(R.id.alert_tv).text.toString()
    }

    private fun switchVisibilityErrorView() {
        animation.duration = 500
        TransitionManager.beginDelayedTransition(this, animation)
    }

    private fun hideErrorView() {
        switchVisibilityErrorView()
        findViewById<TextView>(R.id.alert_tv).visibility = View.INVISIBLE
    }

    fun setAlertMessage(message: String?) {
        findViewById<TextView>(R.id.alert_tv).text = message
        switchVisibilityErrorView()
        if (message == null || message == "") findViewById<TextView>(R.id.alert_tv).visibility =
            View.INVISIBLE
        else findViewById<TextView>(R.id.alert_tv).visibility = View.VISIBLE
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