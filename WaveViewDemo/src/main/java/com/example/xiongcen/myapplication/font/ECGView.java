package com.example.xiongcen.myapplication.font;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 心电图
 * Created by xiongcen on 16/8/30.
 */
public class ECGView extends View {
    private Paint mPaint;// 画笔
    private Path mPath;// 路径对象

    private int screenW, screenH;// 屏幕宽高
    private float x, y;// 路径初始坐标
    private float initScreenW;// 屏幕初始宽度
    private float initX;// 初始X轴坐标
    private float transX, moveX;// 画布移动的距离

    private boolean isCanvasMove;// 画布是否需要平移

    public ECGView(Context context, AttributeSet set) {
        super(context, set);

        // 禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        /*
         * 实例化画笔并设置属性
         */
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        // 圆角的笔触
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置结合处的形态为圆形
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        // 为绘制的图形添加一个阴影层效果
        /**
         * radius:模糊半径，radius越大越模糊，越小越清晰，但是如果radius设置为0，则阴影消失不见
         * dx:阴影的横向偏移距离，正值向右偏移，负值向左偏移
         * dy:阴影的纵向偏移距离，正值向下偏移，负值向上偏移
         * color: 绘制阴影的画笔颜色，即阴影的颜色（对图片阴影无效）
         */
        mPaint.setShadowLayer(7, 0, 0, Color.RED);

        mPath = new Path();
        transX = 0;
        isCanvasMove = false;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        /*
         * 获取屏幕宽高
         */
        screenW = w;
        screenH = h;

        /*
         * 设置起点坐标
         */
        x = 0;
        y = (screenH / 2) + (screenH / 4) + (screenH / 10);

        // 屏幕初始宽度
        initScreenW = screenW;

        // 初始X轴坐标
        initX = ((screenW / 2) + (screenW / 4));

        moveX = (screenW / 24);

        mPath.moveTo(x, y);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        mPath.lineTo(x, y);

        // 向左平移画布
        canvas.translate(-transX, 0);

        // 计算坐标
        calCoors();

        // 绘制路径
        canvas.drawPath(mPath, mPaint);
        invalidate();
    }

    /**
     * 计算坐标
     */
    private void calCoors() {
        if (isCanvasMove == true) {
            transX += 4;
        }

        if (x < initX) {
            x += 8;
        } else {
            if (x < initX + moveX) {
                x += 2;
                y -= 8;
            } else {
                if (x < initX + (moveX * 2)) {
                    x += 2;
                    y += 14;
                } else {
                    if (x < initX + (moveX * 3)) {
                        x += 2;
                        y -= 12;
                    } else {
                        if (x < initX + (moveX * 4)) {
                            x += 2;
                            y += 6;
                        } else {
                            if (x < initScreenW) {
                                x += 8;
                            } else {
                                isCanvasMove = true;
                                initX = initX + initScreenW;
                            }
                        }
                    }
                }
            }

        }
    }
}
