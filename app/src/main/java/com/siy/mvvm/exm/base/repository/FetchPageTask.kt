package com.siy.mvvm.exm.base.repository

import androidx.lifecycle.*
import com.siy.mvvm.exm.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

typealias PageIndex = Int
typealias IsRefresh = Boolean

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

    //页面控制，当pageLiveData的value值发生改变时就请求网络数据
    val pageLiveData = MutableLiveData<Int>()
    val orgDataLiveData = pageLiveData.map(createReqNetParamByPage)
        .switchMap { reqNetParam ->
            networkBoundResource(
                loadFromDb = loadFromDb,
                fetch = { fetchNet(reqNetParam) },
                saveCallResult = {
                    performPage(it)
                    insertDb(it, isRefresh)
                },
                processResponse = net2DbResultConvert
            )
        }

    val listLiveData = MediatorLiveData<DbResultType?>()
    listLiveData.addSource(orgDataLiveData.switchMap {
        liveData {
            if (it.status == Status.LOADING && it.data == null) {
                //当loading的data为null时去数据找一下
                val dbValue = loadFromDb().await()
                Resource.create(it.status, dbValue, it.message)
            } else {
                it
            }.let {
                emit(it)
            }
        }
    }) {
        when (it.status) {
            Status.LOADING -> {
                listLiveData.value = it.data
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

@ExperimentalCoroutinesApi
fun <DbResultType, NetResultType, ReqNetParam> loadFlowDataByPage(
    loadFromDb: () -> Flow<DbResultType>,
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
): ListPageing<DbResultType?> {
    //当前请求页面
    var page = initPageIndex
    //当前是什么操作，默认是刷新
    var isRefresh = true

    //加载更多的状态,用来标识是否还可以加载更多，只有END才不可以加载更多
    val loadStatus = ConflatedBroadcastChannel<PageRes>()
    var loadMore: PAGESTATUS? = null
    //刷新的状态
    val refreshStatus = ConflatedBroadcastChannel<PageRes>()

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

    val sendData: suspend (ConflatedBroadcastChannel<PageRes>, PageRes) -> Unit =
        { channel, resource ->
            if (!channel.isClosedForSend) {
                channel.send(resource)
            }
        }

    val offerData: (ConflatedBroadcastChannel<Int>, Int) -> Unit =
        { channel, resource ->
            if (!channel.isClosedForSend) {
                channel.offer(resource)
            }
        }

    val pageChannel = ConflatedBroadcastChannel<Int>()
    val listFlow = pageChannel.asFlow()
        .map { pageIndex ->
            createReqNetParamByPage(pageIndex)
        }.flatMapLatest { reqNetParam ->
            flowNetworkBoundResource(
                loadFromDb = loadFromDb,
                fetch = { fetchNet(reqNetParam) },
                saveCallResult = {
                    performPage(it)
                    insertDb(it, isRefresh)
                },
                processResponse = net2DbResultConvert
            )
        }.map{
            if (it.status == Status.LOADING && it.data == null) {
                //当loading的data为null时去数据找一下
                val dbValue = loadFromDb().first()
                Resource.create(it.status, dbValue, it.message)
            } else {
                it
            }
        }.map {
            when (it.status) {
                Status.LOADING -> {
                    sendData(
                        if (isRefresh) {
                            refreshStatus
                        } else {
                            loadStatus
                        }, PageRes.loading(null)
                    )
                }
                Status.NONNETWORK, Status.ERROR -> {
                    if (isRefresh) {
                        sendData(refreshStatus, PageRes.error(it.message))
                    }

                    //无论是刷新还是加载更多，都需要知道是否可以加载更多
                    if (loadMore != null) {
                        sendData(loadStatus, PageRes.create(loadMore!!, it.message))
                    } else {
                        sendData(loadStatus, PageRes.error(it.message))
                    }
                }
                Status.SUCCESS -> {
                    if (isRefresh) {
                        sendData(refreshStatus, PageRes.complete(null))
                    }

                    //无论是刷新还是加载更多，都需要知道是否可以加载更多
                    if (loadMore != null) {
                        sendData(loadStatus, PageRes.create(loadMore!!, it.message))
                    } else {
                        sendData(loadStatus, PageRes.complete(it.message))
                    }
                }
            }
            it.data
        }

    val refreshLamda = {
        isRefresh = true
        page = initPageIndex
        offerData(pageChannel, page)
    }

    if (firstRefresh) {
        refreshLamda()
    }

    return ListPageing(
        list = listFlow,
        refresh = refreshLamda,
        loadData = {
            isRefresh = false
            loadMore = null
            offerData(pageChannel, page)
        },
        refreshStatus = refreshStatus.asFlow(),
        loadStatus = loadStatus.asFlow()
    )
}