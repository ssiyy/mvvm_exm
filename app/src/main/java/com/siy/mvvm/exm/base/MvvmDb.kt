package com.siy.mvvm.exm.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siy.mvvm.exm.db.dao.ArticleDao
import com.siy.mvvm.exm.db.dao.BannerDao
import com.siy.mvvm.exm.db.dao.UserArticleDao
import com.siy.mvvm.exm.ui.Article
import com.siy.mvvm.exm.ui.Banner
import com.siy.mvvm.exm.ui.UserArticle


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Database(
    entities = [
        Banner::class,
        Article::class,
        UserArticle::class
    ], version = 1
)
abstract class MvvmDb : RoomDatabase() {
    abstract fun bannerDao(): BannerDao
    abstract fun articleDao(): ArticleDao
    abstract fun userArticleDao(): UserArticleDao
}