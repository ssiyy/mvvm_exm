package com.siy.mvvm.exm.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

interface OnBarItemClick {
    fun click()
}


/**
 *  自定一个bar
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


    /**
     * Pair-1 color
     * Pair-2 数值
     */
    var dataColors: List<Pair<Int, Float>>? = null
        set(values) {
            field = values
            paints = values?.map {
                Paint().apply {
                    color = it.first
                }
            }

            val dataSum = (dataColors?.sumByDouble {
                it.second.toDouble()
            } ?: 0.0).toFloat()

            total = if (total ?: 0f < dataSum) {
                dataSum
            } else {
                total
            }

            invalidateView()
        }

    var total: Float? = null
        set(value) {
            val dataSum = (dataColors?.sumByDouble {
                it.second.toDouble()
            } ?: 0.0).toFloat()

            field = if (value ?: 0f < dataSum) {
                dataSum
            } else {
                value
            }
            invalidateView()
        }


    private val rectfs: MutableList<RectF> = mutableListOf()

    /**
     * 绘制文字的颜色
     */
    var valuesColor = Color.WHITE
        set(value) {
            field = value
            valuesPaint.color = value
            invalidateView()
        }

    /**
     * 绘制文字的大小
     */
    var valuesSize = 16f
        set(value) {
            field = value
            valuesPaint.textSize = value
            invalidateView()
        }


    private val valuesPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onBarItemClick: OnBarItemClick? = null


    init {
        valuesPaint.color = valuesColor

        valuesPaint.textSize = valuesSize
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {


            rectfs.forEachIndexed { index, rectF ->
                if (rectF.contains(event.x, event.y)) {
                    Log.e("siy", "values:${dataColors?.get(index)?.second}")
                }
            }


        }



        return super.onTouchEvent(event)
    }

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

        rectfs.clear()
        dataColors?.forEachIndexed { index, pair ->
            val rate = pair.second / (total ?: 100f)
            if (index == 0) {
                rectfs.add(index, RectF(0f, 0f, w * rate, h.toFloat()))
            } else {
                val lastR = rectfs[index - 1]
                rectfs.add(index, RectF(lastR.right, 0f, lastR.right + w * rate, h.toFloat()))
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        rectfs.forEachIndexed { index, rectF ->
            paints?.let {
                //绘制bar
                canvas?.drawRect(rectF, it[index])

                //文字文字
                //bar的宽度
                val barWidth = rectF.right - rectF.left
                //需要绘制的文字
                val drawValue = dataColors?.get(index)?.second.toString()
                //需要绘制文字的宽度
                val drawValueWidth = valuesPaint.measureText(drawValue)
                //如果需要绘制文字的宽度大于bar的宽度，就不绘制
                if (drawValueWidth < barWidth) {
                    val cy = (rectF.bottom - rectF.top) / 2f
                    val cx = rectF.left + (rectF.right - rectF.left) / 2f
                    val offsetY = (valuesPaint.descent() + valuesPaint.ascent()) / 2
                    val offsetX = drawValueWidth / 2
                    canvas?.drawText(
                        drawValue,
                        0,
                        drawValue.length,
                        cx - offsetX,
                        cy - offsetY,
                        valuesPaint
                    )
                }
            }
        }
    }
}