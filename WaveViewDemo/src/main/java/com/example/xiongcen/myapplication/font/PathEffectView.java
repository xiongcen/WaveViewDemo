package com.example.xiongcen.myapplication.font;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.SumPathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 16/8/30.
 */
public class PathEffectView extends View {
    private float mPhase;// 偏移值
    private Paint mPaint;// 画笔对象
    private Path mPath;// 路径对象
    private PathEffect[] mEffects;// 路径效果数组

    public PathEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
         * 实例化画笔并设置属性
         */
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.DKGRAY);

        // 实例化路径
        mPath = new Path();

        // 定义路径的起点
        mPath.moveTo(0, 0);

        // 定义路径的各个点
        for (int i = 0; i <= 30; i++) {
            mPath.lineTo(i * 35, (float) (Math.random() * 100));
        }

        // 创建路径效果数组
        mEffects = new PathEffect[7];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*
         * 实例化各类特效
         */
        // 没有任何效果的路径
        mEffects[0] = null;
        // radius参数表示转角处的圆滑程度
        mEffects[1] = new CornerPathEffect(50);
        // segmentLength参数表示杂点的密度，值越小杂点越密集，deviation参数表示“杂点”突出的大小，值越大突出的距离越大
        mEffects[2] = new DiscretePathEffect(3.0F, 5.0F);
        // intervals参数只要浮点型数组中元素个数大于等于2即可，phase参数表示偏移值
        mEffects[3] = new DashPathEffect(new float[] { 20, 10, 5, 10 }, mPhase);
        // PathDashPathEffect可自定义虚线样式
        // 正方形样式
        Path path1 = new Path();
        path1.addRect(0, 0, 8, 8, Path.Direction.CCW);
        // 圆圈样式
        Path path2 = new Path();
        path2.addCircle(0, 0, 3, Path.Direction.CCW);
        mEffects[4] = new PathDashPathEffect(path2, 12, mPhase, PathDashPathEffect.Style.ROTATE);
        // 先将路径变成innerpe的效果，再去复合outerpe的路径效果
        mEffects[5] = new ComposePathEffect(mEffects[2], mEffects[4]);
        // 把两种路径效果加起来再作用于路径
        mEffects[6] = new SumPathEffect(mEffects[4], mEffects[3]);

        /*
         * 绘制路径
         */
        for (int i = 0; i < mEffects.length; i++) {
            mPaint.setPathEffect(mEffects[i]);
            canvas.drawPath(mPath, mPaint);

            // 每绘制一条将画布向下平移100个像素
            canvas.translate(0, 100);
        }

        // 刷新偏移值并重绘视图实现动画效果
        mPhase += 1;
        invalidate();
    }
}
