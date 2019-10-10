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

    @Query("select count(1) from articles")
    fun queryDataSum(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(workItems: List<Article>)

    @Query(
        """
        select * from ( select 
        t.chapterName||t.superChapterName||t.author||t.niceDate||t.publishTime||t.title||t.type as unioncontent ,t.*  from  articles t)
        where unioncontent like '%'||:searchStr||'%' order by _order_ asc
    """
    )
    fun queryBySearchStr(searchStr: String): LiveData<List<Article>>

    @Query("delete from articles")
    fun deleteAll()
}