package me.bytebeats.views.marquee

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ViewFlipper
import androidx.annotation.AnimRes

/**
 * Created by bytebeats on 2021/8/20 : 20:51
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class MarqueeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewFlipper(context, attrs) {
    var direction: Direction = Direction.StartToEnd
    var duration: Long = 500
        set(value) {
            field = if (duration <= 0) 500 else value
        }

    @AnimRes
    private var inAnim: Int = 0

    @AnimRes
    private var outAnim: Int = 0

    var onItemClickListener: OnItemClickListener? = null
    var adapter: Adapter? = null
    set(value) {
        field = value
        if (field != null) {

        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView, 0, 0)
        direction = Direction.values()[a.getInt(R.styleable.MarqueeView_marquee_direction, 0)]
        duration = a.getInt(R.styleable.MarqueeView_marquee_anim_duration, 500).toLong()
        inAndOutAnimation()
        a.recycle()
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
            duration = this@MarqueeView.duration
            inAnimation = this
        }
        AnimationUtils.loadAnimation(context, outAnim).apply {
            duration = this@MarqueeView.duration
            outAnimation = this
        }
    }

    private fun inflateMarqueeView() {
        if (adapter == null) {
            if (childCount > 0) {
                removeAllViews()
                clearAnimation()
            }
        } else {
            for (position in 0 until adapter!!.itemCount()) {
                val child = adapter!!.createItemView(position)
                addView(child)
                adapter!!.bindItemView(position)
            }
        }
    }

    enum class Direction {
        StartToEnd,
        TopToBottom,
        EndToStart,
        BottomToTop
    }

    interface OnItemClickListener {
        fun onItemClick(marqueeView: MarqueeView, itemView: View, position: Int)
    }

    interface Adapter {
        fun itemCount(): Int
        fun createItemView(position: Int): View
        fun bindItemView(position: Int)
    }
}