package com.example.xiongcen.myapplication.circleimg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.R;


/**
 * 用clipPath实现的各种形状
 * Created by zhaonan on 16/8/23.
 */

public class CircleImageViewA extends View {

    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Path mPath = new Path();
    private int mPathType = 0;
    private Paint mPaint = new Paint();
    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
    }
    private Bitmap mBitmap;

    public void setPathType(int type) {
        this.mPathType = type;
        invalidate();
    }

    public CircleImageViewA(Context context) {
        super(context);
        init(context, null);
    }

    public CircleImageViewA(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleImageViewA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pathType);
            mPathType = ta.getInteger(R.styleable.pathType_path_type, 0);
        }


        try {
            // 4.1 版本不设置 没有效果
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setImageBitmap(Drawable drawable) {
        this.mBitmap = ((BitmapDrawable) drawable).getBitmap();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap == null) {
            super.onDraw(canvas);
            return;
        }

        canvas.save();
        canvas.setDrawFilter(pdf); // 据说有消除锯齿的效果 实际然并卵
        mPath.reset();
        //mPath.addCircle(getWidth() / 2, getWidth() / 2, getHeight() / 2, Path.Direction.CCW);
        canvas.clipPath(mPath); // makes the clip empty
        processPathType();
        canvas.clipPath(mPath, Region.Op.REPLACE);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.restore();
    }
    // Region.Op.REPLACE//显示第二次的
    // Region.Op.UNION////全部显示
    // Region.Op.XOR//补集，就是全集的减去交集剩余部分显示
    // Region.Op.INTERSECT//是交集显示
    // Region.Op.REVERSE_DIFFERENCE//第二次不同于第一次的部分显示
    // Region.Op.DIFFERENCE//是第一次不同于第二次的部分显示出来


    private void processPathType() {
        switch (mPathType) {
            case 0:
                // 画圆形
                mPath.addCircle(getWidth() / 2, getWidth() / 2, getHeight() / 2, Path.Direction.CCW);
                break;
            case 1:
                buildPath1(mPath);
                break;
            case 2:
                buildPath2(mPath);
                break;
            case 3:
                buildPath3(mPath);
                break;
            case 4:
                buildPath4(mPath);
                break;
        }
    }

    /**
     * 画圆角矩形
     *
     * @param path
     */
    private void buildPath1(Path path) {
        RectF mRoundRect = new RectF();
        mRoundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        int x_radius = 200;
        int y_radius = 200;
        mPath.addRoundRect(mRoundRect, x_radius, y_radius, Path.Direction.CCW);
    }


    /**
     * 三角形
     *
     * @param path
     */
    private void buildPath2(Path path) {
        path.moveTo(getMeasuredWidth() / 2, 0); // 顶点
        path.lineTo(0, getMeasuredHeight()); // 左下角
        path.lineTo(getMeasuredWidth(), getMeasuredHeight()); // 右下角
        path.close();
    }

    /**
     * 梯形
     *
     * @param path
     */
    private void buildPath3(Path path) {
        path.moveTo(getMeasuredWidth() / 4, 0);// 上左
        path.lineTo(getMeasuredWidth() / 4 * 3, 0);// 上右
        path.lineTo(getMeasuredWidth(), getMeasuredHeight());// 下右
        path.lineTo(0, getMeasuredHeight());// 下左
        path.close();
    }

    /**
     * 上下三角
     *
     * @param path
     */
    private void buildPath4(Path path) {
        path.moveTo(getMeasuredWidth() / 4, 0);// 上左
        path.lineTo(getMeasuredWidth() / 4 * 3, 0);// 上右
        path.lineTo(0, getMeasuredHeight());// 下左
        path.lineTo(getMeasuredWidth(), getMeasuredHeight());// 下右
        path.close();
    }

    public static class CircleImageViewB extends ImageView {


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
}