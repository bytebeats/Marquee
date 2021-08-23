package me.bytebeats.views.marquee

import android.view.View

/**
 * Created by bytebeats on 2021/8/23 : 21:11
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnItemClickListener<T : View> {
    fun onItemClick(view: T, itemView: View, position: Int)
}