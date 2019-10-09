package com.siy.mvvm.exm.base

import com.siy.mvvm.exm.ui.login.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

   @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    /*  @ContributesAndroidInjector
     abstract fun contributeFaceRecFragment(): FaceRecFragment

     @ContributesAndroidInjector
     abstract fun contributeAccountabilityFragment(): AccountabilityFragment

     @ContributesAndroidInjector
     abstract fun contributeWorkItemFragment(): WorkItemListFragment

     @ContributesAndroidInjector
     abstract fun contributeWorkItemAddFragment(): WorkItemAddFragment*/
}