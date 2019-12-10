/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.siy.mvvm.exm.base.repository

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.siy.mvvm.exm.base.MvvmApplication
import com.siy.mvvm.exm.http.BaseBean
import com.siy.mvvm.exm.http.Resource
import com.siy.mvvm.exm.http.Status
import com.siy.mvvm.exm.utils.netAvailable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException


fun <DbResultType, NetResultType> networkBoundResource(
    loadFromDb: () -> LiveData<DbResultType>,
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
): LiveData<Resource<DbResultType>> {
    return CoroutineNetworkBoundResource(
        saveCallResult = saveCallResult,
        shouldFetch = shouldFetch,
        loadFromDb = loadFromDb,
        fetch = fetch,
        processResponse = processResponse,
        onFetchFailed = onFetchFailed,
        isBusinessSuccess = isBusinessSuccess
    ).asLiveData().distinctUntilChanged() // not super happy about this as the data might be BIG
}

/**
 * A [NetworkBoundResource] implementation in corotuines
 */
private class CoroutineNetworkBoundResource<ResultType, RequestType> @MainThread constructor(
    private val saveCallResult: suspend (ResultType?) -> Unit,
    private val shouldFetch: (ResultType) -> Boolean = { true },
    private val loadFromDb: () -> LiveData<ResultType>,
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


    private val result = liveData<Resource<ResultType>> {
        if (latestValue?.status != Status.SUCCESS) {
            emit(Resource.loading(latestValue?.data))
        }
        val dbSource = loadFromDb()
        val initialValue = dbSource.await()
        val willFetch = initialValue == null || shouldFetch(initialValue)
        if (!willFetch) {
            // if we won't fetch, just emit existing db values as success
            emitSource(dbSource.map {
                Resource.success(it)
            })
        } else {
            doFetch(dbSource, this)
        }
    }

    private suspend fun doFetch(
        dbSource: LiveData<ResultType>,
        liveDataScope: LiveDataScope<Resource<ResultType>>
    ) {
        if (isNetAvailable) {
            // emit existing values as loading while we fetch
            val initialSource = liveDataScope.emitSource(dbSource.map {
                Resource.loading(it)
            })
            try {
                val response = fetch()
                if (isBusinessSuccess(response)) {
                    //如果业务上是成功的
                    val dbResult = processResponse(response)
                    initialSource.dispose()
                    saveCallResult(dbResult)
                    liveDataScope.emitSource(dbSource.map {
                        Resource.success(it)
                    })
                } else {
                    //业务上失败的
                    onFetchFailed?.invoke(response)
                    liveDataScope.emitSource(dbSource.map {
                        Resource.error(
                            if (response is BaseBean<*>) response.errorMsg
                                ?: "unknow error" else "unknow error", it
                        )
                    })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                onFetchFailed?.invoke(null)
                liveDataScope.emitSource(dbSource.map {
                    Resource.error(e.message ?: "unknow error", it)
                })
            }
        } else {
            //没有网络
            onFetchFailed?.invoke(null)
            liveDataScope.emitSource(dbSource.map {
                Resource.nonnetwork("Network connection is unavailable", it)
            })
        }
    }

    fun asLiveData() = result
}

suspend fun <T> LiveData<T>.await() = withContext(Dispatchers.Main) {
    val receivedValue = CompletableDeferred<T?>()
    val observer = Observer<T> {
        if (receivedValue.isActive) {
            receivedValue.complete(it)
        }
    }
    try {
        observeForever(observer)
        return@withContext receivedValue.await()
    } finally {
        removeObserver(observer)
    }
}