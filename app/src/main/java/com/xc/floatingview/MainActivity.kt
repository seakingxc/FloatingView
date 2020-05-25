package com.xc.floatingview

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.xuchun.floatingview.Floater
import com.xuchun.floatingview.FloatingBaseView
import com.xuchun.floatingview.`interface`.IFloatingViewListener
import com.xuchun.floatingview.constants.ModeConstant
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY || scrollY < oldScrollY) {
                //滚动
                Floater.instance.hideToEdge()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Floater.instance.icon(R.drawable.img_receiving_certificate)?.create()?.mode(ModeConstant.MODE_STALL)
            ?.listener(object : IFloatingViewListener {
                override fun onClick(floatingBaseView: FloatingBaseView) {
                    Toast.makeText(this@MainActivity, "点我干嘛", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onStart() {
        super.onStart()
        Floater.instance.attach(this)
    }

    override fun onStop() {
        super.onStop()
        Floater.instance.detach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
