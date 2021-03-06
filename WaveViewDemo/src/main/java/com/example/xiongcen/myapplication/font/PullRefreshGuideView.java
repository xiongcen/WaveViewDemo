package com.example.xiongcen.myapplication.font;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.xiongcen.myapplication.R;
import com.example.xiongcen.myapplication.util.SystemUtils;

/**
 * Created by xiongcen on 16/10/11.
 */
public class PullRefreshGuideView extends View {

    private static final int LEFT = 0;
    private static final int DASH_LENGTH = 200;
    private static final int RATE_1 = 8;
    private static final int RATE_2 = 16;

    // 画第一张图距上的高度
    private int mTOP;
    // 第一张图的宽高
    private int mBitamp1Width;
    private int mBitamp1Height;
    // 第二张图触发开始点x，y的值，量PSD得出
    private int mBitamp2StartX = 135;
    private int mBitamp2StartY = 23;
    // 第二张图的宽高
    private int mBitamp2Width;
    private int mBitamp2Height;

    // 开始画虚线的x，y坐标
    private int mStartX;
    private int mStartY;
    // 虚线的点y坐标
    private int mY = 0;
    // 虚线结束的点y坐标
    private int mEndY = 0;
    // 反向的标识
    private boolean mReverseFlag = false;

    private int mScreenWidth;

    private boolean mInterruptInvalidate = false;

    private Path mPath = new Path();

    private Paint mPaint = new Paint();

    private PathEffect mEffects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);

    private Bitmap mBitmap1, mBitmap2;

    private RectF mDestRect1, mDestRect2;

    // 执行次数
    private int mCount;

    private Runnable mGoneRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(View.GONE);
        }
    };

    private Runnable mInvalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public PullRefreshGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenWidth = SystemUtils.getScreenWidth(getContext());

        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.biz_pull_refresh_guide_circle);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.biz_pull_refresh_guide_hand);

        mBitamp1Width = mBitmap1.getWidth();
        mBitamp1Height = mBitmap1.getHeight();

        mBitamp2Width = mBitmap2.getWidth();
        mBitamp2Height = mBitmap2.getHeight();

        // Toolbar高度+二级导航高度-第一张图中心点高度
//        mTOP = getResources().getDimensionPixelSize(R.dimen.tool_bar_height)
//                + getResources().getDimensionPixelSize(R.dimen.biz_pager_indicator_height) - mBitamp1Height / 2;
        mTOP = 0;
        mStartX = LEFT + mScreenWidth / 2;
        mStartY = mTOP + mBitamp1Height / 2;
        mEndY = mStartY + DASH_LENGTH;
        mY = mStartY;

        int srcRectLeft1 = (mScreenWidth - mBitamp1Width) / 2 + LEFT;
        int srcRectTop1 = mTOP;
        int srcRectRight1 = srcRectLeft1 + mBitamp1Width;
        int srcRectBottom1 = srcRectTop1 + mBitamp1Height;
        mDestRect1 = new RectF(srcRectLeft1, srcRectTop1, srcRectRight1, srcRectBottom1);


        int srcRectLeft2 = mScreenWidth / 2 - mBitamp2StartX;
        int srcRectTop2 = mBitamp1Height / 2 + mTOP - mBitamp2StartY;
        int srcRectRight2 = srcRectLeft2 + mBitamp2Width;
        int srcRectBottom2 = srcRectTop2 + mBitamp2Height;
        mDestRect2 = new RectF(srcRectLeft2, srcRectTop2, srcRectRight2, srcRectBottom2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPath.moveTo(mStartX, mStartY);
        mPath.lineTo(mStartX, mY);
        mPaint.setPathEffect(mEffects);
        canvas.drawPath(mPath, mPaint);
        mPath.reset();

        canvas.drawBitmap(mBitmap1, null, mDestRect1, mPaint);
        canvas.drawBitmap(mBitmap2, null, mDestRect2, mPaint);

        if (mY >= mEndY) {
            mReverseFlag = true;
        } else if (mY <= mStartY) {
            mReverseFlag = false;
        }

        if (mReverseFlag) {
            mY -= RATE_2;
            float left = mDestRect2.left;
            float top = mDestRect2.top;
            float right = mDestRect2.right;
            float bottom = mDestRect2.bottom;
            mDestRect2.set(left, top - RATE_2, right, bottom - RATE_2);
        } else if (mY < mEndY) {
            mY += RATE_1;
            float left = mDestRect2.left;
            float top = mDestRect2.top;
            float right = mDestRect2.right;
            float bottom = mDestRect2.bottom;
            mDestRect2.set(left, top + RATE_1, right, bottom + RATE_1);
        }

        if (!mInterruptInvalidate) {
            if (mCount < 3) {
                postDelayed(mInvalidateRunnable, 16);
            } else {
                removeCallbacks(mInvalidateRunnable);
                postDelayed(mGoneRunnable, 100);
            }
        } else {
            removeCallbacks(mGoneRunnable);
            removeCallbacks(mInvalidateRunnable);
        }

        if (mReverseFlag && mY <= mStartY) {
            mCount++;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mInterruptInvalidate = true;
        removeCallbacks(mGoneRunnable);
        removeCallbacks(mInvalidateRunnable);
        super.onDetachedFromWindow();
    }
}
