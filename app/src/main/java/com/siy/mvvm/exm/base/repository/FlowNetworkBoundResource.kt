package com.siy.mvvm.exm.base.repository

import androidx.annotation.MainThread
import com.siy.mvvm.exm.base.MvvmApplication
import com.siy.mvvm.exm.http.BaseBean
import com.siy.mvvm.exm.http.Resource
import com.siy.mvvm.exm.utils.netAvailable
import kotlinx.coroutines.flow.*

fun <DbResultType, NetResultType> flowNetworkBoundResource(
    loadFromDb: () -> Flow<DbResultType>,
    fetch: suspend () -> NetResultType,
    saveCallResult: suspend (DbResultType?) -> Unit,
    processResponse: (NetResultType) -> DbResultType? = {
        if (it is BaseBean<*>) {
            @Suppress("UNCHECKED_CAST")
            it.data as DbResultType
        } else {
            null
        }
    },
    shouldFetch: (DbResultType) -> Boolean = { true },
    onFetchFailed: ((NetResultType?) -> Unit)? = null,
    isBusinessSuccess: (NetResultType) -> Boolean = {
        if (it is BaseBean<*>) {
            it.isSuccess()
        } else {
            false
        }
    }
): Flow<Resource<DbResultType>> {
    return FlowNetworkBoundResource(
        saveCallResult = saveCallResult,
        shouldFetch = shouldFetch,
        loadFromDb = loadFromDb,
        fetch = fetch,
        processResponse = processResponse,
        onFetchFailed = onFetchFailed,
        isBusinessSuccess = isBusinessSuccess
    ).asFlowData().distinctUntilChanged() // not super happy about this as the data might be BIG
}


private class FlowNetworkBoundResource<ResultType, RequestType> @MainThread constructor(
    private val saveCallResult: suspend (ResultType?) -> Unit,
    private val shouldFetch: (ResultType) -> Boolean = { true },
    private val loadFromDb: () -> Flow<ResultType>,
    private val fetch: suspend () -> RequestType,
    private val processResponse: (RequestType) -> ResultType? = {
        if (it is BaseBean<*>) {
            @Suppress("UNCHECKED_CAST")
            it.data as ResultType
        } else {
            null
        }
    },
    private val onFetchFailed: ((RequestType?) -> Unit)? = null,
    private val isBusinessSuccess: (RequestType) -> Boolean = {
        if (it is BaseBean<*>) {
            it.isSuccess()
        } else {
            false
        }
    }
) {

    /**
     * 网络连接是否可用
     */
    private val isNetAvailable: Boolean
        get() = MvvmApplication.instance.netAvailable

    private val result = flow<Resource<ResultType>> {
        val dbSource = loadFromDb()
        val initialValue = dbSource.first()
        if (!shouldFetch(initialValue)) {
            // if we won't fetch, just emit existing db values as success
            emitAll(
                loadFromDb().map {
                    Resource.success(it)
                })
        } else {
            doFetch(dbSource, this)
        }
    }.onStart {
        emit(Resource.loading(null))
    }.catch { e ->
        emitAll(
            loadFromDb().map {
                Resource.error(e.message ?: "unknow", it)
            }
        )
    }

    private suspend fun doFetch(
        dbSource: Flow<ResultType>,
        flowCollector: FlowCollector<Resource<ResultType>>
    ) {
        if (isNetAvailable) {
            flowCollector.emit(Resource.loading(dbSource.first()))
            val netResponse = fetch()
            if (isBusinessSuccess(netResponse)) {
                val netResult = processResponse(netResponse)
                saveCallResult(netResult)
                flowCollector.emitAll(dbSource.map {
                    Resource.success(it)
                })
            } else {
                onFetchFailed?.invoke(netResponse)
                flowCollector.emitAll(dbSource.map {
                    Resource.error(
                        if (netResponse is BaseBean<*>) netResponse.errorMsg
                            ?: "unknow error" else "unknow error", it
                    )
                })
            }
        } else {
            onFetchFailed?.invoke(null)
            flowCollector.emitAll(
                loadFromDb().map {
                    Resource.nonnetwork("Network connection is unavailable", it)
                }
            )
        }
    }

    fun asFlowData() = result
}