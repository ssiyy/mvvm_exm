package com.siy.mvvm.exm.views.recylerview.databindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
abstract class BaseDataBindingAdapter<T, B : ViewDataBinding> : BaseQuickAdapter<T, BaseBindingViewHolder<B>> {

    constructor(@LayoutRes layoutRes: Int, data: List<T>?) : super(layoutRes, data)

    constructor(data: List<T>?) : super(data)


    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    override fun createBaseViewHolder(view: View): BaseBindingViewHolder<B> {
        return BaseBindingViewHolder(view)
    }

    override fun createBaseViewHolder(parent: ViewGroup?, layoutResId: Int): BaseBindingViewHolder<B> {
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

    suspend fun asyncDisffData(newData: List<T>?, diffCallBack: BaseQuickDiffCallback<T>) {
        val result = withContext(Dispatchers.Default) {
            DiffUtil.calculateDiff(diffCallBack, false)
        }
        setNewDiffData(result, newData?: listOf())
    }
}