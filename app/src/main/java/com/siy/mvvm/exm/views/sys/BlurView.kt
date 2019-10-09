package com.siy.mvvm.exm.views.sys

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.siy.mvvm.exm.utils.GDB_ERROR
import com.siy.mvvm.exm.utils.autoDisposable
import com.siy.mvvm.exm.utils.takeScreenShot
import com.siy.mvvm.exm.utils.toBlur
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by Siy on 2019/4/23.
 *
 * @author Siy
 */
class BlurView(context: Context) : AppCompatImageView(context) {

    private var tag = 0

    fun show() {
        if (tag++ <= 0) {
            animate().alpha(1f).setDuration(300).start()
        }
    }

    fun hide() {
        if (--tag <= 0) {
            animate().alpha(0f).setDuration(300).start()
        }
    }

    fun blur() {
        val activity = context as FragmentActivity
        if (tag <= 0) {
            Observable.just(activity)
                .map { activity1 ->
                    val bitmap = activity1.takeScreenShot()
                    bitmap.toBlur(activity1,  4.0f, 0.2f)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .`as`(activity.autoDisposable())
                .subscribe(Consumer{ bitmap -> background = BitmapDrawable(resources, bitmap) },
                    GDB_ERROR
                )
            tag = 0
        }
    }

}
