package com.siy.mvvm.exm.http

import com.siy.mvvm.exm.ui.ArticleList
import com.siy.mvvm.exm.ui.Banner
import retrofit2.http.GET
import retrofit2.http.Path


/**
 *
 * https://www.wanandroid.com/blog/show/2
 *
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
interface GbdService {

    companion object {
        const val URL = "https://www.wanandroid.com"
    }



    @GET("/banner/json")
    suspend fun getBanner(): BaseBean<List<Banner>>

    @GET("/article/list/{page}/json")
    suspend fun getHomeArticles(@Path("page") page: Int): BaseBean<ArticleList>

}