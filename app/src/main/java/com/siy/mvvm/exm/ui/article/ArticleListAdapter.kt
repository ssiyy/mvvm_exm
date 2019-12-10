package com.siy.mvvm.exm.ui.article

import androidx.recyclerview.widget.DiffUtil
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
        R.layout.item_article,
        datas,
        object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

        }
    ) {
    override fun convert(binding: ItemArticleBinding?, item: Article) {
        binding?.article = item
    }
}