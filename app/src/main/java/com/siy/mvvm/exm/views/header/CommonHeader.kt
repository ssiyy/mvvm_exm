package com.siy.mvvm.exm.views.header

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.siy.mvvm.exm.R


/**
 * Created by Siy on 2019/4/19.
 *
 * @author Siy
 */
abstract class CommonHeader {

    var showTitleVg = MutableLiveData(true)

    var backIcon = MutableLiveData(R.drawable.common_back)
    var showBackIcon = MutableLiveData(true)

    var backStr = MutableLiveData("返回")
    var showbackStr = MutableLiveData(true)


    var title = MutableLiveData<String>()
    var showTitle = MutableLiveData(true)

    var titleIcon = MutableLiveData(R.drawable.common_question)
    var showTitleIcon = MutableLiveData(true)

    var rightIcon = MutableLiveData(R.drawable.real_write)
    var rightIconDrawable = MutableLiveData<Drawable>()
    var showRightIcon = MutableLiveData(false)

    /**
     * 点击返回按钮
     */
    abstract fun onBackClick()

    /**
     * 点击右边按钮
     */
    open fun onRightIconClick() = Unit

    /**
     * 点击标题
     */
    open fun onTitleClick() = Unit

    /**
     * 点击标题图片
     */
    open fun onTitleIconClick() = Unit

}
