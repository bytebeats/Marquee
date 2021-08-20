package me.bytebeats.views.marquee

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged

/**
 * Created by bytebeats on 2021/8/19 : 15:45
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class MarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : AppCompatTextView(context, attrs, defStyleRes) {
    var repeatMode: RepeatMode = RepeatMode.FOREVER
    var foreverMode: ForeverMode = ForeverMode.APPENDING

    //milliseconds before start to marquee
    var startDelay: Long = 0
        set(value) {
            field = if (value < 0) 0 else value
        }
    var marqueeSpeed: Long = 1000//in milliseconds
    var resetX: Boolean = true//reset x when text changed
    var tapToPause: Boolean = false//tap to pause marquee, tap again to resume

    //text marquee finished, restart marquee after how much time, in milliseconds
    var briefStayDelay: Long = 0
        set(value) {
            field = if (value < 0) 0 else value
        }

    private var mClickListener: OnClickListener? = null
    private var mIs1stMarquee = true
    private var mPaused = true
    private val mScroller by lazy { Scroller(context, LinearInterpolator()) }
    private val sMainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var mXPaused = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView, 0, defStyleRes)
        repeatMode = RepeatMode.values()[a.getInt(R.styleable.MarqueeTextView_repeat_mode, 0)]
        foreverMode = ForeverMode.values()[a.getInt(R.styleable.MarqueeTextView_forever_mode, 0)]
        startDelay = a.getInteger(R.styleable.MarqueeTextView_start_delay, 0).toLong()
        marqueeSpeed = a.getInteger(R.styleable.MarqueeTextView_marquee_speed, 1000).toLong()
        briefStayDelay = a.getInteger(R.styleable.MarqueeTextView_short_stay_delay, 0).toLong()
        resetX = a.getBoolean(R.styleable.MarqueeTextView_reset_x, true)
        tapToPause = a.getBoolean(R.styleable.MarqueeTextView_tap_to_pause, false)
        a.recycle()
        setOnClickListener {
            if (tapToPause) {
                tapToPause()
            } else {
                mClickListener?.onClick(this)
            }
        }
        isSingleLine = true
        setLines(1)
        ellipsize = TextUtils.TruncateAt.MARQUEE
        setHorizontallyScrolling(true)
        setScroller(mScroller)
        doAfterTextChanged {
            if (resetX) {
                stopMarquee()
            }
        }
    }

    private fun tapToPause() {
        if (mPaused) {
            pauseMarquee()
        } else {
            resumeMarquee()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        mClickListener = l
    }

    fun startMarquee() {
        mXPaused = 0
        mIs1stMarquee = true
        mPaused = true
        resumeMarquee()
    }


    fun resumeMarquee() {
        if (!mPaused) return
        val distance = computeTextLength()
        if (distance <= 0) {
            mPaused = true
            mXPaused = 0
            return
        }
        val toScroll = distance - mXPaused
        val duration = (marqueeSpeed * toScroll * 1.0 / distance).toInt()
        if (mIs1stMarquee) {
            sMainHandler.postDelayed({ startScroll(mXPaused, toScroll, duration) }, startDelay)
        } else {
            sMainHandler.post { startScroll(mXPaused, toScroll, duration) }
        }
    }

    private fun startScroll(xOffset: Int, distance: Int, duration: Int) {
        mScroller.startScroll(xOffset, 0, distance, 0, duration)
        invalidate()
        mPaused = false
    }

    fun pauseMarquee() {
        if (mPaused) return
        mPaused = true
        mXPaused = mScroller.currX
        mScroller.abortAnimation()
    }

    fun stopMarquee() {
        mPaused = true
        mXPaused = 0
        mScroller.startScroll(0, 0, 0, 0, 0)
    }

    private fun computeTextLength(): Int {
        val rect = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, rect)
        Log.i(
            TAG,
            "textBounds: ${rect.width()}, measureText: ${paint.measureText(text.toString())}"
        )
        return rect.width()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.isFinished && !mPaused) {
            if (repeatMode == RepeatMode.ONCE) {
                Log.i(TAG, "once")
                stopMarquee()
                return
            }
            mPaused = true
            mXPaused = -width
            mIs1stMarquee = false
            Log.i(TAG, foreverMode.name)
            if (foreverMode == ForeverMode.APPENDING) {
                sMainHandler.post { resumeMarquee() }
            } else {
                sMainHandler.postDelayed({ resumeMarquee() }, briefStayDelay)
            }
        }
    }

    enum class RepeatMode { FOREVER, ONCE }
    enum class ForeverMode { SHORT_STAY, APPENDING }

    companion object {
        private const val TAG = "MarqueeTextView"
    }
}