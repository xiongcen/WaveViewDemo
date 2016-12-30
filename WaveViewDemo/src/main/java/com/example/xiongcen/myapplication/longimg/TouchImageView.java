package com.example.xiongcen.myapplication.longimg;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.util.NeteaseLog;
import com.example.xiongcen.myapplication.util.ViewUtils;

import java.util.ArrayList;

/**
 * Created by xiongcen on 16/12/30.
 */

public class TouchImageView extends ImageView {

    private static final String TAG = "TouchImageView";

    private ArrayList<View> mHotAreas;

    public void setHotAreas(ArrayList<View> viewList) {
        mHotAreas = viewList;
    }


    private OnHotAreaClickListener mOnHotAreaClickListener;

    public interface OnHotAreaClickListener {
        void onHotAreaClick(View hotAreaView);
    }

    public void setOnHotAreaClickListener(OnHotAreaClickListener listener) {
        mOnHotAreaClickListener = listener;
    }


    public TouchImageView(Context context) {
        this(context, null);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TouchGestureDetector.OnTouchGestureListener listener = new TouchGestureDetector.OnTouchGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                NeteaseLog.d(TAG, "onSingleTapConfirmed");
                // 确定是单击事件
                if (mHotAreas != null && !mHotAreas.isEmpty()) {
                    for (View v : mHotAreas) {
                        if (ViewUtils.isTouchEventInView(e, v)) {
                            if (mOnHotAreaClickListener != null) {
                                mOnHotAreaClickListener.onHotAreaClick(v);
                            }
                            return true;
                        }
                    }
                }
                return performClick();
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // 长按
                performLongClick();
            }


        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int oldMeasuredWidth = getMeasuredWidth();
        final int oldMeasuredHeight = getMeasuredHeight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        NeteaseLog.d(TAG, "onMeasure measuredWidth:" + measuredWidth + ";measuredHeight:" + measuredHeight);

        if (oldMeasuredWidth != measuredWidth || oldMeasuredHeight != measuredHeight) {
            resetToInitialState();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mDrawable != drawable) {
            mDrawable = drawable;
            if (drawable != null) {
                mDrawableIntrinsicWidth = drawable.getIntrinsicWidth();
                mDrawableIntrinsicHeight = drawable.getIntrinsicHeight();
                resetToInitialState();
            } else {
                mDrawableIntrinsicHeight = 0;
                mDrawableIntrinsicWidth = 0;
            }
        }
    }

    private Drawable mDrawable;
    private int mDrawableIntrinsicWidth;
    private int mDrawableIntrinsicHeight;
    private final Matrix mMatrix = new Matrix();
    private float mMaxScale = 1;

    private void resetToInitialState() {
        mMatrix.reset();
        final float minScale = getMinScale();
        mMatrix.postScale(minScale, minScale);


    }

    private float getMinScale() {
        float minScale = Math.min(getMeasuredWidth() / (float) mDrawableIntrinsicWidth, getMeasuredHeight() / (float) mDrawableIntrinsicHeight);
        NeteaseLog.d(TAG, "getMinScale minScale:" + minScale);
        if (minScale > mMaxScale) {
            minScale = mMaxScale;
        }
        return minScale;
    }
}
