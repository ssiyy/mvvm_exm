package com.siy.mvvm.exm.base

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        MainActivityModule::class,
        AppModule::class
    ]
)
interface AppCompoent : AndroidInjector<MvvmApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MvvmApplication): Builder

        fun build(): AppCompoent
    }
}