package com.siy.mvvm.exm.ui.firstpage

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
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
    constructor(context: Context) : super(context)


    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
    )

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

     private fun getRecylerView() =
         try {
             val c = RecyclerView.LayoutManager::class.java
             val f = c.getDeclaredField("mRecyclerView")
             f.isAccessible = true
             val recyclerView = f.get(this)
             f.isAccessible = false
             recyclerView as? RecyclerView
         } catch (e: Exception) {
             e.printStackTrace()
             null
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


    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        val bannerView = findBannerVp()
        bannerView?.run {
            if (mPendingVpPosition != RecyclerView.NO_POSITION) {
                currentItem = mPendingVpPosition
                mPendingVpPosition = RecyclerView.NO_POSITION
            }
        }
    }


    private fun findHeaderView(): View? {
        val recyclerView = getRecylerView()

        val view = (recyclerView?.adapter as? BaseQuickAdapter<*,*>)?.headerLayout

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