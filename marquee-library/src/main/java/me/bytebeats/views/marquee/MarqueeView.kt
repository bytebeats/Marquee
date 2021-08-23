package me.bytebeats.views.marquee

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ViewFlipper
import androidx.annotation.AnimRes
import java.util.*

/**
 * Created by bytebeats on 2021/8/23 : 18:04
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class MarqueeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewFlipper(context, attrs), Observer {
    var direction: MarqueeDirection = MarqueeDirection.StartToEnd
        set(value) {
            field = value
            inAndOutAnimation()
        }
    var animDuration: Long = 500
        set(value) {
            field = if (animDuration <= 0) 500 else value
        }

    @AnimRes
    private var inAnim: Int = 0

    @AnimRes
    private var outAnim: Int = 0

    var onItemClickListener: OnItemClickListener<MarqueeView>? = null
    var adapter: Adapter<*>? = null
        set(value) {
            field = value
            field?.attachToMarqueeView(this)
            updateFlippers()
        }

    var position = -1
        private set

    private var isInAnimStarted = false

    private val mViewCache by lazy { Array<ViewHolder?>(3) { null } }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView, 0, 0)
        direction = MarqueeDirection.values()[a.getInt(R.styleable.MarqueeView_marqueeDirection, 0)]
        animDuration = a.getInt(R.styleable.MarqueeView_marqueeAnimDuration, 500).toLong()
        a.recycle()
    }

    private fun inAndOutAnimation() {
        when (direction) {
            MarqueeDirection.StartToEnd -> {
                inAnim = R.anim.marquee_in_from_start
                outAnim = R.anim.marquee_out_to_end
            }
            MarqueeDirection.TopToBottom -> {
                inAnim = R.anim.marquee_in_from_top
                outAnim = R.anim.marquee_out_to_bottom
            }
            MarqueeDirection.EndToStart -> {
                inAnim = R.anim.marquee_in_from_end
                outAnim = R.anim.marquee_out_to_start
            }
            MarqueeDirection.BottomToTop -> {
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
                if (adapter != null) {
                    position %= adapter!!.itemCount()
                    val holder = createItemView()
                    if (holder.view.parent == null) {
                        addView(holder.view)
                    }
                    adapter!!.bindViewHolder(holder, position)
                }
                isInAnimStarted = false
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }

    private fun updateFlippers() {
        if (adapter == null || adapter!!.itemCount() == 0) {
            position = -1
            if (childCount > 0) {
                removeAllViews()
                clearAnimation()
            }
        } else {
            position = 0
            if (adapter!!.itemCount() > 0) {
                val holder = createItemView()
                if (holder.view.parent == null) {
                    addView(holder.view)
                }
                adapter!!.bindViewHolder(holder, position)
                startFlipping()
            }
        }
    }

    private fun createItemView(): ViewHolder {
        val position = displayedChild + 1
        val p = position % 3
        if (mViewCache[p] == null) {
            mViewCache[p] = adapter!!.createViewHolder(p)
            mViewCache[p]!!.view.setOnClickListener {
                onItemClickListener?.onItemClick(this, it, it.tag as Int)
            }
        }
        val holder = mViewCache[p]!!
        holder.view.tag = position
        return holder
    }

    override fun startFlipping() {
        if (adapter == null) {
            throw IllegalArgumentException("")
        }
        super.startFlipping()
    }

    override fun update(o: Observable?, arg: Any?) {
        if (CMD_NOTIFY_ADAPTER == arg) {
            inAnimation?.cancel()
            updateFlippers()
        }
    }

    abstract class Adapter<T : ViewHolder> : Observable() {
        private var mMarqueeView: MarqueeView? = null
        abstract fun createViewHolder(position: Int): T
        abstract fun bindViewHolder(holder: ViewHolder, position: Int)
        abstract fun itemCount(): Int

        fun attachToMarqueeView(marqueeView: MarqueeView?) {
            if (!isAttachedToMarqueeView()) {
                mMarqueeView = marqueeView
                addObserver(mMarqueeView)
            } else {
                throw IllegalStateException(
                    "The %s must be attached to %s".format(
                        Adapter::class.java.simpleName,
                        MarqueeView::class.java.simpleName
                    )
                )
            }
        }

        private fun isAttachedToMarqueeView(): Boolean = mMarqueeView != null

        protected fun notifyDataSetChanged() {
            if (isAttachedToMarqueeView()) {
                setChanged()
                notifyObservers(CMD_NOTIFY_ADAPTER)
            }
        }
    }

    abstract class ViewHolder(val view: View)

    companion object {
        private const val TAG = "MarqueeView"
        internal const val CMD_NOTIFY_ADAPTER = "MarqueeView#Adapter_Changed"
    }
}