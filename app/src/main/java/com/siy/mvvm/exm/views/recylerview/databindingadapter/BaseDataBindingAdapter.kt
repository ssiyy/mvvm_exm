package com.siy.mvvm.exm.views.recylerview.databindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.diff.BaseQuickAdapterListUpdateCallback
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
abstract class BaseDataBindingAdapter<T, B : ViewDataBinding> :
    BaseQuickAdapter<T, BaseBindingViewHolder<B>> {

    var mListener: ((previousList: List<T>, currentList: List<T>) -> Unit)? = null

    constructor(@LayoutRes layoutRes: Int, data: List<T>?) : super(layoutRes, data)

    @Suppress("unused")
    constructor(data: List<T>?) : super(data)

    @Suppress("unused")
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    override fun createBaseViewHolder(view: View): BaseBindingViewHolder<B> {
        return BaseBindingViewHolder(view)
    }

    override fun createBaseViewHolder(
        parent: ViewGroup?,
        layoutResId: Int
    ): BaseBindingViewHolder<B> {
        val b = DataBindingUtil.inflate<B>(mLayoutInflater, layoutResId, parent, false)
        val view = b?.root ?: getItemView(layoutResId, parent)
        val holder = BaseBindingViewHolder<B>(view)
        holder.binding = b
        return holder
    }

    override fun convert(helper: BaseBindingViewHolder<B>, item: T) {
        convert(helper.binding, item)
        helper.binding?.executePendingBindings()
    }

    protected abstract fun convert(binding: B?, item: T)

    protected fun getViewHolderPosition(viewHolder: RecyclerView.ViewHolder) =
        viewHolder.adapterPosition - headerLayoutCount

    fun asyncDisffData(
        newData: List<T>?,
        diffCallBack: BaseQuickDiffCallback<T>,
        lifecycleScope: LifecycleCoroutineScope
    ) {
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

        lifecycleScope.launchWhenStarted {
            val result = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(diffCallBack, true)
            }
            setNewDiffData(result, newData)
            mListener?.invoke(previousList, newData)
        }
    }

    fun syncDisffData(diffCallBack: BaseQuickDiffCallback<T>) = setNewDiffData(diffCallBack)

}