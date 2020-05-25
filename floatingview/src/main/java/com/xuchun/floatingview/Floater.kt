package com.xuchun.floatingview

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import com.xuchun.floatingview.`interface`.IFloater
import com.xuchun.floatingview.`interface`.IFloatingViewListener
import com.xuchun.floatingview.util.SystemUtil
import java.lang.ref.WeakReference

/**
 * @author: xuchun
 * @time: 2020/5/23 - 14:33
 * @desc: 悬浮窗控制器
 */
class Floater private constructor() : IFloater {
    private var mFloatingBaseView: FloatingBaseView? = null
    private var mContainer: WeakReference<FrameLayout?>? = null

    @LayoutRes
    private val mLayoutId: Int = R.layout.image_floating_view

    @DrawableRes
    private var mIconRes: Int = R.drawable.ic_launcher_round
    private var mLayoutParams: ViewGroup.LayoutParams = getParams()

    private fun getContainer(): FrameLayout? {
        return if (mContainer == null) {
            null
        } else mContainer!!.get()
    }

    override fun attach(activity: Activity?): Floater? {
        attach(getActivityRoot(activity))
        return this
    }

    override fun attach(container: FrameLayout?): Floater? {
        Log.e("floating", "container=${container}  ; floatingBaseView = ${mFloatingBaseView}")

        if (null == container || null == mFloatingBaseView) {
            mContainer = WeakReference(container)
            return this
        }
        if (mFloatingBaseView?.parent == container) {
            return this
        }
        getContainer()?.let {
            if (mFloatingBaseView?.parent == it) {
                it.removeView(mFloatingBaseView)
            }
        }
        mContainer = WeakReference(container)
        container.addView(mFloatingBaseView)
        Log.e(
            "floating",
            "ViewCompat.isAttachedToWindow( floatingBaseView) = " + ViewCompat.isAttachedToWindow(
                mFloatingBaseView!!
            )
        )

        return this
    }

    override fun detach(activity: Activity?): Floater? {
        detach(getActivityRoot(activity))
        return this
    }

    override fun detach(container: FrameLayout?): Floater? {
        if (null != container && null != mFloatingBaseView && ViewCompat.isAttachedToWindow(
                mFloatingBaseView!!
            )
        ) {
            container.removeView(mFloatingBaseView)
        }
        if (mContainer?.get() == container) {
            mContainer = null
        }
        return this
    }

    /**
     * ModeConstant
     */
    override fun mode(modeType: Int): IFloater? {
        mFloatingBaseView?.setModeType(modeType)
        return this
    }

    override fun create(): IFloater? {
        createFloatingView()
        return this
    }


    override fun hideToEdge(): IFloater? {
        mFloatingBaseView?.autoHide()
        return this
    }

    override fun showFromEdge(): IFloater? {
        mFloatingBaseView?.autoShow()
        return this
    }

    override fun listener(listener: IFloatingViewListener?): IFloater? {
        listener?.let {
            mFloatingBaseView?.let {
                it.setViewListener(listener)
            }
        }
        return this
    }

    override fun destroy(): IFloater? {
        Handler(Looper.getMainLooper()).post {
            mFloatingBaseView?.let {
                if (null != getContainer() && ViewCompat.isAttachedToWindow(it)) {
                    getContainer()?.removeView(it)
                }
                mFloatingBaseView = null
            }
        }
        return this
    }

    override fun icon(resId: Int): IFloater? {
        mIconRes = resId
        return this
    }

    override fun layoutParams(params: ViewGroup.LayoutParams): IFloater? {
        mLayoutParams = params
        mFloatingBaseView?.let {
            it.layoutParams = params
        }
        return this
    }

    @Synchronized
    private fun createFloatingView() {
        Log.e(
            "floating", "createFloatingView :" +
                    "getContainer()=${getContainer()}  ; floatingBaseView = $mFloatingBaseView"
        )

        mFloatingBaseView?.let {
            return
        }
        mFloatingBaseView = SystemUtil.get()?.let { ImageFloatView(it, mLayoutId) }!!
        (mFloatingBaseView as ImageFloatView).setIconImage(mIconRes)
        (mFloatingBaseView as ImageFloatView).layoutParams = mLayoutParams;
        getContainer()?.addView(mFloatingBaseView)
        Log.e(
            "floating",
            "ViewCompat.isAttachedToWindow( floatingBaseView) = " + ViewCompat.isAttachedToWindow(
                mFloatingBaseView!!
            )
        )
    }

    companion object {
        val instance: Floater by lazy {
            Single.instance
        }
    }

    private object Single {
        val instance = Floater()
    }

    /**
     * 初始位置在右下
     */
    private fun getParams(): ViewGroup.LayoutParams {
        val params = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.setMargins(params.leftMargin, params.topMargin, 20, 500)
        return params
    }

    private fun getActivityRoot(activity: Activity?): FrameLayout? {
        if (null == activity) return null
        try {
            return activity.window.decorView.findViewById(android.R.id.content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
