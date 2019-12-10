package com.siy.mvvm.exm.views.recylerview.databindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.siy.mvvm.exm.views.recylerview.BaseAdapter


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
abstract class BaseDataBindingAdapter<T, B : ViewDataBinding>
@JvmOverloads constructor(
    @LayoutRes layoutRes: Int = 0,
    data: List<T>? = null,
    diffCallback: DiffUtil.ItemCallback<T>? = null
) : BaseAdapter<T, BaseBindingViewHolder<B>>(layoutRes, data, diffCallback) {

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
}