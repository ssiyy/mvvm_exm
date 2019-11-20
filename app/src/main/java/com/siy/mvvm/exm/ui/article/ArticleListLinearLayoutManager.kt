package com.siy.mvvm.exm.ui.article

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.siy.mvvm.exm.R


/**
 * Created by Siy on 2019/10/23.
 *
 * @author Siy
 */
class ArticleListLinearLayoutManager : LinearLayoutManager {

    @Suppress("unused")
    constructor(context: Context) : super(context)

    @Suppress("unused")
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    @Suppress("unused")
    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    private var mPendingVpPosition = RecyclerView.NO_POSITION

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = bundleOf("instanceState" to super.onSaveInstanceState())
        val bannerView = findBannerVp()
        bannerView?.run {
            bundle.putInt("pos", currentItem)
        }
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            mPendingVpPosition = state.getInt("pos", 0)
            val superState: Parcelable? = state.getParcelable("instanceState")
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        //下面是一些调试信息
        Log.e(
            "siy",
            "saveState:${refectValue<SavedState>(
                "mPendingSavedState",
                LinearLayoutManager::class.java
            )?.showMsg()}"
        )
        Log.e(
            "siy",
            "mPendingScrollPosition:${refectValue<Int>(
                "mPendingScrollPosition",
                LinearLayoutManager::class.java
            )}"
        )
        Log.e(
            "siy",
            "mAnchorInfo:${refectValue<Any>("mAnchorInfo", LinearLayoutManager::class.java)}"
        )

    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        val bannerView = findBannerVp()
        bannerView?.run {
            if (mPendingVpPosition != RecyclerView.NO_POSITION) {
                currentItem = mPendingVpPosition
                mPendingVpPosition = RecyclerView.NO_POSITION
            }
        }
        Log.e("siy", "onLayoutCompleted")
    }


    private fun findHeaderView(): View? {
        val recyclerView: RecyclerView? =
            refectValue("mRecyclerView",  RecyclerView.LayoutManager::class.java)

        val view = (recyclerView?.adapter as? BaseQuickAdapter<*, *>)?.headerLayout

        return if (view?.id ?: View.NO_ID == R.id.rv_header_id) {
            view
        } else {
            null
        }
    }

    private fun findBannerVp(): ViewPager? {
        val headerView = findHeaderView()
        val view = (headerView as? ViewGroup)?.getChildAt(0)
        return if (view is ViewPager) {
            view
        } else {
            null
        }
    }

}

fun LinearLayoutManager.SavedState.showMsg(): String {
    return "SavedState{" +
            "mAnchorPosition=${refectValue<Int>("mAnchorPosition")}, " +
            "mAnchorOffset=${refectValue<Int>("mAnchorOffset")}, " +
            "mAnchorLayoutFromEnd=${refectValue<Boolean>("mAnchorLayoutFromEnd")}}"
}


inline fun <reified T> Any.refectValue(fieldName: String, clazz: Class<*> = this::class.java): T? {
    return try {
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        val fieldValue = field.get(this)
        field.isAccessible = false
        fieldValue as T
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}