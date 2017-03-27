package com.example.xiongcen.myapplication.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 17/3/26.
 */

public class RotateArrow extends View {

    private Paint mLinePaint = new Paint();
    private Paint mBackgroundPaint = new Paint();

    private int width = 500;
    private int height = 500;

    public RotateArrow(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLinePaint.setStrokeWidth(4);
        mLinePaint.setColor(Color.RED);

        mBackgroundPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, width, height, mBackgroundPaint);

        canvas.save();

        canvas.rotate(90, width / 2, height / 2);

        canvas.drawLine(width / 2, 0, 0, height / 2, mLinePaint);
        canvas.drawLine(width / 2, 0, width, height / 2, mLinePaint);
        canvas.drawLine(width / 2, 0, width / 2, height, mLinePaint);

        canvas.restore();

        canvas.drawCircle(width - 100, height - 100, 30, mLinePaint);
    }
}
