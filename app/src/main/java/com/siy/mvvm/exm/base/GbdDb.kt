package com.siy.mvvm.exm.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siy.mvvm.exm.ui.login.Test


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Database(
    entities = [
        Test::class], version = 1
)
abstract class GbdDb : RoomDatabase() {


}