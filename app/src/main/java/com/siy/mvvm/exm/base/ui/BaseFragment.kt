package com.siy.mvvm.exm.base.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.views.sys.SystemDialog
import kotlin.properties.Delegates


/**
 * 所有Fragment的基类
 *
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    protected lateinit var mContext: Context

    //这么写主要是为了不想用哪个?操作符
    protected var mViewDataBinding: T by Delegates.notNull()

    private var mLoadingDialog: SystemDialog? = null

    protected val navController: NavController
        get() = findNavController()

    /**
     * 是否注册返回按钮，注意如果注册app:defaultNavHost="true"的默认行为就会覆盖
     */
    protected open val regBackPressed = false


    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBackPressed()
    }

    /**
     * 注册返回物理返回按钮
     */
    private fun registerBackPressed() {
        if (regBackPressed) {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                onBackPressed()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getContentViewInternal(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewsAndEvents(view, savedInstanceState)
    }

    private fun createLoadingDialog(msg: CharSequence?): SystemDialog {
        val view = layoutInflater.inflate(R.layout.include_loading_dialog, null, false)
        val textView = view.findViewById<TextView>(R.id.tv_loading_tip)
        if (msg.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = msg
        }

        return SystemDialog.Builder(requireActivity())
            .contentView(view)
            .cancelable(true)
            .height(ViewGroup.LayoutParams.WRAP_CONTENT)
            .width(ViewGroup.LayoutParams.WRAP_CONTENT)
            .create()
    }

    protected fun showLoadingDialog(msg: CharSequence? = null) {
        if (mLoadingDialog?.isDismiss != false) {
            mLoadingDialog = createLoadingDialog(msg)
            mLoadingDialog?.show()
        } else {
            mLoadingDialog?.contentView?.apply {
                val tv = findViewById<TextView>(R.id.tv_loading_tip)
                tv.text = msg
            }
        }
    }

    /**
     * 返回按钮
     */
    protected open fun onBackPressed() = Unit

    protected fun hideLoadingDialog() = mLoadingDialog?.dismissAllowingStateLoss()

    protected abstract fun initViewsAndEvents(view: View)

    protected open fun initViewsAndEvents(view: View, savedInstanceState: Bundle?) {
        initViewsAndEvents(view)
    }

    private fun getContentViewInternal(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = if (layoutId != View.NO_ID) {
            inflater.inflate(layoutId, container, false)
        } else {
            getContentView(inflater, container)
        }

        val dataBinding = try {
            DataBindingUtil.bind<T>(view)
        } catch (e: Exception) {
            null
        } catch (e: Throwable) {
            throw e
        }

        return if (dataBinding != null) {
            dataBinding.lifecycleOwner = this
            mViewDataBinding = dataBinding
            mViewDataBinding.root
        } else {
            view
        }
    }

    /**
     * layoutId为View.NO_ID时用这个传递View
     */
    protected open fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
        FrameLayout(mContext)

}

/**
 * 带有默认动画的navigate
 */
fun NavController.navigateAnimate(
    @NonNull directions: NavDirections, @Nullable options: NavOptions = navOptions {
        anim {
            enter = R.anim.slide_right_in
            exit = R.anim.slide_left_out
            popEnter = R.anim.slide_left_in
            popExit = R.anim.slide_right_out
        }
    }
) {
    navigate(directions, navOptions {
        launchSingleTop = options.shouldLaunchSingleTop()

        popUpTo(options.popUpTo) {
            inclusive = options.isPopUpToInclusive
        }

        anim {
            enter = R.anim.slide_right_in
            exit = R.anim.slide_left_out
            popEnter = R.anim.slide_left_in
            popExit = R.anim.slide_right_out
        }
    })
}