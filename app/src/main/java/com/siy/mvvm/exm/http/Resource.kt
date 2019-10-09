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

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
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

data class PageRes(val status: PAGESTATUS, val message: String?) {
    companion object {
        fun  create(status: PAGESTATUS, msg: String?) = PageRes(status, msg)

        fun  loading( msg: String?) = PageRes(PAGESTATUS.LOADING, msg)
        fun  complete( msg: String?) = PageRes(PAGESTATUS.COMPLETE, msg)
        fun  end( msg: String?) = PageRes(PAGESTATUS.END, msg)
        fun  error( msg: String?) = PageRes(PAGESTATUS.ERROR, msg)
    }
}


data class Listing<T>(
    val list: LiveData<T>,
    val refresh: () -> Unit,
    val loadData: () -> Unit,
    val loadStatus: LiveData<PageRes>,
    val refreshStatus: LiveData<PageRes>
)


data class PageResource<out T>(val isRefresh: Boolean, val status: Status, val data: T?, val message: String?) {
    private val resource: Resource<T> = Resource(status, data, message)

    companion object {
        fun <T> success(isRefresh: Boolean, data: T?): PageResource<T> {
            return PageResource(isRefresh, Status.SUCCESS, data, null)
        }


        fun <T> error(isRefresh: Boolean, msg: String, data: T?): PageResource<T> {
            return PageResource(isRefresh, Status.ERROR, data, msg)
        }

        fun <T> nonnetwork(isRefresh: Boolean, msg: String, data: T?): PageResource<T> {
            return PageResource(isRefresh, Status.NONNETWORK, data, msg)
        }

        fun <T> loading(isRefresh: Boolean, data: T?): PageResource<T> {
            return PageResource(isRefresh, Status.LOADING, data, null)
        }


        fun <T> create(isRefresh: Boolean, resource: Resource<T>): PageResource<T> {
            return PageResource(isRefresh, resource.status, resource.data, resource.message)
        }
    }

}


