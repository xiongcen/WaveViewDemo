package com.example.xiongcen.myapplication.shader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 17/3/27.
 */

public class MultiCricleView extends View {

    public MultiCricleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaint(context);
    }

    private Paint strokePaint, textPaint, arcPaint;// 描边画笔和文字画笔

    private int size;// 控件边长

    private static final float STROKE_WIDTH = 1F / 256F, // 描边宽度占比
            SPACE = 1F / 64F,// 大圆小圆线段两端间隔占比
            LINE_LENGTH = 3F / 32F, // 线段长度占比
            CRICLE_LARGER_RADIU = 3F / 32F,// 大圆半径
            CRICLE_SMALL_RADIU = 5F / 64F,// 小圆半径
            ARC_RADIU = 1F / 8F,// 弧半径
            ARC_TEXT_RADIU = 5F / 32F;// 弧围绕文字半径


    private float strokeWidth;// 描边宽度
    private float ccX, ccY;// 中心圆圆心坐标
    private float largeCricleRadiu, smallCricleRadiu;// 大圆半径和小圆半径
    private float lineLength;// 线段长度
    private float space;// 大圆小圆线段两端间隔
    private float textOffsetY;// 文本的Y轴偏移值


    private void initPaint(Context context) {
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

        /*
         * 初始化文字画笔
         */
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textOffsetY = (textPaint.descent() + textPaint.ascent()) / 2;

        arcPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 获取控件边长
        size = w;

        // 参数计算
        calculation();
    }

    private void calculation() {
        // 计算描边宽度
        strokeWidth = STROKE_WIDTH * size;

        // 计算大圆半径
        largeCricleRadiu = size * CRICLE_LARGER_RADIU;

        // 计算小圆半径
        smallCricleRadiu = size * CRICLE_SMALL_RADIU;

        // 计算线段长度
        lineLength = size * LINE_LENGTH;

        // 计算大圆小圆线段两端间隔
        space = size * SPACE;

        // 计算中心圆圆心坐标
        ccX = size / 2;
        ccY = size / 2 + size * CRICLE_LARGER_RADIU;

        // 设置参数
        setPara();

    }

    /**
     * 设置参数
     */
    private void setPara() {
        // 设置描边宽度
        strokePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制背景
        canvas.drawColor(0xFFF29B76);

        // 绘制中心圆
        canvas.drawCircle(ccX, ccY, largeCricleRadiu, strokePaint);
        canvas.drawText("Fruit", ccX, ccY - textOffsetY, textPaint);

        // 绘制左上方图形
        drawTopLeft(canvas);

        // 绘制右上方图形
        drawTopRight(canvas);

        // 绘制左下方图形
        drawBottomLeft(canvas);

        // 绘制下方图形
        drawBottom(canvas);

        // 绘制右下方图形
        drawBottomRight(canvas);
    }

    private void drawTopLeft(Canvas canvas) {
        canvas.save();

        canvas.translate(ccX, ccY);
        canvas.rotate(-30);

        canvas.drawLine(0, -largeCricleRadiu, 0, -lineLength * 2, strokePaint);
        canvas.drawCircle(0, -lineLength * 3, largeCricleRadiu, strokePaint);

        canvas.drawText("Apple", 0, -lineLength * 3 - textOffsetY, textPaint);

        canvas.drawLine(0, -largeCricleRadiu * 4, 0, -lineLength * 5, strokePaint);
        canvas.drawCircle(0, -lineLength * 6, largeCricleRadiu, strokePaint);

        canvas.drawText("Orange", 0, -lineLength * 6 - textOffsetY, textPaint);

        canvas.restore();
    }

    private void drawTopRight(Canvas canvas) {
        canvas.save();

        canvas.translate(ccX, ccY);
        canvas.rotate(45);

        canvas.drawLine(0, -largeCricleRadiu, 0, -lineLength * 2, strokePaint);
        canvas.drawCircle(0, -lineLength * 3, largeCricleRadiu, strokePaint);

        canvas.drawText("Tropical", 0, -lineLength * 3 - textOffsetY, textPaint);

        // 画弧形
        drawTopRightArc(canvas, -lineLength * 3);

        canvas.restore();
    }

    private void drawTopRightArc(Canvas canvas, float cricleY) {
        canvas.save();

        canvas.translate(0, cricleY);
        canvas.rotate(-45);

        float arcRadiu = size * ARC_RADIU;

        RectF oval = new RectF(-arcRadiu, -arcRadiu, arcRadiu, arcRadiu);

        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setColor(0x55EC6941);
        canvas.drawArc(oval, -22.5F, -135, true, arcPaint);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(Color.WHITE);
        canvas.drawArc(oval, -22.5F, -135, false, arcPaint);

        canvas.restore();
    }

    private void drawBottomLeft(Canvas canvas) {
        canvas.save();

        canvas.translate(ccX, ccY);
        canvas.rotate(-100);

        canvas.drawLine(0, -largeCricleRadiu - space, 0, -lineLength * 2 - space, strokePaint);
        canvas.drawCircle(0, -lineLength * 2 - smallCricleRadiu - space * 2, smallCricleRadiu, strokePaint);

        canvas.drawText("Banana", 0, -lineLength * 2 - smallCricleRadiu - space * 2 - textOffsetY, textPaint);

        canvas.restore();
    }

    private void drawBottom(Canvas canvas) {
        canvas.save();

        canvas.translate(ccX, ccY);
        canvas.rotate(180);

        canvas.drawLine(0, -largeCricleRadiu - space, 0, -lineLength * 2 - space, strokePaint);
        canvas.drawCircle(0, -lineLength * 2 - smallCricleRadiu - space * 2, smallCricleRadiu, strokePaint);
        canvas.drawText("Cucumber", 0, -lineLength * 2 - smallCricleRadiu - space * 2 - textOffsetY, textPaint);

        canvas.restore();
    }

    private void drawBottomRight(Canvas canvas) {
        canvas.save();

        canvas.translate(ccX, ccY);
        canvas.rotate(100);

        canvas.drawLine(0, -largeCricleRadiu - space, 0, -lineLength * 2 - space, strokePaint);
        canvas.drawCircle(0, -lineLength * 2 - smallCricleRadiu - space * 2, smallCricleRadiu, strokePaint);
        canvas.drawText("Vibrators", 0, -lineLength * 2 - smallCricleRadiu - space * 2 - textOffsetY, textPaint);

        canvas.restore();
    }
}
