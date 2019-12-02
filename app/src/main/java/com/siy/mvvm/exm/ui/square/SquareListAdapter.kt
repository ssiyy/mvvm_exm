package com.siy.mvvm.exm.ui.square

import androidx.lifecycle.LifecycleCoroutineScope
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.databinding.ItemUserArticleBinding
import com.siy.mvvm.exm.ui.UserArticle
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter

class SquareListAdapter(datas: List<UserArticle>? = null) :
    BaseDataBindingAdapter<UserArticle, ItemUserArticleBinding>(
        R.layout.item_user_article, datas
    ) {
    override fun convert(binding: ItemUserArticleBinding?, item: UserArticle) {
        binding?.article = item
    }

    fun asyncSetDisffData(newList: List<UserArticle>?, lifecycleScope: LifecycleCoroutineScope) {
        asyncDisffData(newList, DiffCallBack(newList, data), lifecycleScope)
    }


    private class DiffCallBack(newList: List<UserArticle>?, oldList: List<UserArticle>) :
        BaseQuickDiffCallback<UserArticle>(newList) {

        init {
            setOldList(oldList)
        }

        /**
         * 判断是否是同一个Item
         */
        override fun areItemsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         *当是同一个item时，再判断内容是否发生改变
         */
        override fun areContentsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
            return oldItem == newItem
        }

    }
}