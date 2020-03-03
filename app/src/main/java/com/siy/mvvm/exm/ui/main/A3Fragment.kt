package com.siy.mvvm.exm.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.databinding.FragmentA3Binding
import com.siy.mvvm.exm.databinding.ItemBarLayoutBinding
import com.siy.mvvm.exm.databinding.MarkViewWindowItemLayoutBinding
import com.siy.mvvm.exm.databinding.MarkViewWindowLayoutBinding
import com.siy.mvvm.exm.utils.showToast
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter


class A3Fragment(override val layoutId: Int = R.layout.fragment_a3) :
    BaseLazyFragment<FragmentA3Binding>() {
    override fun initViewsAndEvents(view: View) {
        mViewDataBinding.run {
            rv.adapter = BarAdapter(
                listOf(
                    Pair(
                        "斑斑",
                        listOf(
                            Pair(Color.RED, 11.2f),
                            Pair(Color.GRAY, 11.2f),
                            Pair(Color.GREEN, 11.2f)
                        )
                    )
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.GRAY, 11.2f), Pair(Color.GRAY, 11.2f)))
                    , Pair("斑斑", listOf(Pair(Color.BLACK, 11.2f)))
                )
            ).apply {
                onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                        showToast(position.toString())
                    }


                onItemLongClickListener =
                    BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                        val splitLine = view.findViewById<View>(R.id.split_line)
                        val ss = IntArray(2)
                        splitLine.getLocationOnScreen(ss)
                        val popupWindow = MarkViewWindow(requireActivity())

                        popupWindow.showAtLocation(
                            Pair("chhch",
                                listOf(
                                    Pair(Color.GREEN, "测试一：1000"),
                                    Pair(Color.BLUE, "测试一：1000"),
                                    Pair(Color.BLUE, "测试一：1000"),
                                    Pair(Color.BLUE, "测试一：1000"),
                                    Pair(Color.BLUE, "测试一：1000"),
                                    Pair(Color.BLUE, "测试一：10000000000000000"),
                                    Pair(Color.WHITE, "测试一：1000")
                                )),
                            requireActivity().window.decorView,
                            Gravity.NO_GRAVITY,
                            ss[0],
                            ss[1]
                        )

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


class MarkViewWindow(mContext: Context) : PopupWindow(mContext) {
    private val dataBinding =
        DataBindingUtil.inflate<MarkViewWindowLayoutBinding>(LayoutInflater.from(mContext),R.layout.mark_view_window_layout, null, false)

    init {
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(0x00000000))

        contentView = dataBinding.run {
            recyclerView.adapter =  MarkViewAdapter()
            root
        }
    }


    fun showAtLocation(
        data: Pair<String, List<Pair<Int, String>>>,
        parent: View?,
        gravity: Int,
        x: Int,
        y: Int
    ) {

        dataBinding.lable.text = data.first
        (dataBinding.recyclerView.adapter as MarkViewAdapter).setNewData(data.second)

        // 在popupWindow还没有弹出显示之前就测量获取其宽高（单位是px像素）
        val w = View.MeasureSpec.makeMeasureSpec(
            (1 shl 30) - 1,
            View.MeasureSpec.AT_MOST
        )
        val h = View.MeasureSpec.makeMeasureSpec(
            (1 shl 30) - 1,
            View.MeasureSpec.AT_MOST
        )
        contentView.measure(w, h)

        width = contentView.measuredWidth
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        super.showAtLocation(parent, gravity, x, y)
    }


}

class MarkViewAdapter(var pair: List<Pair<Int, String>>? = null) :
    BaseDataBindingAdapter<Pair<Int, String>, MarkViewWindowItemLayoutBinding>(
        R.layout.mark_view_window_item_layout,
        pair
    ) {
    override fun convert(binding: MarkViewWindowItemLayoutBinding?, item: Pair<Int, String>) {
        binding?.run {
            point.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(item.first)
            }

            lable.text = item.second
        }
    }

}