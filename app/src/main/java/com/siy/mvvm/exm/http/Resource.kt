/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.siy.mvvm.exm.http

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.*

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> create(status: Status, data: T?, message: String?) = Resource(status, data, message)

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> nonnetwork(msg: String, data: T?): Resource<T> {
            return Resource(Status.NONNETWORK, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

fun <T, F> Flow<Resource<T?>>.resMapFlow(transform: suspend (value: Flow<T>) -> Flow<F>) =
    flow<Resource<F?>> {
        collect { res ->
            val data = res.data
            val status = res.status
            val message = res.message

            if (data == null) {
                emit(Resource.create(status, null, message))
            } else {
                emitAll(
                    transform(
                        flow {
                            emit(data!!)
                        }
                    )
                        .map {
                            Resource.create(status, it, message)
                        }
                )
            }
        }
    }

data class PageRes(val status: PAGESTATUS, val message: String?) {
    companion object {
        fun create(status: PAGESTATUS, msg: String?) = PageRes(status, msg)

        fun loading(msg: String?) = PageRes(PAGESTATUS.LOADING, msg)
        fun complete(msg: String?) = PageRes(PAGESTATUS.COMPLETE, msg)
        fun end(msg: String?) = PageRes(PAGESTATUS.END, msg)
        fun error(msg: String?) = PageRes(PAGESTATUS.ERROR, msg)
    }
}


data class Listing<T>(
    val list: LiveData<T>,
    val refresh: () -> Unit,
    val loadData: () -> Unit,
    val loadStatus: LiveData<PageRes>,
    val refreshStatus: LiveData<PageRes>
)


data class ListPageing<T>(
    val list: Flow<T>,
    val refresh: () -> Unit,
    val loadData: () -> Unit,
    val loadStatus: Flow<PageRes>,
    val refreshStatus: Flow<PageRes>
)

