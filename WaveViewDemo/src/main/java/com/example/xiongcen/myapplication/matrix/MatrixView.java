package com.example.xiongcen.myapplication.matrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.example.xiongcen.myapplication.R;
import com.example.xiongcen.myapplication.util.SystemUtils;

/**
 * Created by xiongcen on 17/1/3.
 */

public class MatrixView extends View {

    private Matrix mMatrix = new Matrix();

    private Bitmap mBitmap;

    public MatrixView(Context context) {
        this(context, null);
    }

    public MatrixView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.autumn);
    }

    private float pivotX = SystemUtils.getScreenWidth(getContext()) / 2;
    private float pivotY = SystemUtils.getScreenHeight(getContext()) / 2;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // 1.基于原点缩放
//        mMatrix.reset();
//        mMatrix.postScale(0.5f, 0.5f);
//        canvas.drawBitmap(mBitmap, mMatrix, null);
        // 2.1 (-T*T*S)*P，基于原点缩放
//        mMatrix.reset();
//        mMatrix.preTranslate(-pivotX, -pivotY);
//        mMatrix.postTranslate(pivotX, pivotY);
//        mMatrix.postScale(0.5f, 0.5f);
//        canvas.drawBitmap(mBitmap, mMatrix, null);
        // 2.2 (-T*S*T)*P，基于(pivotX,pivotY)缩放
        mMatrix.reset();
        mMatrix.postScale(0.5f, 0.5f);
        mMatrix.preTranslate(-pivotX, -pivotY);
        mMatrix.postTranslate(pivotX, pivotY);
        canvas.drawBitmap(mBitmap, mMatrix, null);

    }
}
