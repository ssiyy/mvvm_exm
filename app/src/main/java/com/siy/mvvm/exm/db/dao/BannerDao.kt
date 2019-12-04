package com.siy.mvvm.exm.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siy.mvvm.exm.ui.Banner
import kotlinx.coroutines.flow.Flow


/**
 * Created by Siy on 2019/10/10.
 *
 * @author Siy
 */
@Dao
interface BannerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(banners: List<Banner>)

    @Query("delete from banners")
    fun deleteAll()

    @Query("select * from banners")
    fun queryAll(): Flow<List<Banner>>
}