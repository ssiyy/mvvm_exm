package com.siy.mvvm.exm.ui.forResult

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentToResultBinding
import com.siy.mvvm.exm.utils.NavigationResult


/**
 * Created by Siy on 2019/10/11.
 *
 * @author Siy
 */
class ToResultFragment(override val layoutId: Int = R.layout.fragment_to_result) :
    BaseFragment<FragmentToResultBinding>(), NavigationResult {

    private var result = MutableLiveData<String>()

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding?.let {binding->
            binding.result = result

            binding.click0 = fun(){
                navController.navigateAnimate(ToResultFragmentDirections.actionToResultFragmentToForResultFragment().setLabel("请输入返回字符："))
            }

        }
    }

    override fun onNavigationResult(result: Bundle) {
        this.result.value = result["result"] as? String
    }

}