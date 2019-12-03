package com.siy.mvvm.exm.base.repository

import com.siy.mvvm.exm.http.*
import com.siy.mvvm.exm.utils.detailMsg
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * 这里扩充BaseRepository方法，使他符合https://medium.com/androiddevelopers/lessons-learnt-using-coroutines-flow-4a6b285c0d06
 * 所说的
 */

fun <NeedResultType, NetResultType> BaseRepository.loadFlowDataNoCache(
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
) = flow {
    emit(
        if (isNetAvailable) {
            //如果有网络
            val result = fetchNet()
            val needResult = net2NeedResultTypeConvert(result)
            if (isBusinessSuccess(result)) {
                //判断业务请求是否成功
                if (needResult != null) {
                    Resource.success(needResult)
                } else {
                    Resource.error("result is null", null)
                }
            } else {
                //业务请求不成功，提取服务器那边返回的错误
                Resource.error(
                    if (result is BaseBean<*>) result.errorMsg
                        ?: "未知错误" else "未知错误", needResult
                )
            }
        } else {
            //没有网络
            Resource.nonnetwork("网络连接不可用", null)
        }
    )
}.onStart {
    emit(Resource.loading(null))
}.catch { e ->
    Timber.e(e.detailMsg)
    emit(Resource.error(e.message ?: "unknow", null))
}

fun <DbResultType, NetResultType> BaseRepository.loadFlowData(
    loadFromDb: () -> Flow<DbResultType>,
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

) = flow<Resource<DbResultType>> {
    val dbValue = loadFromDb().first()
    if (shouldFetch(dbValue)) {
        emit(Resource.loading(dbValue))
        if (isNetAvailable) {
            val netResponse = fetchNet()
            if (isBusinessSuccess(netResponse)) {
                val netResult = net2DbResultConvert(netResponse)
                insertDb(netResult)
                emitAll(
                    loadFromDb().map {
                        Resource.success(it)
                    })
            } else {
                fetchFaile()
                emitAll(
                    loadFromDb().map {
                        Resource.error(
                            if (netResponse is BaseBean<*>) netResponse.errorMsg
                                ?: "未知错误" else "未知错误", it
                        )
                    })
            }
        } else {
            fetchFaile()
            emitAll(
                loadFromDb().map {
                    Resource.nonnetwork("网络连接不可用", it)
                }
            )
        }
    } else {
        emitAll(
            loadFromDb().map {
                Resource.success(it)
            }
        )
    }
}.onStart {
    emit(Resource.loading(null))
}.catch { e ->
    Timber.e(e.detailMsg)
    emit(Resource.error(e.message ?: "unknow", null))
}


data class ListPageing<T>(
    val list: Flow<T>,
    val refresh: () -> Unit,
    val loadData: () -> Unit,
    val loadStatus: Flow<PageRes>,
    val refreshStatus: Flow<PageRes>
)

@ExperimentalCoroutinesApi
fun <DbResultType, NetResultType, ReqNetParam> BaseRepository.loadFlowDataByPage(
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
            loadFlowData(
                loadFromDb,
                { fetchNet(reqNetParam) }
                ,
                {
                    performPage(it)
                    insertDb(it, isRefresh)
                }, net2DbResultConvert
            )
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
            it
        }.filter {
            it.status in listOf(Status.NONNETWORK, Status.ERROR, Status.SUCCESS)
        }.map {
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