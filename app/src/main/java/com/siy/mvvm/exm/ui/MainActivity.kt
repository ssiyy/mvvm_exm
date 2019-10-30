package com.siy.mvvm.exm.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.utils.NavigationResult
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {
            res.updateConfiguration(Configuration().apply { setToDefaults() }, res.displayMetrics)
        }
        return res
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            if (isShouldHideInput(currentFocus, ev)) {
                hideSoftInput()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     *
     * 这段代码来自于：Using Navigation Architecture Component in a large banking app
     *
     * @param result 返回结果
     * @see <a href="https://medium.com/google-developer-experts/using-navigation-architecture-component-in-a-large-banking-app-ac84936a42c2">Using Navigation Architecture Component in a large banking app</a>
     * @see <a href="https://issuetracker.google.com/issues/79672220">Issue Tracker - Navigation: startActivityForResult analog</a>
     */
    fun navigateBackWithResult(result: Bundle) {
        val childFragmentManager =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as NavigationResult).onNavigationResult(result)
            childFragmentManager.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        navController().popBackStack()
    }

    private fun navController() = Navigation.findNavController(this,
        R.id.nav_host_fragment
    )

    private fun isShouldHideInput(v: View?, event: MotionEvent?) =
        if (v is EditText && event != null) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        } else {
            false
        }

    private fun hideSoftInput() = currentFocus?.let {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(it.windowToken, 0)
        it.clearFocus()
    }
}
