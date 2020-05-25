package com.xuchun.floatingview.util

import android.app.Application

/**
 * @author: xuchun
 * @time: 2020/5/23 - 15:30
 * @desc:
 */
object SystemUtil {
    private var INSTANCE: Application? = null
    fun get(): Application? {
        return INSTANCE
    }

    init {
        var app: Application? = null
        try {
            app =
                Class.forName("android.app.AppGlobals").getMethod("getInitialApplication")
                    .invoke(null) as Application
            checkNotNull(app) { "Static initialization of Applications must be on main thread." }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                app = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null) as Application
            } catch (ex: Exception) {
                e.printStackTrace()
            }
        } finally {
            INSTANCE = app
        }
    }


}