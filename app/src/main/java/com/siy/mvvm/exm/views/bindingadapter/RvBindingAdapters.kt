package com.siy.mvvm.exm.views.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
object RvBindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter(value = ["android:adapter", "android:onItemClick", "android:onLoadMore", "android:onItemLongClick", "android:emptyView", "android:loadMoreEnable"], requireAll = false)
    fun bindRecylerView(recylerView: RecyclerView, adapter: RecyclerView.Adapter<*>?, itemClickListener: ItemClickListener?, loadMoreListener: LoadMoreListener?,
                        itemLongClickListener: ItemLongClickListener?, emptyView: View?, loadMoreEnable: Boolean) {
        if (adapter is BaseQuickAdapter<*, *>) {
            adapter.bindToRecyclerView(recylerView)
        } else {
            if (adapter != null) {
                recylerView.adapter = adapter
            }
        }

        val recylerViewAdapter = recylerView.adapter as? BaseQuickAdapter<*, *> ?: return

        if (itemClickListener != null) {
            recylerViewAdapter.setOnItemClickListener(itemClickListener::onItemClick)
        }

        if (loadMoreListener != null) {
            recylerViewAdapter.setOnLoadMoreListener(loadMoreListener::onLoadMore, recylerView)
        }

        if (itemLongClickListener != null) {
            recylerViewAdapter.setOnItemLongClickListener(itemLongClickListener::onItemLongClick)
        }

        if (emptyView != null) {
            recylerViewAdapter.emptyView = emptyView
        }

        recylerViewAdapter.setEnableLoadMore(loadMoreEnable)
        recylerViewAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
    }
}

interface ItemClickListener {
    fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}

interface LoadMoreListener {
    fun onLoadMore()
}

interface ItemLongClickListener {
    fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
}
