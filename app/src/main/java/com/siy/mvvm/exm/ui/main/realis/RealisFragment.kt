package com.siy.mvvm.exm.ui.main.realis

import android.view.View
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentLvzhiBinding
import com.siy.mvvm.exm.ui.main.MainFragmentDirections

class RealisFragment(override val layoutId: Int = R.layout.fragment_lvzhi) :
    BaseLazyFragment<FragmentLvzhiBinding>() {

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding?.run {
            hostFrg = this@RealisFragment
        }
    }

    /**
     * 首页
     */
    fun toMainPage() {
        navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToFirstPageFragment())
    }



    /**
     * ForResult
     */
    fun forResult() {
        navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToToRealisFragment())
    }


}