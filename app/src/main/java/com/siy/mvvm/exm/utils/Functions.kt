@file:JvmName("GbdUtils")
@file:JvmMultifileClass

package com.siy.mvvm.exm.utils

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.siy.mvvm.exm.views.ScrollChildSwipeRefreshLayout
import com.siy.mvvm.exm.R
import io.reactivex.functions.Consumer
import timber.log.Timber


/**
 * Created by Siy on 2019/08/12.
 *
 * @author Siy
 */


/**
 * 设置SwipeRefreshLayout
 */
fun setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        Color.parseColor("#4cd964"), Color.parseColor("#4cd964"),
        Color.parseColor("#4cd964")
    )

    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}

val GDB_ERROR = Consumer<Throwable> { Timber.e(it.detailMsg) }

fun <T> prefGbd(default: T, mode: Preference.MODE = Preference.MODE.STROGE_SP) = Preference(default, mode = mode)

fun getEmptyPromptView(context: Context, promptMsg: String): View {
    val view = LayoutInflater.from(context).inflate(R.layout.feed_empty_view, null)
    val textView = view.findViewById<TextView>(R.id.tv_prompt)
    textView.text = promptMsg
    return view
}

/**
 * 是否有外置存储
 */
fun hasSdCard() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

/**
 * 将page转换成sql的index
 */
fun pageToSqlIndex(pageIndex: Int, pageSize: Int): Int {
    //sql的index是从0开始的
    val index = (pageIndex - 1) * pageSize
    return if (index > 0) index else 0
}


infix fun <P1, P2, R> Function1<P1, P2>.andThen(function: Function1<P2, R>): Function1<P1, R> {
    return fun(p1: P1): R {
        return function.invoke(this.invoke(p1))
    }
}

fun <P1, P2, R> Function2<P1, P2, R>.curried() = fun(p1: P1) = fun(p2: P2) = this(p1, p2)