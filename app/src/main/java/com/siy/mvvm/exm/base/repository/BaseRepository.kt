package com.siy.mvvm.exm.base.repository

import androidx.lifecycle.*
import com.siy.mvvm.exm.base.GbdApplication
import com.siy.mvvm.exm.http.*
import com.siy.mvvm.exm.utils.detailMsg
import com.siy.mvvm.exm.utils.netAvailable
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

typealias PageIndex = Int
typealias IsRefresh = Boolean

/**
 * Created by Siy on 2019/07/12.
 *
 * @author Siy
 */
abstract class BaseRepository {

    /**
     * 网络连接是否可用
     */
    protected fun isNetAvailable(): Boolean {
        return GbdApplication.instance.netAvailable
    }

    /**
     * 用这个请求返回的数据，不会保存在数据库
     * @param fetchNet 访问网络的请求
     * @param net2NeedResultTypeConvert 将网络请求返回的数据转成业务需求的数据，这个有默认实现
     * @param isBusinessSuccess 业务请求成功的判断，这个有默认现实
     */
    fun <NeedResultType, NetResultType> loadDataNoCache(
        fetchNet: suspend () -> NetResultType,
        net2NeedResultTypeConvert: (NetResultType) -> NeedResultType? = {
            if (it is BaseBean<*>) {
                @Suppress("UNCHECKED_CAST")
                it.data as NeedResultType
            } else {
                null
            }
        },
        isBusinessSuccess: (NetResultType) -> Boolean = {
            if (it is BaseBean<*>) {
                it.isSuccess()
            } else {
                false
            }
        }
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            if (isNetAvailable()) {
                val result = fetchNet()
                val needResult = net2NeedResultTypeConvert(result)
                if (isBusinessSuccess(result)) {
                    if (needResult != null) {
                        emit(Resource.success(needResult))
                    } else {
                        emit(Resource.error("result is null", null))
                    }
                } else {
                    emit(
                        Resource.error(
                            if (result is BaseBean<*>) result.errorMsg
                                ?: "未知错误" else "未知错误", needResult
                        )
                    )
                }
            } else {
                emit(Resource.nonnetwork("网络连接不可用", null))
            }
        } catch (e: Exception) {
            Timber.e(e.detailMsg)
            emit(Resource.error(e.message ?: "unknow", null))
        }
    }

    /**
     * 用这个请求返回的数据，会保存在数据库
     *
     * @param loadFromDb 从数据库获取数据
     * @param fetchNet  访问网络的请求
     * @param insertDb 保存到数据库
     * @param shouldFetch 否是需要从网络获取数据
     * @param net2DbResultConvert 网络数据转换成数据库保存的数据类型
     * @param isBusinessSuccess 业务请求成功的判断，这个有默认现实
     */
    fun <DbResultType, NetResultType> loadData(
        loadFromDb: () -> LiveData<DbResultType>,
        fetchNet: suspend () -> NetResultType,
        insertDb: (DbResultType?) -> Unit,
        net2DbResultConvert: (NetResultType) -> DbResultType? = {
            if (it is BaseBean<*>) {
                @Suppress("UNCHECKED_CAST")
                it.data as DbResultType
            } else {
                null
            }
        },
        shouldFetch: (DbResultType) -> Boolean = { true },
        fetchFaile: () -> Unit = {},
        isBusinessSuccess: (NetResultType) -> Boolean = {
            if (it is BaseBean<*>) {
                it.isSuccess()
            } else {
                false
            }
        }

    ) = liveData<Resource<DbResultType>> {
        val result = MediatorLiveData<Resource<DbResultType>>()
        val setValue = { newValue: Resource<DbResultType> ->
            if (newValue != result.value) {
                result.value = newValue
            }
        }
        emitSource(result)

        setValue(Resource.loading(null))
        val dbSource = loadFromDb()
        result.addSource(dbSource) { db ->
            result.removeSource(dbSource)
            if (shouldFetch(db)) {
                if (isNetAvailable()) {
                    result.addSource(liveData {
                        try {
                            emit(fetchNet())
                        } catch (e: Exception) {
                            Timber.e(e.detailMsg)
                            fetchFaile()
                            result.addSource(dbSource) { newValue ->
                                setValue(Resource.error(e.message ?: "未知错误", newValue))
                            }
                        }
                    }) { fetchResult ->
                        try {
                            if (isBusinessSuccess(fetchResult)) {
                                val dbResult = net2DbResultConvert(fetchResult)
                                insertDb(dbResult)
                                result.addSource(loadFromDb()) { newValue ->
                                    setValue(Resource.success(newValue))
                                }
                            } else {
                                fetchFaile()
                                result.addSource(dbSource) { newValue ->
                                    setValue(
                                        Resource.error(
                                            if (fetchResult is BaseBean<*>) fetchResult.errorMsg
                                                ?: "未知错误" else "未知错误", newValue
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e.detailMsg)
                            fetchFaile()
                            result.addSource(dbSource) { newValue ->
                                setValue(Resource.error(e.message ?: "未知错误", newValue))
                            }
                        }
                    }
                } else {
                    fetchFaile()
                    result.addSource(dbSource) { newValue ->
                        setValue(Resource.nonnetwork("网络连接不可用", newValue))
                    }
                }
            } else {
                result.addSource(dbSource) { newValue ->
                    setValue(Resource.success(newValue))
                }
            }
        }
    }

    /**
     * 分页数据加载的方法
     *
     * @param loadFromDb 从数据库获取数据
     *
     * @param createReqNetParamByPage 创建网络请求的Bean
     *
     * @param fetchNet 访问网络的请求
     *
     * @param insertDb 将网络数据保存数据
     *
     * @param net2DbResultConvert  网络数据转换成数据库保存的数据类型
     *
     * @param firstRefresh 第一次调用该方法时，是否直接发送刷新列表的请求
     *
     * @param initPageIndex 服务器请求分页的初始页数，默认是0
     *
     * @return
     *
     * @see Listing
     */
    fun <DbResultType, NetResultType, ReqNetParam> loadDataByPage(
        loadFromDb: () -> LiveData<DbResultType>,
        createReqNetParamByPage: (PageIndex) -> ReqNetParam?,
        fetchNet: suspend (ReqNetParam?) -> NetResultType,
        insertDb: (DbResultType?, IsRefresh) -> Unit,
        net2DbResultConvert: (NetResultType) -> DbResultType? = {
            if (it is BaseBean<*>) {
                @Suppress("UNCHECKED_CAST")
                it.data as DbResultType
            } else {
                null
            }
        },
        firstRefresh: Boolean = true,
        initPageIndex: PageIndex = 0
    ): Listing<DbResultType?> {
        //当前请求页面
        var page = initPageIndex
        //当前是什么操作，默认是刷新
        var isRefresh = true

        //加载更多的状态,用来标识是否还可以加载更多，只有END才不可以加载更多
        val loadStatus = MediatorLiveData<PageRes>()
        var loadMore: PAGESTATUS? = null
        //刷新的状态
        val refreshStatus = MediatorLiveData<PageRes>()

        //处理一下页面相关的逻辑
        val performPage = fun(list: DbResultType?) {
            loadMore = if (list is Collection<*>) {
                if (list.isNullOrEmpty()) {
                    PAGESTATUS.END
                } else {
                    PAGESTATUS.COMPLETE
                }
            } else {
                PAGESTATUS.ERROR
            }
            if (list is Collection<*> && !list.isNullOrEmpty()) {
                page += 1
            }
        }

        val pageLiveData = MutableLiveData<Int>()
        val orgDataLiveData = pageLiveData.map(createReqNetParamByPage)
            .switchMap { reqNetParam ->
                loadData(
                    loadFromDb,
                    { fetchNet(reqNetParam) }
                    ,
                    {
                        performPage(it)
                        insertDb(it, isRefresh)
                    }, net2DbResultConvert
                )
            }

        val listLiveData = MediatorLiveData<DbResultType?>()
        listLiveData.addSource(orgDataLiveData) {
            when (it.status) {
                Status.LOADING -> {
                    if (isRefresh) {
                        refreshStatus
                    } else {
                        loadStatus
                    }.value = PageRes.loading(null)
                }
                Status.NONNETWORK, Status.ERROR -> {
                    listLiveData.value = it.data
                    if (isRefresh) {
                        refreshStatus.value = PageRes.error(it.message)
                    }

                    //无论是刷新还是加载更多，都需要知道是否可以加载更多
                    if (loadMore != null) {
                        loadStatus.value = PageRes.create(loadMore!!, it.message)
                    } else {
                        loadStatus.value = PageRes.error(it.message)
                    }

                }
                Status.SUCCESS -> {
                    listLiveData.value = it.data
                    if (isRefresh) {
                        refreshStatus.value = PageRes.complete(null)
                    }

                    //无论是刷新还是加载更多，都需要知道是否可以加载更多
                    if (loadMore != null) {
                        loadStatus.value = PageRes.create(loadMore!!, it.message)
                    } else {
                        loadStatus.value = PageRes.complete(it.message)
                    }
                }
            }
        }

        val refreshLamda = {
            isRefresh = true
            page = initPageIndex
            pageLiveData.value = page
        }

        if (firstRefresh) {
            refreshLamda()
        }

        return Listing(
            list = listLiveData,
            refresh = refreshLamda,
            loadData = {
                isRefresh = false
                loadMore = null
                pageLiveData.value = page
            },
            refreshStatus = refreshStatus,
            loadStatus = loadStatus
        )
    }
}