package com.siy.mvvm.exm.http

import com.siy.mvvm.exm.ui.Article
import com.siy.mvvm.exm.ui.Banner
import com.siy.mvvm.exm.ui.DataList
import com.siy.mvvm.exm.ui.UserArticle
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

    /**
     * 首页banner
     */
    @GET("/banner/json")
    suspend fun getBanner(): BaseBean<List<Banner>>

    /**
     * 首页列表
     */
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticles(@Path("page") page: Int): BaseBean<DataList<Article>>

    /**
     * 广场列表
     */
    @GET("user_article/list/{page}/json")
    suspend fun getUserArticle(@Path("page") page:Int):BaseBean<DataList<UserArticle>>

}