package com.siy.mvvm.exm.ui.main

import android.view.View
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentFirstPageBinding

class FirstPageFragment(override val layoutId: Int = R.layout.fragment_first_page) :
    BaseLazyFragment<FragmentFirstPageBinding>() {

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding?.run {
            hostFrg = this@FirstPageFragment
        }
    }

    /**
     * 首页
     */
    fun toMainPage() =
        navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToArticleListFragment())


    /**
     * ForResult
     */
    fun forResult() =
        navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToToResultFragment())


}