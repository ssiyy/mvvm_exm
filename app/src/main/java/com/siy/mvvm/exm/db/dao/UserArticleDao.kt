package com.siy.mvvm.exm.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siy.mvvm.exm.ui.UserArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface UserArticleDao {

    @Query("select count(1) from user_article")
    fun queryDataSum(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userArticles: List<UserArticle>)

    @Query(
        """
        select * from ( select 
        t.chapterName||t.superChapterName||t.author||t .title||t.niceDate||t.niceShareDate as unioncontent ,t.*  from  user_article t)
        where unioncontent like '%'||:searchStr||'%' order by _order_ asc
    """
    )
    fun queryBySearchStr(searchStr: String): Flow<List<UserArticle>>


    @Query("select * from user_article")
    fun queryAll(): Flow<List<UserArticle>>

    @Query("delete from user_article")
    fun deleteAll()

    @Query("delete from user_article where id = :id")
    fun deleteById(id:Int)
}