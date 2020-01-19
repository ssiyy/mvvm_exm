package com.siy.mvvm.exm.views.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import java.util.*


/**
 * Created by Siy on 2019/10/12.
 *
 * @author Siy
 */
object ViewBindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibleInVisible")
    fun showInVisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter(value = ["android:onClick", "android:isThrottleFirst", "android:throttleTime"], requireAll = false)
    fun clickView(view: View, clickListener: View.OnClickListener?, isThrottleFirst: Boolean?, throttleTime: Long?) {
        view.setOnClickListener(object : View.OnClickListener {
            private val throttleTime_ = throttleTime ?: 1000L
            private var lastClickTime_: Long = 0
            private var lastClickId_: Int? = null
            override fun onClick(v: View?) {
                val currentTime: Long = Calendar.getInstance().timeInMillis
                val mId = v?.id
                if (lastClickId_ != mId) {
                    lastClickId_ = mId
                    lastClickTime_ = currentTime
                    clickListener?.onClick(v)
                    return
                }

                if (isThrottleFirst == true) {
                    if (currentTime - lastClickTime_ > throttleTime_) {
                        lastClickTime_ = currentTime
                        clickListener?.onClick(v)
                    }
                } else {
                    clickListener?.onClick(v)
                }
            }
        })
    }
}