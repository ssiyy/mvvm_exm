package com.siy.mvvm.exm.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


/**
 * 扩展[SwipeRefreshLayout]以支持非直接后代滚动视图。
 *
 * 当滚动视图是直接子视图时，[SwipeRefreshLayout]按预期工作：仅当视图位于顶部时才触发刷新。
 * 此类添加了一种方法（@link #setScrollUpChild}来定义哪个视图控制此行为。
 *
 * Created by Siy on 2019/07/18.
 *
 * @author Siy
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {
    var scrollUpChild: View? = null

    override fun canChildScrollUp() =
            scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
}