package com.siy.mvvm.exm.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;


/**
 * 文字圆形背景
 *
 * @author Siy
 */
public class CircleBgColorSpan extends ReplacementSpan {

    /**
     * 背景颜色
     */
    private int mColor;

    /**
     * 文字颜色
     */
    private int mTextColor;

    /**
     * 内间距
     */
    private int mPadding;

    /**
     * 左边的外间距
     */
    private int mMarginLeft;

    /**
     * 右边的外间距
     */
    private int mMarginRight;


    /**
     * @param color       背景颜色
     * @param textColor   文字颜色
     * @param marginLeft  左边的外间距
     * @param marginRight 右边的外间距
     * @param padding     内间距
     */
    public CircleBgColorSpan(int color, int textColor, int marginLeft, int marginRight, int padding) {
        this.mColor = color;
        this.mTextColor = textColor;
        this.mMarginLeft = marginLeft;
        this.mMarginRight = marginRight;
        this.mPadding = padding;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float width = 2 * mPadding + paint.measureText(text, start, end);
        if (fm != null) {
            fm.ascent = (int) -width;
            fm.descent = 0;
            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return (int) width + mMarginLeft + mMarginRight;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //设置背景颜色
        paint.setColor(mColor);
        // 设置画笔的锯齿效果
        paint.setAntiAlias(true);

        float startX = x + mMarginLeft;
        float textWidth = paint.measureText(text, start, end);
        float width = 2 * mPadding + textWidth;

        canvas.drawCircle(startX + width / 2, width / 2, width / 2, paint);

        //恢复画笔的文字颜色
        paint.setColor(mTextColor);

        //绘制文字
        Rect rect = new Rect();
        paint.getTextBounds(text.toString(),0,text.length(), rect);

        canvas.drawText(text, start, end, startX + mPadding, width / 2 -(paint.ascent()+paint.descent())/2       ,paint);
    }
}