package me.bytebeats.views.marquee

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.annotation.AnimRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

/**
 * Created by bytebeats on 2021/8/20 : 20:51
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class TextMarqueeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewFlipper(context, attrs) {
    var direction: Direction = Direction.StartToEnd
    var animDuration: Long = 500
        set(value) {
            field = if (animDuration <= 0) 500 else value
        }
    var singleLine: Boolean = false
    var textColor: Int = 0xff000000.toInt()
    var textSize: Int = 14
    var textGravity = TextGravity.START
        set(value) {
            field = value
            textGravity()
        }
    private var gravity = Gravity.START and Gravity.CENTER_VERTICAL
    var typeFace: Typeface? = null

    @AnimRes
    private var inAnim: Int = 0

    @AnimRes
    private var outAnim: Int = 0

    var onItemClickListener: OnItemClickListener? = null

    var position = 0
        private set

    private var isInAnimStarted = false

    private val messages by lazy { mutableListOf<CharSequence>() }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TextMarqueeView, 0, 0)
        direction = Direction.values()[a.getInt(R.styleable.TextMarqueeView_marquee_direction, 0)]
        animDuration = a.getInt(R.styleable.TextMarqueeView_marquee_anim_duration, 500).toLong()
        singleLine = a.getBoolean(R.styleable.TextMarqueeView_marquee_single_line, false)
        textSize = a.getDimensionPixelSize(R.styleable.TextMarqueeView_marquee_text_size, 14)
        textColor = a.getColor(R.styleable.TextMarqueeView_marquee_text_color, 0xff000000.toInt())
        @FontRes val fontRes = a.getResourceId(R.styleable.TextMarqueeView_marquee_text_font, 0)
        if (fontRes != 0) {
            typeFace = ResourcesCompat.getFont(context, fontRes)
        }
        textGravity =
            TextGravity.values()[a.getInt(R.styleable.TextMarqueeView_marquee_text_gravity, 0)]
        textGravity()
        inAndOutAnimation()
        a.recycle()
    }

    private fun textGravity() {
        gravity = when (textGravity) {
            TextGravity.START -> Gravity.START and Gravity.CENTER_VERTICAL
            TextGravity.CENTER -> Gravity.CENTER
            TextGravity.END -> Gravity.END and Gravity.CENTER_VERTICAL
        }
    }

    private fun inAndOutAnimation() {
        when (direction) {
            Direction.StartToEnd -> {
                inAnim = R.anim.marquee_in_from_start
                outAnim = R.anim.marquee_out_to_end
            }
            Direction.TopToBottom -> {
                inAnim = R.anim.marquee_in_from_top
                outAnim = R.anim.marquee_out_to_bottom
            }
            Direction.EndToStart -> {
                inAnim = R.anim.marquee_in_from_end
                outAnim = R.anim.marquee_out_to_start
            }
            Direction.BottomToTop -> {
                inAnim = R.anim.marquee_in_from_bottom
                outAnim = R.anim.marquee_out_to_top
            }
        }
        setAnimations()
    }

    private fun setAnimations() {
        AnimationUtils.loadAnimation(context, inAnim).apply {
            duration = animDuration
            inAnimation = this
        }
        AnimationUtils.loadAnimation(context, outAnim).apply {
            duration = animDuration
            outAnimation = this
        }
    }

    override fun setInAnimation(inAnimation: Animation?) {
        super.setInAnimation(inAnimation)
        getInAnimation()?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                if (isInAnimStarted) {
                    animation?.cancel()
                }
                isInAnimStarted = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                ++position
                position %= messages.size
                val itemView = createTextView(messages[position])
                if (itemView.parent == null) {
                    addView(itemView)
                }
                isInAnimStarted = false
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }

    fun startWithMessage(message: CharSequence) {
        messages.clear()
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                splitText(message)
            }
        })
    }

    fun splitText(message: CharSequence) {
        this.messages.clear()
        if (width == 0) {
            throw IllegalArgumentException("TextMarqueeView's width can't be 0!")
        }
        val limit = width / textSize
        val messageSize = message.length
        if (messageSize <= limit) {
            messages.add(message)
        } else {
            val size = messageSize / limit + if (messageSize % limit == 0) 0 else 1
            for (i in 0 until size) {
                val start = i * limit
                val end = messageSize.coerceAtMost((i + 1) * limit)
                messages.add(message.subSequence(start, end))
            }
        }
        post { start() }
    }

    fun startWithMessages(messages: Collection<CharSequence>?) {
        this.messages.clear()
        if (!messages.isNullOrEmpty()) {
            this.messages.addAll(messages)
        }
        post { start() }
    }

    private fun start() {
        removeAllViews()
        clearAnimation()
        if (messages.isEmpty()) {
            throw IllegalArgumentException("Messages can't be empty!")
        }
        position = 0
        addView(createTextView(messages[position]))
        if (messages.size > 1) {
            startFlipping()
        }
    }

    private fun createTextView(message: CharSequence?): TextView {
        var itemView: TextView? = getChildAt((displayedChild + 1) % 3) as TextView?
        if (itemView == null) {
            itemView = TextView(context)
            itemView.gravity = gravity
            itemView.setTextColor(textColor)
            itemView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            itemView.includeFontPadding = true
            itemView.isSingleLine = singleLine
            if (singleLine) {
                itemView.setLines(1)
                itemView.ellipsize = TextUtils.TruncateAt.END
            }
            if (typeFace != null) {
                itemView.typeface = typeFace
            }
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(this, itemView, itemView.tag as Int)
            }
        }
        itemView.text = message
        itemView.tag = position
        return itemView
    }

    enum class Direction {
        StartToEnd,
        TopToBottom,
        EndToStart,
        BottomToTop
    }

    interface OnItemClickListener {
        fun onItemClick(textMarqueeView: TextMarqueeView, itemView: View, position: Int)
    }

    enum class TextGravity {
        START, CENTER, END
    }
}