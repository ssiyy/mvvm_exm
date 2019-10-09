package com.siy.mvvm.exm.http


/**
 * Created by Siy on 2019/08/21.
 *
 * @author Siy
 */
data class SearReq(
    var page: Int,
    var pageSize: Int,
    var searchStr: String? = null,
    var isRefresh: Boolean
)