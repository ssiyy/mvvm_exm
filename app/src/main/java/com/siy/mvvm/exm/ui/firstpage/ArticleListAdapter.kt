package com.siy.mvvm.exm.ui.firstpage

import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.databinding.ItemArticleBinding
import com.siy.mvvm.exm.ui.Article
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */
class ArticleListAdapter(datas: List<Article>?) :
    BaseDataBindingAdapter<Article, ItemArticleBinding>(
        R.layout.item_article, datas
    ) {

    override fun convert(binding: ItemArticleBinding?, item: Article) {
        binding?.article = item
    }

    suspend fun asyncSetDisffData(newList: List<Article>?) {
        asyncDisffData(newList, DiffCallBack(newList, data))
    }

    private class DiffCallBack(newList: List<Article>?, oldList: List<Article>) :
        BaseQuickDiffCallback<Article>(newList) {

        init {
            setOldList(oldList)
        }

        /**
         * 判断是否是同一个Item
         */
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         *当是同一个item时，再判断内容是否发生改变
         */
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
}