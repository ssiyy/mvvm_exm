package com.siy.mvvm.exm.ui.main

import android.graphics.Color
import android.view.View
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.databinding.FragmentIvBinding

class A3Fragment(override val layoutId: Int = R.layout.fragment_iv) : BaseLazyFragment<FragmentIvBinding>(){
    override fun initViewsAndEvents(view: View) {
        mViewDataBinding.run {
            resId = R.drawable.a3

            bV.total = 100f
            bV.dataColors = listOf(Pair(Color.RED,100f),Pair(Color.BLUE,100f))


        }
    }

}