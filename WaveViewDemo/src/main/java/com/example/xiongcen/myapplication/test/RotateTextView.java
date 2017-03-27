package com.example.xiongcen.myapplication.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.xiongcen.myapplication.util.SystemUtils;

/**
 * Created by xiongcen on 17/3/25.
 */

public class RotateTextView extends TextView {

    private int screenWidth;
    private int screenHeight;

    private Paint mPaint = new Paint();

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        screenWidth = SystemUtils.getScreenWidth(context);
        screenHeight = SystemUtils.getScreenHeight(context);
        mPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(90);
        canvas.translate(100, -getHeight());
        super.onDraw(canvas);
    }
}
