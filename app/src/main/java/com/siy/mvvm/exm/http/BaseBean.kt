package com.siy.mvvm.exm.http

import android.text.TextUtils
import com.google.gson.annotations.SerializedName


/**
 * Created by Siy on 2019/07/12.
 *
 * @author Siy
 */
data class BaseBean<T>(
    @field:SerializedName("status")
    val statues: String,
    @field:SerializedName("msg")
    val msg: String?,
    @field:SerializedName("data")
    val data: T?
) {

    companion object {
        const val STATUS_SUCCESS: String = "success"
    }

    /**
     * 判断服务器的业务请求是否成功
     * status
     * 成功：success
     * 失败：failed
     *
     * @return
     */
    fun isSuccess() = TextUtils.equals(statues, STATUS_SUCCESS)
}


data class ResultRsp<T>(@field:SerializedName("result") val rsp: List<T>)