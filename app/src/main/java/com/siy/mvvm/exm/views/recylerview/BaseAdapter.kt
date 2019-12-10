package com.siy.mvvm.exm.views.recylerview

import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.diff.BaseQuickAdapterListUpdateCallback
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 *
 * Created by Siy on  2019/12/10.
 *
 * @author Siy
 */
abstract class BaseAdapter<T, K : BaseViewHolder>
@JvmOverloads constructor(
    @LayoutRes layoutRes: Int = 0,
    data: List<T>? = null,
    private val diffCallback: DiffUtil.ItemCallback<T>? = null
) : BaseQuickAdapter<T, K>(layoutRes, data) {

    var mListener: ((previousList: List<T>, currentList: List<T>) -> Unit)? = null

    fun asyncDisffData(newData: List<T>?, lifecycleScope: LifecycleCoroutineScope?) {
        if (newData == mData) {
            return
        }
        val previousList = mData
        if (newData.isNullOrEmpty()) {
            val countRemoved = mData.size
            mData = listOf()
            val mUpdateCallback = BaseQuickAdapterListUpdateCallback(this)
            mUpdateCallback.onRemoved(0, countRemoved)
            mListener?.invoke(previousList, listOf())
            return
        }

        if (mData.isNullOrEmpty()) {
            setNewData(newData)
            mListener?.invoke(previousList, newData)
            return
        }

        val func = suspend {
            diffCallback?.let {
                val result = withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(object : BaseQuickDiffCallback<T>(newData) {
                        init {
                            oldList = data
                        }

                        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                            return diffCallback.areItemsTheSame(oldItem, newItem)
                        }

                        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                            return diffCallback.areContentsTheSame(oldItem, newItem)
                        }

                        override fun getChangePayload(oldItem: T, newItem: T): Any? {
                            return diffCallback.getChangePayload(oldItem, newItem)
                        }

                    }, true)
                }
                setNewDiffData(result, newData)
                mListener?.invoke(previousList, newData)
            }
        }

        if (lifecycleScope != null) {
            lifecycleScope.launchWhenStarted {
                func()
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                func()
            }
        }
    }

    protected fun getViewHolderPosition(viewHolder: RecyclerView.ViewHolder) =
        viewHolder.adapterPosition - headerLayoutCount
}