package com.siy.mvvm.exm.ui.forResult

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.databinding.FragmentForResultBinding
import com.siy.mvvm.exm.utils.popBackForResult


/**
 * Created by Siy on 2019/10/11.
 *
 * @author Siy
 */
class ForResultFragment(override val layoutId: Int = R.layout.fragment_for_result) :
    BaseFragment<FragmentForResultBinding>() {

    private val args: ForResultFragmentArgs by navArgs()

    private var inputStr = MutableLiveData<String>()

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding?.let { binding ->
            binding.lable = args.label
            binding.input = inputStr
            binding.click0 = fun() {
                popBackForResult(bundleOf("result" to inputStr.value))
            }
        }
    }

}