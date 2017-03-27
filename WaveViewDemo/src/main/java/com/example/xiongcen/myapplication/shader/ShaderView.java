package com.example.xiongcen.myapplication.shader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.xiongcen.myapplication.R;
import com.example.xiongcen.myapplication.util.SystemUtils;

/**
 * Created by xiongcen on 17/3/25.
 */

public class ShaderView extends View {

    private static final int RECT_SIZE = 200;// 矩形尺寸的一半

    private Paint mPaint;// 画笔

    private int left, top, right, bottom;// 矩形坐上右下坐标

    public ShaderView(Context context) {
        super(context);
    }

    public ShaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        int screenHeight = SystemUtils.getScreenHeight(context);
        int screenWidth = SystemUtils.getScreenWidth(context);

        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;

        left = screenCenterX - RECT_SIZE;
        top = screenCenterY - RECT_SIZE;
        right = screenCenterX + RECT_SIZE;
        bottom = screenCenterY + RECT_SIZE;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);

        // 设置着色器

        // 第一种
//        mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.MIRROR, Shader.TileMode.CLAMP));

        // 第二种 线性渐变
//        mPaint.setShader(new LinearGradient(left, top, right, bottom, Color.RED, Color.YELLOW, Shader.TileMode.REPEAT));
//        mPaint.setShader(new LinearGradient(left, top, right - RECT_SIZE, bottom - RECT_SIZE, Color.RED, Color.YELLOW, Shader.TileMode.REPEAT));
//        mPaint.setShader(new LinearGradient(left, top, right, bottom, new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE }, new float[] { 0, 0.1F, 0.5F, 0.7F, 0.8F }, Shader.TileMode.MIRROR));

        // 第三种 梯度渐变
//        mPaint.setShader(new SweepGradient(screenCenterX, screenCenterY, Color.RED, Color.YELLOW));
//        mPaint.setShader(new SweepGradient(screenCenterX, screenCenterY, new int[] { Color.GREEN, Color.WHITE, Color.GREEN }, null));

        // 第四种 RadialGradient 径向渐变：圆形中心向四周渐变的效果
        mPaint.setShader(new RadialGradient(screenCenterX, screenCenterY, 100, Color.RED, Color.YELLOW, Shader.TileMode.REPEAT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(left, top, right, bottom, mPaint);
    }
}
