package com.siy.mvvm.exm.ui.article

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.diff.BaseQuickAdapterListUpdateCallback
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.databinding.ItemArticleBinding
import com.siy.mvvm.exm.ui.Article
import com.siy.mvvm.exm.utils.autoDisposable
import com.siy.mvvm.exm.views.recylerview.databindingadapter.BaseDataBindingAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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

    fun asyncSetDisffData(newList: List<Article>?, lifecycleScope: LifecycleCoroutineScope) {
        if (newList == mData) {
            return
        }

        if (newList.isNullOrEmpty()) {
            val countRemoved = mData.size
            mData = listOf()
            val mUpdateCallback = BaseQuickAdapterListUpdateCallback(this)
            mUpdateCallback.onRemoved(0, countRemoved)
            return
        }

        if (mData.isNullOrEmpty()) {
            mData = newList
            val mUpdateCallback = BaseQuickAdapterListUpdateCallback(this)
            mUpdateCallback.onInserted(0, newList.size)
            return
        }

        lifecycleScope.launchWhenStarted {
            asyncDisffData(newList, DiffCallBack(newList, data))
        }
    }

    fun syncSetDisffData(newList: List<Article>?) {
        syncDisffData(DiffCallBack(newList, data))
    }


    fun rxSetDisffData(newList: List<Article>?, lifecycleOwner: LifecycleOwner) {
        if (newList == mData) {
            return
        }

        if (newList.isNullOrEmpty()) {
            val countRemoved = mData.size
            mData = null
            val mUpdateCallback = BaseQuickAdapterListUpdateCallback(this)
            mUpdateCallback.onRemoved(0, countRemoved)
            return
        }

        if (mData.isNullOrEmpty()) {
            mData = newList
            val mUpdateCallback = BaseQuickAdapterListUpdateCallback(this)
            mUpdateCallback.onInserted(0, newList.size)
            return
        }

        Observable.just(newList)
            .map {
                DiffUtil.calculateDiff(DiffCallBack(newList, data), false)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(lifecycleOwner.autoDisposable())
            .subscribe {
                setNewDiffData(it, newList)
            }
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