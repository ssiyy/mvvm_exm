@file:JvmName("GbdUtils")
@file:JvmMultifileClass

package com.siy.mvvm.exm.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.siy.mvvm.exm.views.sys.SystemDialog
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */

fun <T> Flow<T>.throttleFist(windowDuration: Long): Flow<T> = flow {
    var windowStartTime = System.currentTimeMillis()
    var emitted = false
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= windowDuration) {
            windowStartTime += delta / windowDuration * windowDuration
            emitted = false
        }
        if (!emitted) {
            emit(value)
            emitted = true
        }
    }
}


inline fun <reified R, T> R.pref(default: T, mode: Preference.MODE = Preference.MODE.STROGE_SP) =
    Preference(default, R::class.java.name, mode)

/**
 * 截屏
 */
fun Activity.takeScreenShot(): Bitmap {
    val decorView = window.decorView
    val bitmap = Bitmap.createBitmap(decorView.width, decorView.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    decorView.draw(canvas)
    return bitmap
}

/**
 * RxJava自动释放
 */
fun <T> LifecycleOwner.autoDisposable(event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY): AutoDisposeConverter<T> =
    AutoDispose.autoDisposable<T>(
        AndroidLifecycleScopeProvider.from(this, event)
    )

/**
 * dip转px
 * @param dipValue 需要转的dip
 */
fun Context.dip2px(dipValue: Float) =
    (TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dipValue,
        resources.displayMetrics
    ) + 0.5f).toInt()

fun Fragment.dip2px(dipValue: Float) = requireActivity().dip2px(dipValue)

fun Context.checkPermissions(vararg permissions: String): Boolean {
    val checkPermission =
        fun(permission: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    for (permission in permissions) {
        if (!checkPermission(permission)) {
            return false
        }
    }

    return true
}

fun Context.showToast(msg: CharSequence) =
    Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()

/**
 * 隐藏键盘
 */
fun View.hideSoftInput() {
    val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}

/**
 * LiveData 转 Flowable
 */
fun <T> LiveData<T>.toFlowable(owner: LifecycleOwner): Flowable<T> {
    val publisher = LiveDataReactiveStreams.toPublisher(owner, this)
    return Flowable.fromPublisher(publisher)
}

typealias SClick = SystemDialog.OnClickListener?

/**
 * 显示系统对话框 正文-按钮文字
 *
 * @param context     上下文
 * @param msg         正文 如果为空，视图隐藏
 * @param btnText     按钮文字 如果为空，视图隐藏
 * @param btnListener 按钮点击事件 如果为空，点击事件为隐藏对话框
 */
fun Context.showSysDialog(
    msg: String?,
    btnText: String?,
    btnListener: SClick
): SystemDialog {
    return showSysDialog(null, msg, btnText, btnListener)
}

fun Fragment.showSysDialog(
    msg: String?,
    btnText: String?,
    btnListener: SClick
) = requireActivity().showSysDialog(msg, btnText, btnListener)

/**
 * 显示系统对话框 标题-正文-按钮文字
 *
 * @param title       标题
 * @param msg         正文 如果为空，视图隐藏
 * @param btnText     按钮文字 如果为空，视图隐藏
 * @param btnListener 按钮点击事件 如果为空，点击事件为隐藏对话框
 */
fun Context.showSysDialog(
    title: String?,
    msg: String?,
    btnText: String?,
    btnListener: SClick
): SystemDialog {
    return showSysDialog(title, msg, btnText, null, btnListener, null)
}

fun Fragment.showSysDialog(
    title: String?,
    msg: String?,
    btnText: String?,
    btnListener: SClick
) = requireActivity().showSysDialog(title, msg, btnText, btnListener)

/**
 * 显示系统对话框 正文-右边文字-左边文字
 *
 * @param msg              正文 如果为空，视图隐藏
 * @param positiveBtnText  右边按钮文字 如果为空，视图隐藏
 * @param negativeBtnText  左边按钮文字 如果为空，视图隐藏
 * @param positiveListener 右边按钮点击事件 如果为空，点击事件为隐藏对话框
 * @param negativeListener 左边按钮点击事件 如果为空，点击事件为隐藏对话框
 */
fun Context.showSysDialog(
    msg: String?,
    positiveBtnText: String?,
    negativeBtnText: String?,
    positiveListener: SClick,
    negativeListener: SClick
): SystemDialog {
    return showSysDialog(
        null,
        msg,
        positiveBtnText,
        negativeBtnText,
        positiveListener,
        negativeListener
    )
}

fun Fragment.showSysDialog(
    msg: String?,
    positiveBtnText: String?,
    negativeBtnText: String?,
    positiveListener: SClick,
    negativeListener: SClick
) = requireActivity().showSysDialog(
    msg,
    positiveBtnText,
    negativeBtnText,
    positiveListener,
    negativeListener
)

/**
 * 显示系统对话框  标题-正文-右边文字-左边文字
 *
 * @param title            标题 如果为空，视图隐藏
 * @param msg              正文 如果为空，视图隐藏
 * @param positiveBtnText  右边按钮文字 如果为空，视图隐藏
 * @param negativeBtnText  左边按钮文字 如果为空，视图隐藏
 * @param positiveListener 右边按钮点击事件 如果为空，点击事件为隐藏对话框
 * @param negativeListener 左边按钮点击事件 如果为空，点击事件为隐藏对话框
 */
fun Context.showSysDialog(
    title: String?,
    msg: String?,
    positiveBtnText: String?,
    negativeBtnText: String?,
    positiveListener: SClick,
    negativeListener: SClick
): SystemDialog {
    return showSysDialog(
        title,
        null,
        null,
        msg,
        positiveBtnText,
        negativeBtnText,
        positiveListener,
        negativeListener
    )
}

fun Fragment.showSysDialog(
    title: String?,
    msg: String?,
    positiveBtnText: String?,
    negativeBtnText: String?,
    positiveListener: SClick,
    negativeListener: SClick
) = requireActivity().showSysDialog(
    title,
    msg,
    positiveBtnText,
    negativeBtnText,
    positiveListener,
    negativeListener
)


/**
 * 显示系统对话框  标题-标题描述-辅助文字-正文-右边文字-左边文字
 *
 * @param title            标题 如果为空，视图隐藏
 * @param titleMsg         标题描述 如果为空，视图隐藏
 * @param helpMsg          辅助描述字 如果为空，视图隐藏
 * @param msg              正文 如果为空，视图隐藏
 * @param positiveBtnText  右边按钮文字 如果为空，视图隐藏
 * @param negativeBtnText  左边按钮文字 如果为空，视图隐藏
 * @param positiveListener 右边按钮点击事件 如果为空，点击事件为隐藏对话框
 * @param negativeListener 左边按钮点击事件 如果为空，点击事件为隐藏对话框
 * @return
 */
fun Context.showSysDialog(
    title: CharSequence?,
    titleMsg: CharSequence?,
    helpMsg: CharSequence?,
    msg: CharSequence?,
    positiveBtnText: CharSequence?,
    negativeBtnText: CharSequence?,
    positiveListener: SClick,
    negativeListener: SClick
): SystemDialog {
    val dialog = SystemDialog.Builder(this)
        .title(title)
        .titleMsg(titleMsg)
        .helpMsg(helpMsg)
        .message(msg)
        .positiveBtnText(positiveBtnText)
        .negativeBtnText(negativeBtnText)
        .positiveClickListener(positiveListener)
        .negativeClickListener(negativeListener)
        .cancelable(true)
        .create()
    dialog.show()
    return dialog
}

fun Fragment.showSysDialog(
    title: CharSequence?,
    titleMsg: CharSequence?,
    helpMsg: CharSequence?,
    msg: CharSequence?,
    positiveBtnText: CharSequence?,
    negativeBtnText: CharSequence?,
    positiveListener: SClick,
    negativeListener: SClick
) = requireActivity().showSysDialog(
    title,
    titleMsg,
    helpMsg,
    msg,
    positiveBtnText,
    negativeBtnText,
    positiveListener,
    negativeListener
)


fun Context.checkIntentIsUse(intent: Intent) =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

fun Fragment.showToast(msg: CharSequence) = context?.run { showToast(msg) }

fun Bitmap.toBlur(context: Context, radius: Float, scale: Float): Bitmap {
    val blurWidth = Math.round(width * scale)
    val blurHeight = Math.round(height * scale)
    val blurBitmap = Bitmap.createScaledBitmap(this, blurWidth, blurHeight, true)

    val renderScript = RenderScript.create(context.applicationContext)

    val input = Allocation.createFromBitmap(renderScript, blurBitmap)
    val output = Allocation.createTyped(renderScript, input.type)
    ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).run {
        setInput(input)
        setRadius(radius)
        forEach(output)
    }

    output.copyTo(blurBitmap)
    renderScript.destroy()
    return blurBitmap
}


val Throwable.detailMsg: String
    get() {
        val info = StringWriter()
        val pw = PrintWriter(info)

        this.printStackTrace(pw)
        var cause = this.cause

        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }

        val result = info.toString()
        pw.close()
        return result
    }

/**
 * 网络是否可用
 */
val Context.netAvailable: Boolean
    get() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

val Context.deviceSize: IntArray
    get() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return intArrayOf(dm.widthPixels, dm.heightPixels)
    }

val Context.imei: String
    @SuppressLint("MissingPermission")
    get() {
        return try {
            //实例化TelephonyManager对象
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //获取IMEI号
            var imei = ""
            if (checkPermissions(Manifest.permission.READ_PHONE_STATE)) {
                imei = telephonyManager.deviceId
            }
            imei
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

val Context.packageInfo: PackageInfo
    get() = packageManager.getPackageInfo(packageName, 0)

val Context.exCacheDir: File
    get() = if (hasSdCard() || !Environment.isExternalStorageRemovable()) {
        externalCacheDir ?: cacheDir
    } else {
        cacheDir
    }


val View.inflater: LayoutInflater
    get() = LayoutInflater.from(context)