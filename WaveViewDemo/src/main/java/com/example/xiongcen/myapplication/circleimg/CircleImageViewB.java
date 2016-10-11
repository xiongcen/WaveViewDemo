package com.example.xiongcen.myapplication.circleimg;

/**
 * 用xfermode实现的各种形状
 * Created by zhaonan on 16/8/23.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageViewB extends ImageView {


    private Bitmap bitmap;
    private PaintFlagsDrawFilter mPdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Paint mPaint = new Paint();
    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);// 设置画笔的锯齿效果
    }

    private Bitmap mDstB = null;
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    public CircleImageViewB(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CircleImageViewB(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CircleImageViewB(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap makeDst(int w, int h) {
        // 只是为了要一个形状 用ALPHA_8可以极大的省内存
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        c.drawCircle(w / 2, h / 2, w / 2, p);
        // Bitmap.Config.ALPHA_8 测试内存使用
        //Toast.makeText(getContext(), ""+bm.getByteCount(), Toast.LENGTH_SHORT).show();
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == bitmap) {
            return;
        }

        if (null == mDstB) {
            mDstB = makeDst(getWidth(), getHeight());
        }
        canvas.save();
        canvas.setDrawFilter(mPdf);
        canvas.drawBitmap(mDstB, 0, 0, mPaint);
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);

        // 蒙层
        //drawNightColor(canvas, true);

        canvas.restore();

        /** canvas的save和restore最好证明，去除两个方法会画出不一样的效果
        int px = getMeasuredWidth();
        int py = getMeasuredWidth();

        // Draw background
        canvas.drawRect(0, 0, px, py, mPaint);

        canvas.save();
        canvas.rotate(90, px/2, py/2);

        // Draw up arrow
        canvas.drawLine(px / 2, 0, 0, py / 2, mPaint);
        canvas.drawLine(px / 2, 0, px, py / 2, mPaint);
        canvas.drawLine(px / 2, 0, px / 2, py, mPaint);

        canvas.restore();

        // Draw circle
        canvas.drawCircle(px - 10, py - 10, 10, mPaint);
         */
    }


    private void drawNightColor(Canvas canvas, boolean isCircle) {
        if(isCircle) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.parseColor("#99000000"));
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getHeight() / 2, p);
        }
        else {
            canvas.drawColor(Color.parseColor("#99000000"));
        }
    }

}
