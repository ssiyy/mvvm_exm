package com.siy.mvvm.exm.base

import android.app.ActivityManager
import android.content.Context
import android.os.Looper
import android.os.Process
import android.widget.Toast
import com.siy.mvvm.exm.utils.Preference
import com.siy.mvvm.exm.utils.detailMsg
import com.siy.mvvm.exm.utils.pref
import timber.log.Timber
import java.util.*
import kotlin.concurrent.thread

/**
 * 全局的异常捕捉类
 *
 *
 * created by Siy on 2019年7月30日10:31:19
 * @author Siy
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()

     var carshDetailMsg by pref("", Preference.MODE.STROGE_FILE)

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (!handlerUncaughtException(e)) {
            //如果没有自己处理异常，则交给系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(t, e)
        } else {
            try {
                Thread.sleep(2000)
                killAllProcess()
            } catch (e: Exception) {
            }
        }
    }

    private fun killAllProcess() {
        val actManager = MvvmApplication.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (info in actManager.runningAppProcesses) {
            if (info.processName.startsWith(MvvmApplication.instance.packageName)) {
                Process.killProcess(info.pid)
            }
        }

    }

    private fun handlerUncaughtException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        thread {
            Looper.prepare()
            Toast.makeText(MvvmApplication.instance, "对不起，程序异常崩溃", Toast.LENGTH_SHORT).show()
            Looper.loop()
        }
        saveException(ex)
        return true
    }

    private fun saveException(ex: Throwable?) {
        Timber.e(ex?.detailMsg)
        carshDetailMsg = "${Date().time}   \n${ex?.detailMsg
            ?: ""}"
    }

    fun clearException() {
        carshDetailMsg = ""
    }
}