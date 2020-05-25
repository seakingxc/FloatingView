package com.xuchun.floatingview

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.xuchun.floatingview.`interface`.IFloatingViewListener
import com.xuchun.floatingview.constants.ModeConstant.MODE_DRAGGABLE
import com.xuchun.floatingview.constants.ModeConstant.MODE_STALL
import com.xuchun.floatingview.util.UIUtil

/**
 * @author: xuchun
 * @time: 2020/5/23 - 11:49
 * @desc:悬浮窗
 * 请设置需要展示的activity属性：android:configChanges="orientation|screenSize"
 * 这样横竖屏切换时不会重新调用生命周期，重新调用生命周期会导致控件detach和重新attach，可能会出现位置变化。
 * 如果设置了此属性，则会走控件中onConfigurationChanged，已经覆写
 */
open class FloatingBaseView : FrameLayout {
    private val MARGIN_EDGE = 13
    private val TOUCH_TIME_THRESHOLD = 150
    private var modeType: Int = MODE_DRAGGABLE
    private lateinit var moveAnimator: MoveAnimator
    private var statusBarHeight: Int = 0
    private var usableScreenWidth = 0
    private var screenHeight = 0
    private var touchX: Float = 0F//触摸点相对于控件自身x坐标
    private var touchY: Float = 0F
    private var touchRawX: Float = 0F//触摸点相对于手机屏幕左上角坐标
    private var touchRawY: Float = 0F
    private var isNearestLeft = true
    private var lastTouchDownTime: Long = 0
    private var isHide: Boolean = false
    private var viewListener: IFloatingViewListener? = null

    fun setViewListener(listener: IFloatingViewListener?) {
        listener?.let {
            viewListener = listener
        }
    }

    fun setModeType(modeType: Int) {
        if (modeType > 0) {
            this.modeType = modeType
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    //y坐标和屏幕高度的比例
    var yWithScreenHeightRatio: Float = 0F

    private fun init() {
        moveAnimator = MoveAnimator()
        statusBarHeight = UIUtil.getStatusBarHeight(context)
        isClickable = true
        resetSize()
        post {
            val location = IntArray(2)
            getLocationInWindow(location)
            yWithScreenHeightRatio = location[1].toFloat() / screenHeight
            isNearestLeft()
            Log.e(
                "floating",
                "init    screenHeight = ${screenHeight} ;  location[0]=${location[0]} ;location[1] = ${location[1]}" +
                        "; yWithScreenHeightRatio= ${yWithScreenHeightRatio}"
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null == event)
            return false
        else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = x
                    touchY = y
                    touchRawX = event.rawX
                    touchRawY = event.rawY
                    lastTouchDownTime = System.currentTimeMillis()
                    resetSize()
                    moveAnimator.stop()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (modeType == MODE_DRAGGABLE) {
                        x = event.rawX - touchRawX + touchX
                        // 限制不可超出屏幕高度
                        var desY: Float = event.rawY - touchRawY + touchY
                        if (desY < statusBarHeight) desY = statusBarHeight.toFloat()
                        if (desY > screenHeight - height) {
                            desY = (screenHeight - height).toFloat()
                        }
                        y = desY
                    }
                }
                MotionEvent.ACTION_UP -> {
                    when (modeType) {
                        MODE_DRAGGABLE -> moveToEdge(isNearestLeft())
                    }
                    if (System.currentTimeMillis() - lastTouchDownTime < TOUCH_TIME_THRESHOLD) {
                        if (modeType == MODE_STALL) {
                            autoShow(isNearestLeft())
                        }
                        viewListener?.let {
                            viewListener!!.onClick(this)
                        }
                    }
                }
            }
        }
        return true
    }

    protected open fun isNearestLeft(): Boolean {
        val middle: Int = usableScreenWidth / 2
        isNearestLeft = x < middle
        return isNearestLeft
    }

    open fun moveToEdge(isLeft: Boolean) {
        val moveDistance: Float =
            if (isLeft) MARGIN_EDGE.toFloat() else usableScreenWidth - MARGIN_EDGE.toFloat()
        moveAnimator.start(moveDistance, y)
    }

    open fun autoHide() {
        autoHide(isNearestLeft())
    }

    private fun autoHide(isLeft: Boolean) {
        if (isHide) return
        resetSize()
        val moveDistance: Float =
            if (isLeft) -(MARGIN_EDGE.toFloat() + width / 2) else usableScreenWidth.toFloat() + width / 2
        moveAnimator.start(moveDistance, y)
        isHide = true
    }

    open fun autoShow() {
        autoShow(isNearestLeft())
    }

    private fun autoShow(isLeft: Boolean) {
        if (isHide) {
            resetSize()
            val moveDistance: Float =
                if (isLeft) MARGIN_EDGE.toFloat() else usableScreenWidth.toFloat() - MARGIN_EDGE
            moveAnimator.start(moveDistance, y)
            isHide = false
        }
    }


    private fun resetSize() {
        usableScreenWidth = UIUtil.getScreenWidth(context) - width
        screenHeight = UIUtil.getScreenHeight(context)
    }

    protected inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX = 0f
        private var destinationY = 0f
        private var startingTime: Long = 0
        fun start(x: Float, y: Float) {
            destinationX = x
            destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView == null || rootView.parent == null) {
                return
            }
            val progress =
                Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
            val deltaX: Float = (destinationX - x) * progress
            val deltaY: Float = (destinationY - y) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }

        fun stop() {
            handler.removeCallbacks(this)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x += deltaX
        y += deltaY
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e("floating", "onConfigurationChanged")

        resetSize()
        when (modeType) {
            MODE_DRAGGABLE -> moveToEdge(isNearestLeft)
            MODE_STALL -> {
                //当横屏时，继续用y当纵坐标就不合适了，应该先计算y占屏幕高度的比例，比如竖屏时，屏幕高度1920，y初始是1200，
                // 那么view的y坐标大约在屏幕高度的2/3处，横屏后也应该在2/3处，比如横屏后屏幕高度是1080，那么view的y坐标就是1080*2/3 = 720
//
                y = screenHeight * yWithScreenHeightRatio
                Log.e("floating", "screenHeight = $screenHeight ; y = $y")
                if (isHide) autoShow(isNearestLeft) else autoHide(isNearestLeft)
            }
        }
    }
}