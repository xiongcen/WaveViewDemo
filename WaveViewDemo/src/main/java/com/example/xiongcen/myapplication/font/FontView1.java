package com.example.xiongcen.myapplication.font;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 16/8/27.
 */
public class FontView1 extends View {
    private static final String TEXT = "ap爱哥ξτβбпшㄎㄊ";
    private Paint textPaint, linePaint;// 文本的画笔和中心线的画笔

    private int baseX, baseY;// Baseline绘制的XY坐标

    private Paint.FontMetrics mFontMetrics;// 文本测量对象

    public FontView1(Context context) {
        this(context, null);
    }

    public FontView1(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化画笔
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 实例化画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70);
        textPaint.setColor(Color.BLACK);

        mFontMetrics = textPaint.getFontMetrics();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 计算Baseline绘制的起点X轴坐标
        baseX = (int) (canvas.getWidth() / 2 - textPaint.measureText(TEXT) / 2);

        // 计算Baseline绘制的Y坐标
        /****** 大多数情况下我们没有考虑top和bottom ******/
        // 文字以baseline开始绘制，所以要将内容移至中线，需要让文字下移。错误认知是下移ascent/2，但不要忽略了descent
        baseY = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
        // baseline和中线在一条线上，错误的实现
//        baseY = canvas.getHeight() / 2;
        // 1.第一种实现方法
//        canvas.drawText(TEXT, baseX, baseY, textPaint);
        // 2.第二种实现方法
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(TEXT, canvas.getWidth() / 2, baseY, textPaint);

        // 为了便于理解我们在画布中心处绘制一条中线
        canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, linePaint);
    }
}
