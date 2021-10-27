package com.gmail.beprogressive.it.alertview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager


@BindingMethods(
    BindingMethod(
        type = AlertView::class,
        attribute = "set_alert_message",
        method = "setAlertMessage"
    ),
    BindingMethod(
        type = AlertView::class,
        attribute = "set_alert_button_text",
        method = "setButtonText"
    ),
    BindingMethod(
        type = AlertView::class,
        attribute = "set_expandable",
        method = "setExpandable"
    ),
)
class AlertView : ConstraintLayout, PopupMenu.OnMenuItemClickListener {

    @Suppress("Unused")
    val messageView: TextView by lazy {
        findViewById(R.id.alert_tv)
    }

    @Suppress("Unused")
    val buttonView: Button by lazy { findViewById(R.id.button) }

    private val arrowView: ImageView by lazy { findViewById(R.id.arrow) }

    @Suppress("Unused")
    fun switchExpandState() = cycleTextViewExpansion(messageView)

    private fun getAlertMessage() = messageView.text.toString()

    @Suppress("Unused")
    fun collapsed() = messageView.lineCount > messageView.maxLines

    private var onStateChanged: ((isShown: Boolean?) -> Unit)? = null
    private var onAlertClick: ((alertView: AlertView, message: String?) -> Unit)? = null
    private var onAlertLongClick: ((message: String?) -> Unit)? = null
    private var onButtonClick: ((alertView: AlertView, message: String?) -> Unit)? = null

    private var transition = Slide(Gravity.TOP)

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

        setOnTouchListener(SwipeListener(this))

        setOnLongClickListener {
            if (onAlertLongClick != null)
                onAlertLongClick?.invoke(getAlertMessage())
            else {
                val popupMenu = PopupMenu(context, this)
                popupMenu.menu.add("Copy")
                popupMenu.setOnMenuItemClickListener(this)
                popupMenu.show()
            }
            true
        }

        val a = context.obtainStyledAttributes(attrs, R.styleable.AlertView, 0, 0)
        setupAnimation(a.getInt(R.styleable.AlertView_alert_animation_gravity, Gravity.TOP))
        setButtonText(a.getString(R.styleable.AlertView_set_alert_button_text))
        setAlertMessage(a.getString(R.styleable.AlertView_set_alert_message))
        expandable = a.getBoolean(R.styleable.AlertView_set_expandable, true)

        a.recycle()
    }

    var expandable: Boolean = true
        set(value) {
            if (value) {
                arrowView.visibility = View.VISIBLE
                messageView.ellipsize = null
            } else {
                arrowView.visibility = View.GONE
                messageView.ellipsize = TextUtils.TruncateAt.END
            }
            field = value
        }

    private fun setButton(
        showButton: Boolean,
        animate: Boolean = true,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        buttonView.apply {

            if (onButtonClick != null && showButton) {
                if (animate)
                    slideInRight()
                else
                visibility = View.VISIBLE
            } else {
                if (animate)
                    slideOutRight {
                        visibility = View.GONE
                        onAnimationEnd?.invoke()
                    }
                else
                visibility = View.GONE
            }

            setOnClickListener { onButtonClick?.invoke(this@AlertView, getAlertMessage()) }
        }
    }

    private val collapsedMaxLines = 3
    private fun cycleTextViewExpansion(tv: TextView) {
        if (!expandable) return

        val showButton: Boolean
        val animation = ObjectAnimator.ofInt(
            tv, "maxLines",
            if (tv.maxLines == collapsedMaxLines) {
                arrowView.setImageResource(R.drawable.ic_less)
                showButton = false
                tv.lineCount
            } else {
                arrowView.setImageResource(R.drawable.ic_more)
                showButton = true
                collapsedMaxLines
            }
        )
        if (!showButton)
            setButton(showButton = false, true) {
                animation.setDuration(200).start()
            }
        else {
            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    if (!showButton)
                        setButton(showButton)
                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (showButton)
                        setButton(showButton)
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            })
            animation.setDuration(200).start()
        }

    }

    private fun setupAnimation(gravity: Int) {
        transition = Slide(gravity)
        transition.duration = 500
    }

    private fun switchVisibilityAlertView(
        show: Boolean,
        customTransition: Transition = transition
    ) {
        transition.addTarget(this)
        (parent as? ViewGroup)?.let {
            TransitionManager.beginDelayedTransition(it, customTransition)
            visibility = if (show) View.VISIBLE else View.GONE
        }

        onStateChanged?.invoke(show)
    }

    private fun resetAlertView() {
        messageView.maxLines = collapsedMaxLines
        arrowView.setImageResource(R.drawable.ic_more)
    }

    @Suppress("Unused")
    fun hideAlertView(toRight: Boolean = false) {

        resetAlertView()

        if (toRight) {

            val customTransition = Slide(Gravity.RIGHT)

            transition.duration = 400

            customTransition.addListener(object : Transition.TransitionListener {

                override fun onTransitionStart(transition: Transition) {
                }

                override fun onTransitionEnd(transition: Transition) {
                    animate()
                        .x(0F)
                        .setDuration(0)
                        .start()
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }
            })

            switchVisibilityAlertView(false, customTransition)

        } else
            switchVisibilityAlertView(false)
    }

    fun setAlertMessage(message: String?) {
        setMessage(message)
        switchVisibilityAlertView(message != null && message != "")
    }

    private fun setMessage(message: String?) {

        messageView.text = message

        messageView.post {
            arrowView.visibility =
                if (expandable && messageView.lineCount > messageView.maxLines)
                    View.VISIBLE
                else
                    View.GONE

            setButton(onButtonClick != null)

            if (onAlertClick == null) {
                if (messageView.lineCount > messageView.maxLines)
                    setOnClickListener {
                        cycleTextViewExpansion(messageView)
                    }
                else
                    setOnClickListener {
                        hideAlertView()
                    }
            } else
                setOnClickListener {
                    onAlertClick?.invoke(this, message)
                }
        }
    }

    @Suppress("Unused")
    fun setButtonText(text: String?) {
        if (text != null && text != "")
            buttonView.text = text
        else
            buttonView.text = context.getString(R.string.ok)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) when (item.title) {
            "Copy" -> {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(null, getAlertMessage())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    /**
     * Overrides listeners
     * @param onButtonClick if defined, the alert also shows the button
     * @param onAlertClick if NOT defined and message fits folded max lines, hides the alert on alert click
     */
    @Suppress("UNUSED")
    fun setListeners(
        onStateChanged: ((isShown: Boolean?) -> Unit)? = null,
        onAlertClick: ((alertView: AlertView, message: String?) -> Unit)? = null,
        onAlertLongClick: ((message: String?) -> Unit)? = null,
        onButtonClick: ((alertView: AlertView, message: String?) -> Unit)? = null,
    ) {
        this.onStateChanged = onStateChanged
        this.onAlertClick = onAlertClick
        this.onAlertLongClick = onAlertLongClick
        this.onButtonClick = onButtonClick
    }

    @Suppress("UNUSED")
    fun setStateListener(onStateChanged: ((isShown: Boolean?) -> Unit)? = null) {
        this.onStateChanged = onStateChanged
    }

    /**
     * @param onAlertClick if 'null' and message fits folded max lines, hides the alert on alert click
     */
    @Suppress("UNUSED")
    fun setAlertClickListener(onAlertClick: ((alertView: AlertView, message: String?) -> Unit)? = null) {
        this.onAlertClick = onAlertClick
    }

    @Suppress("UNUSED")
    fun setAlertLongClickListener(onAlertLongClick: ((message: String?) -> Unit)? = null) {
        this.onAlertLongClick = onAlertLongClick
    }

    /**
     * @param onButtonClick if not null, the alert also shows the button. Hides button otherwise
     */
    @Suppress("UNUSED")
    fun setButtonClickListener(onButtonClick: ((alertView: AlertView, message: String?) -> Unit)? = null) {
        
        this.onButtonClick = onButtonClick

        setButton(onButtonClick != null)
    }
}