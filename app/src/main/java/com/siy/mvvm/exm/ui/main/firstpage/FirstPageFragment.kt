package com.siy.mvvm.exm.ui.main.firstpage

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.GbdDb
import com.siy.mvvm.exm.base.Injectable
import com.siy.mvvm.exm.base.repository.BaseRepository
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.databinding.FragmentFirstpageLayoutBinding
import com.siy.mvvm.exm.db.dao.BannerDao
import com.siy.mvvm.exm.http.GbdService
import com.siy.mvvm.exm.http.Status
import com.siy.mvvm.exm.ui.Banner
import com.siy.mvvm.exm.utils.autoCleared
import com.siy.mvvm.exm.utils.setupRefreshLayout
import com.siy.mvvm.exm.views.header.CommonHeader
import com.siy.mvvm.exm.views.loopingviewpager.LoopViewPager
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */
class FirstPageFragment(override val layoutId: Int = R.layout.fragment_firstpage_layout) :
    BaseFragment<FragmentFirstpageLayoutBinding>(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var adapter by autoCleared<ArticleListAdapter>()

    private val viewModel: FirstPageViewModel by viewModels {
        viewModelFactory
    }

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding?.run {
            header = object : CommonHeader() {
                init {
                    title.value = "首页"
                }

                override fun onBackClick() {
                    navController.popBackStack()
                }
            }

            click0s = mapOf(
                "onRefresh" to
                        fun() {

                        },
                "onLoadMore" to
                        fun() {

                        }
            )

            click1s = mapOf(
                "onItemClick" to
                        fun(postion: Int): Boolean {

                            return false
                        },
                "onItemLongLick" to
                        fun(postion: Int): Boolean {

                            return true
                        }
            )

            adapter = ArticleListAdapter(null).apply {
                this@FirstPageFragment.adapter = this
                addHeaderView(headerView)
            }

            setupRefreshLayout(srlLayout, recyclerView)
        }
        setUpObserver()
    }


    private fun setUpObserver() {
        viewModel.banners.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> showLoadingDialog("加载中")
                Status.SUCCESS -> lifecycleScope.launchWhenStarted {
                    hideLoadingDialog()
                    addBanner(it.data)
                }
            }

        }
    }

    private val headerView by lazy {
        val looperPage = LoopViewPager(context)
        looperPage.adapter = BannerAdapter()
        looperPage
    }

    class BannerAdapter : PagerAdapter() {

        private var banners: List<Banner> = listOf()

        fun setDatas(banners: List<Banner>) {
            this.banners = banners
            notifyDataSetChanged()
        }

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = banners.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val iv = ImageView(container.context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            Glide.with(container.context!!).load(banners[position]).into(iv)
            container.addView(iv)
            return iv
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }


    private fun addBanner(banners: List<Banner>?) {
        if (!banners.isNullOrEmpty()) {
            (headerView.adapter as? BannerAdapter)?.setDatas(banners)
        }
    }
}

class FirstPageViewModel @Inject constructor(
    rep: FirstPageRep
) : ViewModel() {
    val banners = rep.getBanners()
}

@Singleton
class FirstPageRep @Inject constructor(
    private val service: GbdService,
    private val bannerDao: BannerDao,
    private val db: GbdDb

) : BaseRepository() {


    fun getBanners() = loadData(
        {
            bannerDao.queryAll()
        },
        {
            service.getBanner()
        }, {
            db.runInTransaction {
                bannerDao.deleteAll()
                it?.let { banners ->
                    bannerDao.insertAll(banners)
                }
            }
        }
    )

}