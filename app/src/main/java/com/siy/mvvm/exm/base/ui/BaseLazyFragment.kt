package com.siy.mvvm.exm.base.ui

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


/**
 *
 *
 *  created by Siy on 2019/08/11
 *
 * @author Siy
 */
abstract class BaseLazyFragment<T : ViewDataBinding> : BaseFragment<T>() {

    /**
     * 记录上一次可见的Fragment
     */
    private var lastChildrenVisibleFragment: Fragment? = null

    /**
     * 是否是第一次显示
     */
    private var isFirstVisible: Boolean = true

    /**
     * 是否是第一次隐藏
     */
    private var isFirstInvisible: Boolean = true

    /**
     *  是否是第一次onResume
     */
    private var isFirstResume: Boolean = true

    /**
     * 是否是第一准备
     */
    private var isPrepared: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPrepare()
    }

    @Synchronized
    private fun initPrepare() {
        if (isPrepared) {
            onFirstUserVisible()
        } else {
            isPrepared = true
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        changChildrenVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                initPrepare()
            } else {
                onUserVisible()
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false
                onFirstUserInvisible()
            } else {
                onUserInvisible()
            }
        }
    }

    private fun changChildrenVisibleHint(isVisibleToUser: Boolean) {
        if (!isAdded) {
            return
        }

        if (isVisibleToUser) {
            if (lastChildrenVisibleFragment != null && lastChildrenVisibleFragment!!.isAdded) {
                lastChildrenVisibleFragment?.userVisibleHint = true
            }
        } else {
            val fragments = childFragmentManager.fragments
            if (!fragments.isNullOrEmpty()) {
                for (fragment in fragments) {
                    if (fragment.userVisibleHint) {
                        lastChildrenVisibleFragment = fragment
                        fragment.userVisibleHint = false
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
            return
        }
        if (userVisibleHint) {
            onUserVisible()
        }
    }

    override fun onStop() {
        super.onStop()
        if (userVisibleHint) {
            onUserInvisible()
        }
    }

    /**
     * Fragment第一次可见时回调，可以在这里 加载数据 / 开启动画 / 广播.....
     */
    protected open fun onFirstUserVisible() = Unit

    /**
     * Fragment可见时(>1)回调，可以在这里开启动画 / 广播.....
     */
    protected open fun onUserVisible() = Unit

    /**
     * Fragment不见时(>1)回调，可以在这里暂停动画 / 暂停广播.....
     */
    protected open fun onUserInvisible() = Unit

    /**
     * Fragment第一次不可见时回到,一般不用
     */
    protected open fun onFirstUserInvisible() = Unit
}