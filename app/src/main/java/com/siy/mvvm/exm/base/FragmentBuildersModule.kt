package com.siy.mvvm.exm.base

import com.siy.mvvm.exm.ui.login.LoginFragment
import com.siy.mvvm.exm.ui.main.firstpage.FirstPageFragment
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

    @ContributesAndroidInjector
    abstract fun contributeFirstPageFragment(): FirstPageFragment
}