package com.siy.mvvm.exm.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */


/*
"desc":"Android高级进阶直播课免费学习",
"id":23,
"imagePath":"https://wanandroid.com/blogimgs/92d96db5-d951-4223-ac42-e13a62899f50.jpeg",
"isVisible":1,
"order":0,
"title":"Android高级进阶直播课免费学习",
"type":0,
"url":"https://url.163.com/4bj"
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
    var _order_:Int
)

data class ArticleList(
    val offset: Int,
    val size: Int,
    val total: Int,
    val pageCount: Int,
    val curPage: Int,
    val over: Boolean,
    val datas: List<Article>
)