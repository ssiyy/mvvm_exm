package com.siy.mvvm.exm.ui.square

import androidx.recyclerview.widget.DiffUtil
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.databinding.ItemUserArticleBinding
import com.siy.mvvm.exm.ui.UserArticle
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter

class SquareListAdapter(
    datas: List<UserArticle>? = null
) : BaseDataBindingAdapter<UserArticle, ItemUserArticleBinding>(
    R.layout.item_user_article,
    datas,
    object : DiffUtil.ItemCallback<UserArticle>(){
        override fun areItemsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun convert(binding: ItemUserArticleBinding?, item: UserArticle) {
        binding?.article = item
    }
}