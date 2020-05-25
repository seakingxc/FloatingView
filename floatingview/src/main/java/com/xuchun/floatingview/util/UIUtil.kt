package com.xuchun.floatingview.util

import android.content.Context

/**
 * @author: xuchun
 * @time: 2020/5/23 - 12:17
 * @desc:
 */
object UIUtil {

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getScreenWidth(context: Context): Int {
        var screenWith = -1
        try {
            screenWith = context.resources.displayMetrics.widthPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenWith
    }

    fun getScreenHeight(context: Context): Int {
        var screenHeight = -1
        try {
            screenHeight = context.resources.displayMetrics.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenHeight
    }
}