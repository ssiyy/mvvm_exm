package com.siy.mvvm.exm.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.databinding.MainTabIndicatorLayoutBinding


/**
 * 提供给首页用的切换指示器，
 * <p>
 * Created by Siy on 2018/10/10.
 *
 * @author Siy
 */
class MainIndicator : FrameLayout {

    companion object {

        const val VG1_CODE = 0x00000001

        const val VG2_CODE = 0x00000002


        const val VG4_CODE = 0x00000004

        const val VG5_CODE = 0x00000008


        const val VG_MAIN_CODE = 0x000E
    }

    val stateVg = ObservableField<Int>()

    private var click: ((Int) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        init(context)
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        setBackgroundColor(Color.TRANSPARENT)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        inflater?.let {
            val dataBindding =
                DataBindingUtil.inflate<MainTabIndicatorLayoutBinding>(
                    inflater,
                    R.layout.main_tab_indicator_layout,
                    this,
                    true
                )
            dataBindding.mainIndicator = this
        }
    }

    /**
     * 初始化选择的位置
     */
    fun initPosition(vgCode: Int, click: ((Int) -> Unit)?) {
        stateVg.set(vgCode)
        this.click = click
        when (vgCode) {
            VG1_CODE -> onVg1Click()
            VG2_CODE -> onVg2Click()
            VG4_CODE -> onVg4Click()
            VG5_CODE -> onVg5Click()
            VG_MAIN_CODE -> onVgMainClick()
        }
    }

    fun onVg1Click() {
        stateVg.set(VG1_CODE)
        click?.invoke(VG1_CODE)
    }

    fun onVg2Click() {
        stateVg.set(VG2_CODE)
        click?.invoke(VG2_CODE)
    }


    fun onVg4Click() {
        stateVg.set(VG4_CODE)
        click?.invoke(VG4_CODE)
    }

    fun onVg5Click() {
        stateVg.set(VG5_CODE)
        click?.invoke(VG5_CODE)
    }

    fun onVgMainClick() {
        click?.invoke(VG_MAIN_CODE)
    }

}