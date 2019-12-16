@file:JvmName("Utils")
@file:JvmMultifileClass

package com.siy.mvvm.exm.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.Uri
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

/**
 * 小米应用商店
 */
private const val MARKET_PKG_NAME_MI = "com.xiaomi.market"

/**
 * 360商店
 */
private const val MARKET_PKG_NAME_360 = "com.qihoo.appstore"
/**
 * vivo商店
 */
private const val MARKET_PKG_NAME_VIVO = "com.bbk.appstore"

/**
 * oppo商店
 */
private const val MARKET_PKG_NAME_OPPO = "com.oppo.market"

/**
 * 应用宝
 */
private const val MARKET_PKG_NAME_YINGYONGBAO = "com.tencent.android.qqdownloader"

/**
 *安智
 */
private const val MARKET_PKG_NAME_ANZHI = "cn.goapk.market"

/**
 * 华为
 */
private const val MARKET_PKG_NAME_HUAWEI = "com.huawei.appmarket"

/**
 * 百度
 */
private const val MARKET_PKG_NAME_BAIDU = "com.baidu.appsearch"

/**
 *历趣
 */
private const val MARKET_PKG_NAME_LIQU = "com.liqucn.android"

/**
 * 搜狗
 */
private const val MARKET_PKG_NAME_SOUGOU = "com.sougou.androidtool"

/**
 * 魅族
 */
private const val MARKET_PKG_NAME_MEIZU = "com.meizu.mstore"


/**
 * 跳转到渠道对应的市场，如果没有该市场，就跳转到应用宝（App或者网页版）
 */
fun Context.goToAppMarket() {
    try {
//        val uri = Uri.parse("market://details?id=$packageName")
        val uri = Uri.parse("market://details?id=" + "com.tencent.mm")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resInfo = packageManager.queryIntentActivities(intent, 0)

        val pkgName = when ("yingyongbao") {
            "normal" -> MARKET_PKG_NAME_YINGYONGBAO
            "baidu" -> MARKET_PKG_NAME_BAIDU
            "huawei" -> MARKET_PKG_NAME_HUAWEI
            "oppo" -> MARKET_PKG_NAME_OPPO
            "qihu360" -> MARKET_PKG_NAME_360
            "vivo" -> MARKET_PKG_NAME_VIVO
            "xiaomi" -> MARKET_PKG_NAME_MI
            "yingyongbao" -> MARKET_PKG_NAME_YINGYONGBAO
            "anzi" -> MARKET_PKG_NAME_ANZHI
            "liqu" -> MARKET_PKG_NAME_LIQU
            "sougou" -> MARKET_PKG_NAME_SOUGOU
            "meizu" -> MARKET_PKG_NAME_MEIZU
            else -> MARKET_PKG_NAME_YINGYONGBAO
        }

        // 筛选指定包名的市场intent
        if (resInfo.size > 0) {
            for (i in resInfo.indices) {
                val resolveInfo = resInfo[i]
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName.toLowerCase() == pkgName) {
                    intent.component = ComponentName(packageName, resolveInfo.activityInfo.name)
                    startActivity(intent)
                    return
                }
            }
        }
        // 未匹配到，跳转到应用宝网页版
        goToYingYongBaoWeb()
    } catch (e: Exception) {
        // 发生异常，跳转到应用宝网页版
        goToYingYongBaoWeb()
    }
}

/**
 * 跳转到应用宝网页版 多客拼团页面
 */
fun Context.goToYingYongBaoWeb() {
//    val url = "https://a.app.qq.com/o/simple.jsp?pkgname=$packageName"
    val url = "https://a.app.qq.com/o/simple.jsp?pkgname=com.tencent.mm"
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}