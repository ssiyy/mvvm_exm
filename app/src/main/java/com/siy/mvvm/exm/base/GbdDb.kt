package com.siy.mvvm.exm.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siy.mvvm.exm.db.dao.ArticleDao
import com.siy.mvvm.exm.db.dao.BannerDao
import com.siy.mvvm.exm.ui.Article
import com.siy.mvvm.exm.ui.Banner


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Database(
    entities = [
        Banner::class,
        Article::class], version = 1
)
abstract class GbdDb : RoomDatabase() {

    abstract fun bannerDao(): BannerDao
    abstract fun articleDao(): ArticleDao
}