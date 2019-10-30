package com.siy.mvvm.exm.db

import androidx.room.Room
import androidx.room.RoomDatabase
import com.siy.mvvm.exm.base.MvvmApplication
import com.siy.mvvm.exm.base.MvvmDb
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
    fun provideDb(application: MvvmApplication): MvvmDb {
        return Room
            .databaseBuilder(application, MvvmDb::class.java, "mvvm.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
            .build()
    }

    @Singleton
    @Provides
    fun bannerDao(db: MvvmDb) = db.bannerDao()

    @Singleton
    @Provides
    fun articleDao(db: MvvmDb) = db.articleDao()

}