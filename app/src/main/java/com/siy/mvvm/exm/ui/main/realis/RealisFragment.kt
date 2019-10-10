package com.siy.mvvm.exm.ui.main.realis

import android.view.View
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.databinding.FragmentLvzhiBinding

class RealisFragment(override val layoutId: Int = R.layout.fragment_lvzhi) : BaseLazyFragment<FragmentLvzhiBinding>() {
    override fun initViewsAndEvents(view: View) {
       mViewDataBinding?.run {
           hostFrg = this@RealisFragment
       }
    }

    /**
     * 岗位职责
     */
    fun toAccountability(){
   //     navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToAccountabilityFragment())
    }

    /**
     * 工作项目
     */
    fun toWorkItem(){
   //     navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToWorkItemFragment())
    }

}