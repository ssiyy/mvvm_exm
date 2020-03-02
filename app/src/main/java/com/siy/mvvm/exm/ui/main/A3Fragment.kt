package com.siy.mvvm.exm.ui.main

import android.graphics.Color
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.databinding.FragmentA3Binding
import com.siy.mvvm.exm.databinding.ItemBarLayoutBinding
import com.siy.mvvm.exm.utils.showToast
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter

class A3Fragment(override val layoutId: Int = R.layout.fragment_a3) :
    BaseLazyFragment<FragmentA3Binding>() {
    override fun initViewsAndEvents(view: View) {
        mViewDataBinding.run {
            rv.adapter = BarAdapter(
                listOf(
                    Pair("斑斑", listOf(Pair(Color.RED, 11.2f),Pair(Color.GRAY, 11.2f),Pair(Color.GREEN, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.BLACK, 11.2f)))
                )
            ).apply {
                onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                         showToast(position.toString())
                    }


                onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                    showToast(position.toString())

                    false
                }
            }
        }
    }
}


class BarAdapter(datas: List<Pair<String, List<Pair<Int, Float>>>>) :
    BaseDataBindingAdapter<Pair<String, List<Pair<Int, Float>>>, ItemBarLayoutBinding>(
        R.layout.item_bar_layout,
        datas
    ) {
    override fun convert(
        binding: ItemBarLayoutBinding?,
        item: Pair<String, List<Pair<Int, Float>>>
    ) {
        binding?.label = item.first

        binding?.barView?.dataColors = item.second
    }

}