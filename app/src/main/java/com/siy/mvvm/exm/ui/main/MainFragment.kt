package com.siy.mvvm.exm.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.databinding.FragmentMainBinding
import com.siy.mvvm.exm.utils.GDB_ERROR
import com.siy.mvvm.exm.utils.autoDisposable
import com.siy.mvvm.exm.utils.goToAppMarket
import com.siy.mvvm.exm.utils.showToast
import com.siy.mvvm.exm.views.MainIndicator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.FlutterView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.processors.PublishProcessor
import java.util.concurrent.TimeUnit

class MainFragment(
    override val layoutId: Int = R.layout.fragment_main,
    override val regBackPressed: Boolean = true
) :
    BaseFragment<FragmentMainBinding>() {

    /**
     * 用来判断是否是单击的
     */
    private val backPressProessor = PublishProcessor.create<Byte>()

    override fun onBackPressed() {
        backPressProessor.onNext(0.toByte())
    }

    /**
     * 注册判断点击的逻辑
     */
    private fun registerBackPress() {
        backPressProessor.buffer(backPressProessor.debounce(500, TimeUnit.MILLISECONDS))
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(autoDisposable())
            .subscribe(Consumer {
                if (it.size < 2) {
                    showToast("快速点击2次返回键退出")
                } else {
                    requireActivity().finish()
                }
            }, GDB_ERROR)
    }

    override fun initViewsAndEvents(view: View) {
        registerBackPress()
        initPages()
    }

    private fun initPages() {
        val fragments =
            listOf(
                (FlutterFragment.withNewEngine()
                    .renderMode(FlutterView.RenderMode.texture)
                    .initialRoute("abcjjjjjjjj")
                    .build()) as Fragment,
                FirstPageFragment(),
                A2Fragment(),
                A3Fragment()
            )
        val adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int) = fragments[position]

            override fun getCount() = fragments.size
        }
        mViewDataBinding.viewPager?.let {
            it.offscreenPageLimit = 3
            it.adapter = adapter
        }

        mViewDataBinding.mainIndicator?.initPosition(MainIndicator.VG2_CODE) { code ->
            val vp = mViewDataBinding.viewPager
            when (code) {
                MainIndicator.VG1_CODE -> vp?.currentItem = 0
                MainIndicator.VG2_CODE -> vp?.currentItem = 1
                MainIndicator.VG4_CODE -> vp?.currentItem = 2
                MainIndicator.VG5_CODE -> vp?.currentItem = 3
                MainIndicator.VG_MAIN_CODE -> {
                    mContext.applicationContext.goToAppMarket()
                }
            }
        }
    }
}