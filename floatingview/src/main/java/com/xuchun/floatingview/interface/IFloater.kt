package com.xuchun.floatingview.`interface`

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes

/**
 * @author: xuchun
 * @time: 2020/5/23 - 14:33
 * @desc:
 */
interface IFloater {

    fun attach(activity: Activity?): IFloater?

    fun attach(container: FrameLayout?): IFloater?

    fun detach(activity: Activity?): IFloater?

    fun detach(container: FrameLayout?): IFloater?

    fun mode(modeType: Int): IFloater?

    fun create(): IFloater?

    fun destroy(): IFloater?

    fun hideToEdge(): IFloater?

    fun showFromEdge(): IFloater?

    fun listener(listener: IFloatingViewListener?): IFloater?

    fun icon(@DrawableRes resId: Int): IFloater?

    fun layoutParams(params: ViewGroup.LayoutParams): IFloater?

}