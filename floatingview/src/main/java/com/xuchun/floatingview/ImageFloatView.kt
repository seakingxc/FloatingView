package com.xuchun.floatingview

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes

/**
 * @author: xuchun
 * @time: 2020/5/23 - 13:19
 * @desc: 图片悬浮框
 */
class ImageFloatView : FloatingBaseView {
    private var mIcon: ImageView? = null

    constructor(context: Context) : this(context, R.layout.image_floating_view)
    constructor(context: Context, resource: Int) : super(context, null) {
        View.inflate(context, resource, this)
        mIcon = findViewById(R.id.icon)
    }

    fun setIconImage(@DrawableRes resId: Int) {
        mIcon!!.setImageResource(resId)
    }
}