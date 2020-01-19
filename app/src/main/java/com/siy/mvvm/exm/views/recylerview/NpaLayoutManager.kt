package com.siy.mvvm.exm.views.recylerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager


/**
 * No Predictive Animations LinearLayoutManager
 *
 *
 * 解决方法来自于 https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position/47324896#47324896
 *
 * Created by Siy on  2019/12/18.
 *
 * @author Siy
 */
open class NpaLinearLayoutManager : LinearLayoutManager {

    @Suppress("unused")
    constructor(context: Context) : super(context)

    @Suppress("unused")
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}

class NpaGridLayoutManager : GridLayoutManager {

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    @Suppress("unused")
    constructor(context: Context?, spanCount: Int) : super(context, spanCount)

    @Suppress("unused")
    constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout)

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}