package com.siy.mvvm.exm.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siy.mvvm.exm.ui.Article


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(workItems: List<Article>)

    @Query("select * from articles")
    fun queryAll(): LiveData<List<Article>>

    @Query("delete from articles")
    fun deleteAll()
}