package com.gmail.beprogressive.it.alertview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.gmail.beprogressive.it.alertview.databinding.ViewAlertBinding

class AlertView : ConstraintLayout, PopupMenu.OnMenuItemClickListener {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        init()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("alertMessage")
        fun setAlertMessage(view: AlertView, message: String?) {
            view.binding.alertTv.text = message

            view.switchVisibilityErrorView()
            if (message == null || message == "") view.binding.alertTv.visibility = View.INVISIBLE
            else view.binding.alertTv.visibility = View.VISIBLE
        }
    }

    private lateinit var binding: ViewAlertBinding

    private var animation = Slide(Gravity.TOP)

    private fun init() {
        binding = ViewAlertBinding.inflate(LayoutInflater.from(context), this, true)
//        inflate(context, R.layout.view_alert, this)

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

        //        val a = context.obtainStyledAttributes(attrs, R.styleable.ErrorView, defStyle, 0)
        //        val errorMessage = a.getString(R.styleable.ErrorView_errorMessage)
        //        a.recycle()
    }

    private fun getErrorMessage(): String? {
        return binding.alertTv.text.toString()
    }

    private fun switchVisibilityErrorView() {
        animation.duration = 500
        TransitionManager.beginDelayedTransition(this, animation)
    }

    private fun hideErrorView() {
        switchVisibilityErrorView()
        binding.alertTv.visibility = View.INVISIBLE
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