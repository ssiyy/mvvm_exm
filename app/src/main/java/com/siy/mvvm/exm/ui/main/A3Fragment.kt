package com.siy.mvvm.exm.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.databinding.FragmentA3Binding
import com.siy.mvvm.exm.databinding.ItemBarLayoutBinding
import com.siy.mvvm.exm.databinding.MarkViewWindowItemLayoutBinding
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


                        showToast(position.toString() + "${splitLine?.toString()}")

                        val ss = IntArray(2)
                        splitLine.getLocationOnScreen(ss)


//                        val popupWindow = MarkViewWindow(requireActivity())


                        val view = LayoutInflater.from(mContext)
                            .inflate(R.layout.mark_view_window_layout, null, false)
                        val rv = view.findViewById<RecyclerView>(R.id.recycler_view)
                        rv.adapter = MarkViewAdapter(
                            kotlin.collections.listOf(
                                kotlin.Pair(android.graphics.Color.GREEN, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.BLUE, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.BLUE, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.BLUE, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.BLUE, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.BLUE, "测试一：1000"),
                                kotlin.Pair(android.graphics.Color.WHITE, "测试一：1000")
                            )
                        )

                        // 在popupWindow还没有弹出显示之前就测量获取其宽高（单位是px像素）
                        val widthSpec =
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
                        val heightSpec =
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST)
                        view.measure(widthSpec, heightSpec)
                        val viewWidth: Int = view.measuredWidth //获取测量宽度px


                        val popupWindow = PopupWindow()

                        popupWindow.isFocusable = true
                        popupWindow.isOutsideTouchable = true
                        popupWindow. setBackgroundDrawable(ColorDrawable(0x00000000))
                        popupWindow.  width = viewWidth
                        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        popupWindow.contentView = view

                        popupWindow.showAtLocation(
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


class MarkViewWindow(val mContext: Context) : PopupWindow(mContext) {


    init {

        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(0x00000000))

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.mark_view_window_layout, null, false)

        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = MarkViewAdapter(
            listOf(
                Pair(Color.GREEN, "测试一：1000"),
                Pair(Color.BLUE, "测试一：1000"),
                Pair(Color.BLUE, "测试一：1000"),
                Pair(Color.BLUE, "测试一：1000"),
                Pair(Color.BLUE, "测试一：1000"),
                Pair(Color.BLUE, "测试一：1000"),
                Pair(Color.WHITE, "测试一：1000")
            )
        )


        // 在popupWindow还没有弹出显示之前就测量获取其宽高（单位是px像素）
        val w = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        val h = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        view.measure(w, h)
        //获取测量宽度px
        val viewWidth: Int = view.measuredWidth



        width = viewWidth
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        contentView = view
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