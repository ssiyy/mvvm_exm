package com.siy.mvvm.exm.http

import com.google.gson.annotations.SerializedName


/**
 * Created by Siy on 2019/07/12.
 *
 * @author Siy
 */
data class BaseBean<T>(
    @field:SerializedName("errorCode")
    val errorCode: Int,
    @field:SerializedName("errorMsg")
    val errorMsg: String?,
    @field:SerializedName("data")
    val data: T?
) {


    /**
     * 判断服务器的业务请求是否成功
     *
     * @return
     */
    fun isSuccess() = errorCode == 0
}


data class ResultRsp<T>(@field:SerializedName("result") val rsp: List<T>)