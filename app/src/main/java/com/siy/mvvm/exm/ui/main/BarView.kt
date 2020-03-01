package com.siy.mvvm.exm.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.util.AttributeSet
import android.view.View


/**
 *
 * Created by Siy on  2020/02/28.
 *
 * @author Siy
 */
class BarView @JvmOverloads constructor(
    context: Context,
    set: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, set, defStyle) {

    /**
     * 画笔
     */
    private var paints: List<Paint>? = null

    var dataColors: List<Pair<Int, Float>>? = null
        set(values) {
            field = values
            paints = values?.map {
                Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    color = it.first
                    style = Paint.Style.FILL
                }
            }
        }

    var total: Float? = null
        set(value) {
//            field = value


         val a=    dataColors?.sumBy {
                it.second
            }
            invalidate()
        }

    private val rectfs: MutableList<RectF> = mutableListOf()


    /**
     * 通知重新绘制视图
     */
    private fun invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        dataColors?.forEachIndexed { index, pair ->
            val rate = pair.second / (total ?: 100f)
            rectfs.clear()
            if (index == 0) {
                rectfs.add(0, RectF(0f, 0f, w * rate, h.toFloat()))
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        rectfs.forEachIndexed { index, rectF ->
            canvas?.drawRect(rectF, paints!![index])
        }
    }
}