package com.example.xiongcen.myapplication.porter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongcen on 16/9/28.
 */
public class PorterView2 extends View {

    private Paint paint = new Paint();

    public PorterView2(Context context) {
        super(context);
    }

    public PorterView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置背景色
//        canvas.drawARGB(255, 139, 197, 186);

        int canvasWidth = canvas.getWidth();
        int r = canvasWidth / 3;
        //正常绘制黄色的圆形
        paint.setColor(0xFFFFCC44);
        canvas.drawCircle(r, r, r, paint);
        //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setColor(0xFF66AAFF);
        canvas.drawRect(r, r, r * 2.7f, r * 2.7f, paint);
        //最后将画笔去除Xfermode
        paint.setXfermode(null);
    }
}
