package com.siy.mvvm.exm.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */


//api 接口来自于 https://wanandroid.com/blog/show/2

/*
"desc:String?//"Android高级进阶直播课免费学习",
"id:String?//23,
"imagePath:String?//"https://wanandroid.com/blogimgs/92d96db5-d951-4223-ac42-e13a62899f50.jpeg",
"isVisible:String?//1,
"order:String?//0,
"title:String?//"Android高级进阶直播课免费学习",
"type:String?//0,
"url:String?//"https://url.163.com/4bj"
}*/
@Entity(tableName = "banners")
data class Banner(
    @PrimaryKey
    val id: Int,
    val desc: String,

    @ColumnInfo(name = "image_path")
    val imagePath: String,

    @ColumnInfo(name = "is_visible")
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val id: Int,
    val originId: Int?,
    val title: String,
    val chapterId: Int?,
    val chapterName: String?,
    val envelopePic: String?,
    val link: String,
    val author: String?,
    val origin: String?,
    val publishTime: Long?,
    val zan: Int?,
    val desc: String?,
    val visible: Int?,
    val niceDate: String?,
    val courseId: Int?,
    var collect: Boolean?,
    val apkLink: String?,
    val projectLink: String?,
    val superChapterId: Int?,
    val superChapterName: String?,
    val type: Int?,
    val fresh: Boolean?,
    /**
     * 用于存储服务器返回的顺序
     */
    var _order_: Int
)

@Entity(tableName = "user_article")
data class UserArticle(
    @PrimaryKey
    val id: Int,//10626,
    val apkLink: String?,//"",
    val audit: String?,//1,
    val author: String?,//"",
    val chapterId: String?,//494,
    val chapterName: String?,//"广场",
    val collect: String?,//false,
    val courseId: String?,//13,
    val desc: String?,//"",
    val envelopePic: String?,//"",
    val fresh: Boolean,//true,
    val link: String,//"https:,//www.jianshu.com/p/494246f9ae51",
    val niceDate: String?,//"23分钟前",
    val niceShareDate: String?,//"23分钟前",
    val origin: String?,//"",
    val prefix: String?,//"",
    val projectLink: String?,//"",
    val publishTime: String?,//1575273356000,
    val selfVisible: String?,//0,
    val shareDate: String?,//1575273356000,
    val shareUser: String?,//"吊儿郎当",
    val superChapterId: String?,//494,
    val superChapterName: String?,//"广场Tab",
    val title: String?,//"懂LruCache？你必须先懂LinkedHashMap，顺带给LruCache提个建议",
    val type: String?,//0,
    val userId: String?,//2554,
    val visible: String?,//0,
    val zan: String?,//0

    /**
     * 用于存储服务器返回的顺序
     */
    var _order_: Int
)


data class DataList<T>(
    val offset: Int,
    val size: Int,
    val total: Int,
    val pageCount: Int,
    val curPage: Int,
    val over: Boolean,
    val datas: List<T>
)