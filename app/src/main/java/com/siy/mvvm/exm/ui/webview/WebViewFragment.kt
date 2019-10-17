package com.siy.mvvm.exm.ui.webview

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.navArgs
import com.siy.mvvm.exm.base.ui.BaseFragment
import com.siy.mvvm.exm.databinding.FragmentWebviewLayoutBinding
import com.siy.mvvm.exm.views.header.CommonHeader

fun getFixedContext(context: Context): Context {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        context.createConfigurationContext(Configuration())
    } else {
        context
    }
}

class FixWebView : WebView {
    constructor(context: Context) : super(getFixedContext(context))

    constructor(context: Context, attrs: AttributeSet) : super(getFixedContext(context), attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        getFixedContext(
            context
        ), attrs, defStyleAttr
    )
}

    /**
     * Created by Siy on 2019/10/17.
     *
     * @author Siy
     */
    class WebViewFragment(
        override val layoutId: Int = com.siy.mvvm.exm.R.layout.fragment_webview_layout,
        override val regBackPressed: Boolean = true
    ) :
        BaseFragment<FragmentWebviewLayoutBinding>() {

        private val arg by navArgs<WebViewFragmentArgs>()


        private val webView by lazy {
            FixWebView(mContext).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        mViewDataBinding?.progressBar?.visibility = View.VISIBLE
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        mViewDataBinding?.progressBar?.visibility = View.GONE
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        mViewDataBinding?.progressBar?.progress = newProgress
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        title?.let {
                            mViewDataBinding?.header?.title?.value = it
                        }
                    }
                }
            }


        }

        override fun initViewsAndEvents(view: View) {
            mViewDataBinding?.run {
                header = object : CommonHeader() {
                    init {
                        title.value = "加载中..."
                    }

                    override fun onBackClick() {
                        navController.popBackStack()
                    }
                }
                progressBar.progressDrawable =
                    AppCompatResources.getDrawable(
                        mContext,
                        com.siy.mvvm.exm.R.drawable.color_progressbar
                    )

                webViewRoot.addView(
                    webView, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
            }

            webView.loadUrl(arg.url)
        }

        override fun onBackPressed() {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                navController.popBackStack()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            mViewDataBinding?.webViewRoot?.run {
                removeAllViews()
            }
            webView.run {
                clearCache(true)
                removeAllViews()
                clearHistory()
                clearFormData()
                destroy()
            }

        }
    }