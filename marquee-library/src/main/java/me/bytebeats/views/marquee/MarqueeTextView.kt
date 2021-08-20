package me.bytebeats.views.marquee

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
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

    //time which all text marquee once takes, in milliseconds
    var marqueeSpeed: Long = 1000

    //reset x when text changed
    var resetX: Boolean = true
    var tapToPause: Boolean = false//tap to pause marquee, tap again to resume

    //text marquee finished, restart marquee after how much time, in milliseconds
    var shortStayDelay: Long = 0
        set(value) {
            field = if (value < 0) 0 else value
        }

    private var mClickListener: OnClickListener? = null
    private var m1stMarquee = true
    private val mScroller by lazy { Scroller(context, LinearInterpolator()) }
    private val sMainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var mXPaused = 0
    private var mState = State.INITIALIZED

    var onStateChangeListener: OnStateChangeListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView, 0, defStyleRes)
        repeatMode = RepeatMode.values()[a.getInt(R.styleable.MarqueeTextView_repeat_mode, 0)]
        foreverMode = ForeverMode.values()[a.getInt(R.styleable.MarqueeTextView_forever_mode, 0)]
        startDelay = a.getInteger(R.styleable.MarqueeTextView_start_delay, 0).toLong()
        marqueeSpeed = a.getInteger(R.styleable.MarqueeTextView_marquee_speed, 1000).toLong()
        shortStayDelay = a.getInteger(R.styleable.MarqueeTextView_short_stay_delay, 0).toLong()
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
                reset()
                resumeMarquee()
            }
        }
    }

    private fun tapToPause() {
        if (mState == State.RESUMED) {
            pauseMarquee()
        } else {
            resumeMarquee()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun reset() {
        mXPaused = 0
        m1stMarquee = true
        moveToState(State.INITIALIZED)
        mScroller.startScroll(0, 0, 0, 0, 0)
    }

    fun resumeMarquee() {
        if (mState == State.RESUMED) return
        val distance = computeDistance()
        if (distance <= 0) {
            reset()
            return
        }
        val toScroll = distance - mXPaused
        val duration = (marqueeSpeed * toScroll * 1.0 / distance).toInt()
        if (m1stMarquee) {
            sMainHandler.postDelayed({ startScroll(mXPaused, toScroll, duration) }, startDelay)
        } else {
            sMainHandler.post { startScroll(mXPaused, toScroll, duration) }
        }
    }

    private fun startScroll(xOffset: Int, distance: Int, duration: Int) {
        mScroller.startScroll(xOffset, 0, distance, 0, duration)
        invalidate()
        moveToState(State.RESUMED)
    }

    fun pauseMarquee() {
        if (mState == State.PAUSED) return
        mXPaused = mScroller.currX
        m1stMarquee = false
        mScroller.abortAnimation()
        moveToState(State.PAUSED)
    }

    private fun computeDistance(): Int {
        val rect = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, rect)
        return rect.width()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.isFinished && (mState == State.RESUMED || mState == State.AFTER_ONCE_FINISHED)) {
            mXPaused = -width
            if (repeatMode == RepeatMode.ONCE) {
                if (mState == State.RESUMED) {
                    doAfterOnceFinished()
                } else if (mState == State.AFTER_ONCE_FINISHED) {
                    m1stMarquee = false
                    reset()
                }
                return
            }
            m1stMarquee = false
            moveToState(State.RESTARTED)
            if (foreverMode == ForeverMode.APPENDING) {
                sMainHandler.post { resumeMarquee() }
            } else {
                sMainHandler.postDelayed({ resumeMarquee() }, shortStayDelay)
            }
        }
    }

    private fun doAfterOnceFinished() {
        val distance = computeDistance()
        if (distance <= 0) {
            reset()
            return
        }
        val toScroll = 0 - mXPaused
        val duration = (marqueeSpeed * toScroll * 1.0 / distance).toInt()
        mScroller.startScroll(mXPaused, 0, toScroll, 0, duration)
        invalidate()
        moveToState(State.AFTER_ONCE_FINISHED)
    }

    private fun moveToState(newState: State) {
        onStateChangeListener?.onChanged(mState, newState)
        mState = newState
    }

    enum class RepeatMode { FOREVER, ONCE }
    enum class ForeverMode { SHORT_STAY, APPENDING }

    interface OnStateChangeListener {
        fun onChanged(oldState: State, newState: State)
    }

    enum class State {
        INITIALIZED, RESUMED, AFTER_ONCE_FINISHED, RESTARTED, PAUSED
    }

    companion object {
        private const val TAG = "MarqueeTextView"
    }
}