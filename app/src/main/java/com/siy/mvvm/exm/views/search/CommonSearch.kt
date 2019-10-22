package com.siy.mvvm.exm.views.search

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.utils.hideSoftInput
import com.siy.mvvm.exm.utils.showToast


/**
 * Created by Siy on 2019/07/18.
 *
 * @author Siy
 */
abstract class CommonSearch(initSearchStr: String? = "") {

    /**
     * 搜索配图
     */
    val searchIcon = MutableLiveData<Int>(R.drawable.common_search)

    /**
     * 搜索框提示文字
     */
    val searchTip = MutableLiveData<String>("请输入内容搜索")

    /**
     * 搜索提示文字
     */
    val searchStr = MutableLiveData<String>(initSearchStr ?: "")


    fun onEditorAction(v: TextView, actionId: Int) =
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val key = v.text.toString().trim()
            if (key.isEmpty()) {
                v.context.showToast("请输入搜索内容")
                true
            } else {
                searchApi(key)
                v.hideSoftInput()
                true
            }
        } else {
            false
        }


    /**
     * 点击键盘搜索按钮进行搜索
     */
    abstract fun searchApi(searchStr: String)
}