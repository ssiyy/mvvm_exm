package com.siy.mvvm.exm.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.siy.mvvm.exm.ui.MainActivity


/**
 * Created by Siy on 2019/08/26.
 *
 * @author Siy
 */
interface NavigationResult {
    fun onNavigationResult(result: Bundle)
}

fun Fragment.popBackForResult(result: Bundle) {
    val activity = requireActivity()
    if (activity is MainActivity) {
        activity.navigateBackWithResult(result)
    }
}