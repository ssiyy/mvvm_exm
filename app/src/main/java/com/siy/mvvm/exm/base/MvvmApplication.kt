package com.siy.mvvm.exm.base

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import com.siy.mvvm.exm.BuildConfig
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
class MvvmApplication : Application(), HasActivityInjector {

    companion object {
        private var mInstance: MvvmApplication by Delegates.notNull()

        val instance: MvvmApplication
            get() = mInstance
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        mInstance = this
        initLogger()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {
            res.updateConfiguration(Configuration().apply { setToDefaults() }, res.displayMetrics)
        }
        return res
    }

    /**
     * 初始化日志框架
     *
     */
    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag("GbdApplication")
        }
    }
}