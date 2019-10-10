package com.siy.mvvm.exm.db

import androidx.room.Room
import androidx.room.RoomDatabase
import com.siy.mvvm.exm.base.GbdApplication
import com.siy.mvvm.exm.base.GbdDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Siy on 2019/08/22.
 *
 * @author Siy
 */
@Module
class DbModule {
    @Singleton
    @Provides
    fun provideDb(application: GbdApplication): GbdDb {
        return Room
            .databaseBuilder(application, GbdDb::class.java, "mvvm.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
            .build()
    }

    @Singleton
    @Provides
    fun bannerDao(db: GbdDb) = db.bannerDao()

    @Singleton
    @Provides
    fun articleDao(db: GbdDb) = db.articleDao()

}