# FloatingView
FloatingView By Kotlin
一个简单的悬浮框实现，由kotlin实现，无需申请各种系统权限。
支持两种模式：
1、可拖动：可随意拖动到屏幕任何地方，停下后自动吸附边缘
2、固定位置，支持在边缘隐藏一半：不可拖动，固定在屏幕某个位置，支持页面滑动时，收起到边缘隐藏一半

用法：
1、attach和detach

    override fun onStart() {
        super.onStart()
        Floater.instance.attach(this)
    }

    override fun onStop() {
        super.onStop()
        Floater.instance.detach(this)
    }
    
2、使用：可设置icon、mode不设置默认是可拖动模式、支持点击事件监听

        override fun onResume() {
                super.onResume()
                Floater.instance.icon(R.drawable.img_receiving_certificate)?.create()?.mode(ModeConstant.MODE_STALL)
                    ?.listener(object : IFloatingViewListener {
                        override fun onClick(floatingBaseView: FloatingBaseView) {
                            Toast.makeText(this@MainActivity, "点我干嘛", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
    
3、如果是固定位置模式，支持在滑动监听中隐藏悬浮窗

         scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY > oldScrollY || scrollY < oldScrollY) {
                        //滚动
                        Floater.instance.hideToEdge()
                    }
        })
