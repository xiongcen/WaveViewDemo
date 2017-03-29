package com.example.xiongcen.myapplication.shader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 17/3/29.
 */

public class LayerView extends View {
    private Paint mPaint;// 画笔对象

    private int mViewWidth, mViewHeight;// 控件宽高

    public LayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 实例化画笔对象并设置其标识值
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        /*
         * 获取控件宽高
         */
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // ①绘制一个红色的大矩形再保存画布绘制了一个蓝色的小矩形
//        /*
//         * 绘制一个红色矩形
//         */
//        mPaint.setColor(Color.RED);
//        canvas.drawRect(mViewWidth / 2F - 200, mViewHeight / 2F - 200, mViewWidth / 2F + 200, mViewHeight / 2F + 200, mPaint);
//
//        /*
//         * 保存画布并绘制一个蓝色的矩形
//         */
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);


        // ②两个矩形都顺时针旋转30°
//        /*
//         * 绘制一个红色矩形
//         */
//        canvas.rotate(30);
//        mPaint.setColor(Color.RED);
//        canvas.drawRect(mViewWidth / 2F - 200, mViewHeight / 2F - 200, mViewWidth / 2F + 200, mViewHeight / 2F + 200, mPaint);
//
//        /*
//         * 保存画布并绘制一个蓝色的矩形
//         */
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);


        // ③只针对蓝色的矩形进行了旋转
//        /*
//        * 保存画布并绘制一个蓝色的矩形
//        */
//        canvas.save();
//        mPaint.setColor(Color.BLUE);
//
//        // 旋转画布
//        canvas.rotate(30);
//        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);
//        canvas.restore();
//
//        /*
//        * 绘制一个红色矩形
//        */
//        mPaint.setColor(Color.RED);
//        canvas.drawRect(mViewWidth / 2F - 200, mViewHeight / 2F - 200, mViewWidth / 2F + 200, mViewHeight / 2F + 200, mPaint);

        // ④使用saveLayer只针对蓝色的矩形进行了旋转
//        /*
//        * 绘制一个红色矩形
//        */
//        mPaint.setColor(Color.RED);
//        canvas.drawRect(mViewWidth / 2F - 200, mViewHeight / 2F - 200, mViewWidth / 2F + 200, mViewHeight / 2F + 200, mPaint);
//        /*
//        * 保存画布并绘制一个蓝色的矩形
//        */
//        canvas.saveLayer(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, null, Canvas.ALL_SAVE_FLAG);
//
//        mPaint.setColor(Color.GREEN);
//        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);
//
//        // 旋转画布
//        canvas.rotate(45);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);
//        canvas.restore();

        // ⑤使用saveLayer的返回值
        /*
        * 保存并裁剪画布填充黄色
        */
        int saveID1 = canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mViewWidth / 2F - 300, mViewHeight / 2F - 300, mViewWidth / 2F + 300, mViewHeight / 2F + 300);
        canvas.drawColor(Color.YELLOW);

        /*
        * 保存并裁剪画布填充绿色
        */
        int saveID2 = canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mViewWidth / 2F - 200, mViewHeight / 2F - 200, mViewWidth / 2F + 200, mViewHeight / 2F + 200);
        canvas.drawColor(Color.GREEN);

        /*
        * 保存画布并旋转后绘制一个蓝色的矩形
        */
        int saveID3 = canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(5);
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(mViewWidth / 2F - 100, mViewHeight / 2F - 100, mViewWidth / 2F + 100, mViewHeight / 2F + 100, mPaint);
        canvas.restoreToCount(saveID1);

        mPaint.setColor(Color.CYAN);
        canvas.drawRect(mViewWidth / 2F, mViewHeight / 2F, mViewWidth / 2F + 400, mViewHeight / 2F + 400, mPaint);
    }
}
