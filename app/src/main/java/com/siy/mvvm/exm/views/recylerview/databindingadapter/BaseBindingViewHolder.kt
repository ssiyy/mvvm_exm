package com.siy.mvvm.exm.views.recylerview.databindingadapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder


/**
 * Created by Siy on 2019/07/16.
 *
 * @author Siy
 */
class BaseBindingViewHolder<B : ViewDataBinding>(view: View) : BaseViewHolder(view) {
     var binding: B? = null
}