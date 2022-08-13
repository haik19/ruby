package com.tbnt.ruby

import android.app.Activity
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewOutlineProvider

val Int.toPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun View.setRoundedCorner(cornerRadius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?.let {
                outline?.setRoundRect(0, 0, it.width, it.height, cornerRadius)
            }
        }
    }
    clipToOutline = true
}


fun Activity.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Activity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Activity?.isVisible(view: View?): Boolean {
    if (this == null || view == null) {
        return false
    }
    if (!view.isShown) {
        return false
    }
    val actualPosition = Rect()
    view.getGlobalVisibleRect(actualPosition)
    val screen = Rect(0, 0, getScreenWidth(), getScreenHeight())
    return actualPosition.intersect(screen)?:false
}