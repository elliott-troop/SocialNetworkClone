package com.elliottco.socialnetworkclone.ui

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.elliottco.socialnetworkclone.R


/**
 * Animation to slide up a single view
 */
fun View.slideup(context: Context, animationTime: Long, startOffSet: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = animationTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffSet
    }

    startAnimation(slideUp)
}

/**
 * Slides up a list of views with a specified animation time and delay
 */
fun slideUpViews(context: Context, vararg views: View, animationTime: Long = 300L, delay: Long = 150L) {
    for(i in views.indices) {
        views[i].slideup(context, animationTime, delay * i)
    }
}