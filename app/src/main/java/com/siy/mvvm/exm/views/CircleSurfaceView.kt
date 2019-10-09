package com.siy.mvvm.exm.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * 圆形SurfaceView
 */
class CircleSurfaceView : SurfaceView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun draw(canvas: Canvas) {
        Path().run {
            addCircle(width / 2.0f, height / 2.0f, width / 2.0f, Path.Direction.CCW)
            if (Build.VERSION.SDK_INT >= 28) {
                canvas.clipPath(this)
            } else {
                canvas.clipPath(this, Region.Op.REPLACE)
            }
        }
        super.draw(canvas)
    }
}
