package com.siy.mvvm.exm.ui.firstpage

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.GbdDb
import com.siy.mvvm.exm.base.Injectable
import com.siy.mvvm.exm.base.repository.BaseRepository
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentFirstpageLayoutBinding
import com.siy.mvvm.exm.db.dao.ArticleDao
import com.siy.mvvm.exm.db.dao.BannerDao
import com.siy.mvvm.exm.http.GbdService
import com.siy.mvvm.exm.http.PAGESTATUS
import com.siy.mvvm.exm.http.Status
import com.siy.mvvm.exm.ui.Banner
import com.siy.mvvm.exm.utils.autoCleared
import com.siy.mvvm.exm.utils.dip2px
import com.siy.mvvm.exm.utils.inflater
import com.siy.mvvm.exm.utils.setupRefreshLayout
import com.siy.mvvm.exm.views.header.CommonHeader
import com.siy.mvvm.exm.views.loopingviewpager.LoopViewPager
import com.siy.mvvm.exm.views.search.AutoSearch
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
                    showTitleIcon.value = false
                }

                override fun onBackClick() {
                    navController.popBackStack()
                }
            }

            search = object : AutoSearch(viewLifecycleOwner) {
                override fun searchApi(searchStr: String) {
                    viewModel.showArctiles(searchStr)
                }

            }

            click0s = mapOf(
                "onRefresh" to viewModel::refresh,
                "onLoadMore" to viewModel::loadMore
            )

            click1s = mapOf(
                "onItemClick" to
                        fun(postion: Int) {
                            val item = this@FirstPageFragment.adapter.getItem(postion)
                            item?.let {
                                navController.navigateAnimate(
                                    FirstPageFragmentDirections.actionFirstPageFragmentToWebViewFragment(
                                        it.link
                                    )
                                )
                            }
                        }
            )

            adapter = ArticleListAdapter(null).apply {
                this@FirstPageFragment.adapter = this

                addHeaderView(LoopViewPager(context).apply {
                    layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(200f))
                    headerView = this
                })

                setHeaderAndEmpty(true)
            }
            setupRefreshLayout(srlLayout, recyclerView)
        }
        setUpObserver()
        viewModel.showArctiles(mViewDataBinding?.search?.searchStr?.value ?: "")
    }


    private fun setUpObserver() {
        viewModel.banners.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> lifecycleScope.launchWhenStarted {
                    hideLoadingDialog()
                    addBanner(it.data)
                }
                else -> Unit
            }
        }

        viewModel.articleList.observe(viewLifecycleOwner) {
            lifecycleScope.launchWhenStarted {
                adapter.asyncSetDisffData(it)
            }
        }

        viewModel.loadState.observe(viewLifecycleOwner) {
            when (it.status) {
                PAGESTATUS.COMPLETE -> {
                    adapter.loadMoreComplete()
                }
                PAGESTATUS.ERROR, PAGESTATUS.END -> {
                    adapter.loadMoreEnd()
                }
                else -> Unit
            }
        }

        viewModel.refreshState.observe(viewLifecycleOwner) {
            when (it.status) {
                PAGESTATUS.LOADING ->
                    if (mViewDataBinding?.srlLayout?.isRefreshing == false) {
                        mViewDataBinding?.srlLayout?.isRefreshing = true
                    }
                PAGESTATUS.ERROR, PAGESTATUS.COMPLETE -> {
                    stopRefresh()
                }
                else -> Unit
            }
        }
    }

    /**
     * 停止刷新
     */
    private fun stopRefresh() {
        if (mViewDataBinding?.srlLayout?.isRefreshing == true) {
            mViewDataBinding?.srlLayout?.isRefreshing = false
        }
    }

    private lateinit var headerView: LoopViewPager

    private fun addBanner(banners: List<Banner>?) {
        if (!banners.isNullOrEmpty()) {
            headerView.adapter = BannerAdapter(banners)
        }
    }

    class BannerAdapter(private var banners: List<Banner>) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = banners.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = container.inflater.inflate(R.layout.fleet_free_banner_view, null, false)
            val view1 = view.findViewById<ImageView>(R.id.imageview)
            view1.scaleType = ImageView.ScaleType.CENTER_CROP
            view1.setBackgroundResource(R.drawable.common_shape_banner_bg)
            Glide.with(container.context).load(banners[position].imagePath).into(view1)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}

class FirstPageViewModel @Inject constructor(
    rep: FirstPageRep
) : ViewModel() {
    val banners = rep.getBanners()

    /**
     * 搜索的关键字
     */
    private val searchStr = MutableLiveData<String>()
    private val articleResult = searchStr.map {
        rep.getArtclesByPage(it)
    }

    /**
     * 列表
     */
    val articleList = articleResult.switchMap {
        it.list
    }


    /**
     * 加载状态
     */
    val loadState = articleResult.switchMap {
        it.loadStatus
    }

    /**
     * 刷新状态
     */
    val refreshState = articleResult.switchMap {
        it.refreshStatus
    }

    /**
     * 加载更多方法
     */
    fun loadMore() {
        articleResult.value?.loadData?.invoke()
    }

    /**
     * 刷新方法
     */
    fun refresh() {
        articleResult.value?.refresh?.invoke()
    }

    fun showArctiles(str: String): Boolean {
        if (searchStr.value == str) {
            return false
        }
        searchStr.value = str
        return true
    }
}

@Singleton
class FirstPageRep @Inject constructor(
    private val service: GbdService,
    private val bannerDao: BannerDao,
    private val articleDao: ArticleDao,
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


    fun getArtclesByPage(search: String) =
        loadDataByPage(
            {
                articleDao.queryBySearchStr(search)
            },
            {
                it
            },
            {
                if (search.isEmpty()) {
                    service.getHomeArticles(it!!)
                } else {
                    //只做本地搜索
                    null
                }
            },
            { list, isRefresh ->
                if (isRefresh) {
                    articleDao.deleteAll()
                }

                if (!list.isNullOrEmpty()) {
                    val sum = articleDao.queryDataSum()

                    articleDao.insertAll(list.mapIndexed { index, article ->
                        article._order_ = sum + index
                        article
                    })
                }
            },
            {
                it?.data?.datas
            }
        )

}