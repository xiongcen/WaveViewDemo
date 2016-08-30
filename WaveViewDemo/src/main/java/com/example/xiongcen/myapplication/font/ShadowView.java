package com.example.xiongcen.myapplication.font;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * 类似BlurMaskFilterView的效果
 * Created by xiongcen on 16/8/30.
 */
public class ShadowView extends View {
    private static final int RECT_SIZE = 500;// 方形大小
    private Paint mPaint;// 画笔

    private int left, top, right, bottom;// 绘制时坐标

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // setShadowLayer不支持HW
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 初始化画笔
        initPaint();

        // 初始化资源
        initRes(context);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 实例化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        // radius表示阴影的扩散半径，而dx和dy表示阴影平面上的偏移值，shadowColor阴影颜色
        mPaint.setShadowLayer(10, 5, 5, Color.DKGRAY);
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context) {
        /*
         * 计算位图绘制时左上角的坐标使其位于屏幕中心
         */
        left = MeasureUtil.getScreenSize((Activity) context)[0] / 2 - RECT_SIZE / 2;
        top = MeasureUtil.getScreenSize((Activity) context)[1] / 2 - RECT_SIZE / 2;
        right = MeasureUtil.getScreenSize((Activity) context)[0] / 2 + RECT_SIZE / 2;
        bottom = MeasureUtil.getScreenSize((Activity) context)[1] / 2 + RECT_SIZE / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 先绘制位图
        canvas.drawRect(left, top, right, bottom, mPaint);
    }
}
