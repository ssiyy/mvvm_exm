package com.siy.mvvm.exm.ui.article

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.Injectable
import com.siy.mvvm.exm.base.MvvmDb
import com.siy.mvvm.exm.base.glide.GlideApp
import com.siy.mvvm.exm.base.repository.BaseRepository
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentArticleListBinding
import com.siy.mvvm.exm.db.dao.ArticleDao
import com.siy.mvvm.exm.db.dao.BannerDao
import com.siy.mvvm.exm.http.GbdService
import com.siy.mvvm.exm.http.PAGESTATUS
import com.siy.mvvm.exm.http.Status
import com.siy.mvvm.exm.ui.Banner
import com.siy.mvvm.exm.utils.dip2px
import com.siy.mvvm.exm.utils.inflater
import com.siy.mvvm.exm.utils.setupRefreshLayout
import com.siy.mvvm.exm.utils.throttleFist
import com.siy.mvvm.exm.views.header.CommonHeader
import com.siy.mvvm.exm.views.loopingviewpager.LoopViewPager
import com.siy.mvvm.exm.views.search.AutoSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import javax.inject.Inject
import javax.inject.Singleton


/**
 *
 * Created by Siy on 2019/10/10.
 *
 * 关于Navigation切换界面调用onDestroyView()和onCreateView()状态保留问题
 * Ian Lake：
 * 您不必每次调用onCreateView时都为新视图inflater-您可以保留对您第一次创建的View的引用，然后再次返回它。当然，对于不可见的内容，这会不断浪费内存和资源。保持数据>>您的视图
 *
 *  关于保留视图的引用，内存泄露问题：
 *  Ian Lake：
 *  确保您没有将setRetainInstance(true)与带有Views的Fragments一起使用，或者不在ViewModel中存储任何引用context的Views和things
 *  由于视图引用了旧的上下文，因此视图将永远无法幸免于configuration更改驱动的Activity 重启。
 *
 *  Ian Lake Tips:
 *  请记住，即使不缓存视图本身，Fragment视图也会自动保存和恢复其状态。如果不是这种情况，则应首先解决该问题（确保视图具有android：id等）。否则，保留片段中的视图不是泄漏。
 *
 *
 * @see <a href="https://issuetracker.google.com/issues/109856764">Issue Tracker -  Transaction type is not available with Navigation Architecture Component</a>
 * @see <a href="https://issuetracker.google.com/issues/127932815">Issue Tracker -   Open fragment without lose the previous fragment states</a>
 * @see <a href="https://github.com/android/architecture-components-samples/issues/530">github -  architecture-components-samples</a>
 * @see <a href="http://twitter.com/ianhlake/status/1103522856535638016">twitter - Ian Lake(Navigation)</a>
 *
 * @author Siy
 */
class ArticleListFragment(override val layoutId: Int = R.layout.fragment_article_list) :
    BaseFragment<FragmentArticleListBinding>(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FirstPageViewModel by viewModels {
        viewModelFactory
    }

    override fun initViewsAndEvents(view: View) {
        val headerView = LoopViewPager(context).apply {
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(200f))
            adapter = BannerAdapter(viewLifecycleOwner.lifecycleScope)
        }

        val artAdapter = ArticleListAdapter(null).apply {
            addHeaderView(headerView)
            headerLayout?.id = R.id.rv_header_id
            setHeaderAndEmpty(true)
        }

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

            search = object : AutoSearch(viewLifecycleOwner, viewModel.searchStr.value) {
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
                            val item = artAdapter.getItem(postion)
                            item?.let {
                                navController.navigateAnimate(
                                    ArticleListFragmentDirections.actionFirstPageFragmentToWebViewFragment(
                                        it.link
                                    )
                                )
                            }
                        }
            )
            adapter = artAdapter
            setupRefreshLayout(srlLayout, recyclerView)
        }
        setUpObserver(artAdapter, headerView)
    }


    private fun setUpObserver(adapter: ArticleListAdapter, headerView: LoopViewPager) {
        viewModel.banners.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS, Status.ERROR -> {
                    hideLoadingDialog()
                    headerView.adapter = BannerAdapter(viewLifecycleOwner.lifecycleScope, it.data)
                }
                else -> Unit
            }
        }

        viewModel.articleList.observe(viewLifecycleOwner) {
            adapter.asyncSetDisffData(it, lifecycleScope)
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

    class BannerAdapter(
        private val scope: CoroutineScope,
        private var banners: List<Banner>? = null
    ) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = banners?.size ?: 0

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = container.inflater.inflate(R.layout.fleet_free_banner_view, container, false)
            val view1 = view.findViewById<ImageView>(R.id.imageview)
            view1.scaleType = ImageView.ScaleType.CENTER_CROP
            view1.setBackgroundResource(R.drawable.common_shape_banner_bg)

            val item = banners?.get(position)
            item?.let { banner ->
                view1.clicks()
                    .throttleFist(1000)
                    .onEach {
                        view1.findNavController().navigateAnimate(
                            ArticleListFragmentDirections.actionFirstPageFragmentToWebViewFragment(
                                banner.url
                            )
                        )
                    }.launchIn(scope)

                GlideApp.with(container.context)
                    .load(banner.imagePath)
                    .transition(DrawableTransitionOptions.withCrossFade(600))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(view1)
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            val index = banners?.indexOf(`object`) ?: -1
            return if (index == -1) {
                POSITION_NONE
            } else {
                index
            }
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
    private val _searchStr = MutableLiveData<String>()
    val searchStr: LiveData<String>
        get() = _searchStr

    private val articleResult = _searchStr.map {
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
        if (_searchStr.value == str) {
            return false
        }
        _searchStr.value = str
        return true
    }
}

@Singleton
class FirstPageRep @Inject constructor(
    private val service: GbdService,
    private val bannerDao: BannerDao,
    private val articleDao: ArticleDao,
    private val db: MvvmDb

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