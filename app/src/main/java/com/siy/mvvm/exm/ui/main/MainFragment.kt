package com.siy.mvvm.exm.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.databinding.FragmentMainBinding
import com.siy.mvvm.exm.ui.main.me.MyFragment
import com.siy.mvvm.exm.ui.main.message.MessageFragment
import com.siy.mvvm.exm.ui.main.realis.RealisFragment
import com.siy.mvvm.exm.ui.main.search.SearchFragment
import com.siy.mvvm.exm.views.MainIndicator

class MainFragment(override val layoutId: Int = R.layout.fragment_main) : BaseFragment<FragmentMainBinding>() {


    override fun initViewsAndEvents(view: View) {
        initPages()
    }


    private fun initPages() {
        val fragments = listOf<Fragment>(MessageFragment(), RealisFragment(), SearchFragment(), MyFragment())
        val adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int) = fragments[position]

            override fun getCount() = fragments.size
        }
        mViewDataBinding?.viewPager?.let {
            it.offscreenPageLimit = 3
            it.adapter = adapter
        }

        mViewDataBinding?.mainIndicator?.initPosition(MainIndicator.VG2_CODE) { code ->
            val vp = mViewDataBinding?.viewPager
            when (code) {
                MainIndicator.VG1_CODE -> vp?.currentItem = 0
                MainIndicator.VG2_CODE -> vp?.currentItem = 1
                MainIndicator.VG4_CODE -> vp?.currentItem = 2
                MainIndicator.VG5_CODE -> vp?.currentItem = 3
                MainIndicator.VG_MAIN_CODE -> {
                }
            }
        }
    }
}