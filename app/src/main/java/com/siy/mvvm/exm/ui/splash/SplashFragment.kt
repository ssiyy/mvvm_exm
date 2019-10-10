package com.siy.mvvm.exm.ui.splash

import android.graphics.Color
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import com.siy.mvvm.exm.BuildConfig
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.CrashHandler
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
class SplashFragment(override val layoutId: Int = View.NO_ID) : BaseFragment<ViewDataBinding>() {

    /**
     * 错误日志
     */
    private val errMsg by lazy {
        CrashHandler.carshDetailMsg
    }

    /**
     * 是否显示错误日志
     */
    private val showErrMsg = errMsg.isNotEmpty() && BuildConfig.DEBUG

    @ExperimentalCoroutinesApi
    override fun initViewsAndEvents(view: View) {
        flowOf(0)
            .onStart {
                delay(2 * 1000)
            }
            .catch {
                requireActivity().finish()
            }
            .onCompletion {
                navToLogin()
            }.launchIn(lifecycleScope)

    }

    private fun navToLogin(haveMsg: Boolean = showErrMsg) {
        if (!haveMsg) {
            navController.navigateAnimate(
                SplashFragmentDirections.actionSplashFragmentToLoginFragment(),
                navOptions {
                    popUpTo(R.id.splashFragment) {
                        inclusive = true
                    }
                })
        }
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?) =
        createContentView()

    private fun createContentView(): View {
        val frameLayout = FrameLayout(mContext)
        frameLayout.addView(
            ImageView(mContext).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageResource(R.drawable.splash)
            },
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )


        if (showErrMsg) {
            frameLayout.addView(
                TextView(mContext).apply {
                    setTextColor(Color.parseColor("#3D3636"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                    setBackgroundColor(Color.WHITE)
                    text = errMsg
                    isVerticalScrollBarEnabled = true
                    movementMethod = ScrollingMovementMethod.getInstance()


                },
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            frameLayout.addView(
                TextView(mContext).apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.parseColor("#131212"))
                    text = "跳过"

                    setOnClickListener {
                        navToLogin(false)
                        CrashHandler.clearException()
                    }
                },
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
        return frameLayout
    }
}