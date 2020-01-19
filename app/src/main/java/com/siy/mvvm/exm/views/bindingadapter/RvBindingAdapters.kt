package com.siy.mvvm.exm.views.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.siy.mvvm.exm.http.PAGESTATUS
import com.siy.mvvm.exm.views.recylerview.BaseAdapter


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
object RvBindingAdapters {

    private fun createAdapter(recylerView: RecyclerView, className: String): RecyclerView.Adapter<*>? {
        val trimClassName = className.trim()
        return if (trimClassName.isNotEmpty()) {
            try {
                val classLoader = if (recylerView.isInEditMode) {
                    recylerView.javaClass.classLoader
                } else {
                    recylerView.context.classLoader
                }

                val adapterClass = Class.forName(trimClassName, false, classLoader)
                    .asSubclass(RecyclerView.Adapter::class.java)
                val constructor = try {
                    adapterClass.getConstructor()
                } catch (e: Exception) {
                    throw IllegalStateException(" Error creating RecyclerView.Adapter $className")
                }
                constructor.isAccessible = true
                constructor.newInstance()
            } catch (e: Exception) {
                throw IllegalStateException(" Unable to find Adapter $trimClassName")
            }
        } else {
            null
        }
    }

    @JvmStatic
    @BindingAdapter(value = [
        "android:adapter",
        "android:onItemClick",
        "android:onLoadMore",
        "android:onItemLongClick",
        "android:emptyView",
        "android:loadMoreEnable"], requireAll = false)
    fun bindRecylerViewConfig(
        recylerView: RecyclerView,
        andapterAny: Any?,
        itemClickListener: ItemClickListener?,
        loadMoreListener: LoadMoreListener?,
        itemLongClickListener: ItemLongClickListener?,
        emptyview: View?,
        loadMoreEnable: Boolean) {
        if (recylerView.adapter == null) {
            val newAdapter = if (andapterAny is String) {
                //如果是传递的字符串，就认为是传递的类路径，自己去创建
                createAdapter(recylerView, andapterAny)
            } else {
                andapterAny
            }

            if (newAdapter is BaseQuickAdapter<*, *>) {
                newAdapter.bindToRecyclerView(recylerView)
            } else if (newAdapter is RecyclerView.Adapter<*>) {
                recylerView.adapter = newAdapter
            }

            @Suppress("UNCHECKED_CAST")
            (recylerView.tag as? List<*>)?.run {
                if (newAdapter is BaseAdapter<*, *>) {
                    newAdapter.submitList(this as List<Nothing>, (recylerView.context as? LifecycleOwner)?.lifecycleScope)
                }
                //把存储list的tag清除掉
                recylerView.tag = null
            }
        }

        (recylerView.adapter as? BaseQuickAdapter<*, *>)?.run {
            itemClickListener?.let {
                setOnItemClickListener(it::onItemClick)
            }

            itemLongClickListener?.let {
                setOnItemLongClickListener(it::onItemLongClick)
            }

            loadMoreListener?.let {
                setOnLoadMoreListener(it::onLoadMore, recylerView)
            }

            emptyview?.let {
                emptyView = it
            }
            setEnableLoadMore(loadMoreEnable)
            openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter(value = ["android:listItems"], requireAll = false)
    fun <T> bindRecylerViewListItems(recylerView: RecyclerView, listItems: List<T>?) {
        if (recylerView.adapter == null && !listItems.isNullOrEmpty()) {
            recylerView.tag = listItems
        } else {
            (recylerView.adapter as? BaseAdapter<T, *>)?.run {
                submitList(listItems, (recylerView.context as? LifecycleOwner)?.lifecycleScope)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter(value = ["android:loadState"], requireAll = false)
    fun <T> bindRecylerViewLoadState(recylerView: RecyclerView, state: PAGESTATUS?) {
        if (state != null) {
            (recylerView.adapter as? BaseQuickAdapter<T, *>)?.run {
                when (state) {
                    PAGESTATUS.COMPLETE -> loadMoreComplete()
                    PAGESTATUS.END, PAGESTATUS.ERROR -> loadMoreEnd()
                    else -> Unit
                }
            }
        }
    }
}


interface ItemClickListener {
    fun onItemClick(adapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, position: Int)
}

interface LoadMoreListener {
    fun onLoadMore()
}

interface ItemLongClickListener {
    fun onItemLongClick(adapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, position: Int): Boolean
}
