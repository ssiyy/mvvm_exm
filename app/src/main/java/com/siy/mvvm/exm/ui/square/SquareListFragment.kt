package com.siy.mvvm.exm.ui.square

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.Injectable
import com.siy.mvvm.exm.base.repository.BaseRepository
import com.siy.mvvm.exm.base.repository.loadFlowDataByPage
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentArticleListBinding
import com.siy.mvvm.exm.db.dao.UserArticleDao
import com.siy.mvvm.exm.http.GbdService
import com.siy.mvvm.exm.http.PAGESTATUS
import com.siy.mvvm.exm.utils.setupRefreshLayout
import com.siy.mvvm.exm.views.header.CommonHeader
import com.siy.mvvm.exm.views.search.AutoSearch
import javax.inject.Inject
import javax.inject.Singleton

class SquareListFragment(override val layoutId: Int = R.layout.fragment_article_list) :
    BaseFragment<FragmentArticleListBinding>(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SqueareListModel by viewModels {
        viewModelFactory
    }

    override fun initViewsAndEvents(view: View) {

        val squareAdapter = SquareListAdapter()

        mViewDataBinding?.run {
            header = object : CommonHeader() {
                init {
                    title.value = "广场"
                    showTitleIcon.value = false
                }

                override fun onBackClick() {
                    navController.popBackStack()
                }
            }

            search = object : AutoSearch(viewLifecycleOwner, viewModel.searchStr.value) {
                override fun searchApi(searchStr: String) {
                    viewModel.showUserArctiles(searchStr)
                }
            }

            click0s = mapOf(
                "onRefresh" to viewModel::refresh,
                "onLoadMore" to viewModel::loadMore
            )

            click1s = mapOf(
                "onItemClick" to
                        fun(postion: Int) {
                            val item = squareAdapter.getItem(postion)
                            item?.let {
                                navController.navigateAnimate(
                                    SquareListFragmentDirections.actionSquareListFragmentToWebViewFragment(
                                        it.link
                                    )
                                )
                            }
                        }
            )

            adapter = squareAdapter
            setupRefreshLayout(srlLayout, recyclerView)
        }

        setUpObserver(squareAdapter)
    }

    private fun setUpObserver(adapter: SquareListAdapter) {
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
}

class SqueareListModel @Inject constructor(
    rep: SqueareListRep
) : ViewModel() {
    private val _searchStr = MutableLiveData<String>()
    val searchStr: LiveData<String>
        get() = _searchStr

    private val listPageing = _searchStr.map {
        rep.getUserArtclesByPage(it)
    }

    /**
     * 列表
     */
    val articleList = listPageing.switchMap {
        it.list.asLiveData()
    }


    /**
     * 加载状态
     */
    val loadState = listPageing.switchMap {
        it.loadStatus.asLiveData()
    }

    /**
     * 刷新状态
     */
    val refreshState = listPageing.switchMap {
        it.refreshStatus.asLiveData()
    }

    /**
     * 刷新方法
     */
    fun refresh() {
        listPageing.value?.refresh?.invoke()
    }

    /**
     * 加载更多方法
     */
    fun loadMore() {
        listPageing.value?.loadData?.invoke()
    }

    fun showUserArctiles(str: String): Boolean {
        if (_searchStr.value == str) {
            return false
        }
        _searchStr.value = str
        return true
    }
}

@Singleton
class SqueareListRep @Inject constructor(
    private val service: GbdService,
    private val dao: UserArticleDao
) : BaseRepository() {

    fun getUserArtclesByPage(search: String) =
        loadFlowDataByPage(
            {
                dao.queryBySearchStr(search)
            },
            {
                it
            },
            {
                if (search.isEmpty()) {
                    service.getUserArticle(it!!)
                } else {
                    //只做本地搜索
                    null
                }
            },
            { list, isrefresh ->
                if (isrefresh) {
                    dao.deleteAll()
                }

                if (!list.isNullOrEmpty()) {
                    val sum = dao.queryDataSum()

                    dao.insertAll(
                        list.mapIndexed { index, item ->
                            item._order_ = sum + index
                            item
                        }
                    )
                }
            },
            {
                it?.data?.datas
            }
        )
}